package com.spider.fetcher;

import com.alibaba.dubbo.common.utils.ConcurrentHashSet;
import com.spider.config.ProxyConstant;
import com.spider.config.RemoteConstant;
import com.spider.config.ServerConstant;
import com.spider.sequence.Sequence;
import com.spider.sequence.impl.SnowflakeSequence;
import com.spider.util.RemoteServiceUtil;
import com.spider.util.ThreadUtils;
import com.zhoukai.service.monitor.ProcessService;
import com.zhoukai.status.ProcessStatusEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 抓取器抽象类
 */
public abstract class AbstractFetcher implements Fetcher {
    protected final Logger LOG = LoggerFactory.getLogger(getClass());
    protected final static ExecutorService FETCHEREXECUTOR = ThreadUtils.FETCHEREXECUTOR;
    protected final static ExecutorService FETCHER_RESULT_EXECUTOR = ThreadUtils.FETCHER_RESULT_EXECUTOR;
    protected final static ConcurrentLinkedQueue<FutureModel> FUTURES = new ConcurrentLinkedQueue<FutureModel>();
    protected final static Sequence SEQUENCE = SnowflakeSequence.getSequence();

    protected final AtomicInteger THREAD_ACTIVE_NUMBER = new AtomicInteger(0); // 线程激活数量，用来统计结果线程的统计

    /**
     * 记录正在执行的文件（取路径地址），用来排重,不采用队列的contains,会遍历所有，效率较低
     */
    protected final static Set<String> RUNNING_FILES = new ConcurrentHashSet<String>();

    @Autowired
    protected ProcessService processService;

    /**
     * 执行抓取
     */
    @Override
    public void execute(String rootPath) {
        File sites = new File(rootPath);
        if (!sites.exists()) {
            sites.mkdirs();
        }
        if (ServerConstant.ZOOKEEPER_SWITCH == 0) { // 未开启zookeeper，则手动拉取一次
            RemoteServiceUtil.pullRecentlyUrl2Filter(RemoteConstant.PUSH_METHOD);
        }
        this.executeTaskPackage(sites);
        LOG.info("spider fetcher start ....");
    }


    /**
     * 读取所有配置文件
     */
    private List<File> files(List<File> ret, File file) {
        if (file.isDirectory()) {
            if (!".svn".equals(file.getName())) {
                File[] fs = file.listFiles();
                for (File f : fs) {
                    files(ret, f);
                }
            }
        } else if (file.getName().endsWith(".json")) {
            ret.add(file);
        }
        return ret;
    }


    /**
     * 执行线程包任务
     */
    protected abstract void executeRunablePackage(String processId, File executeFile) throws Exception;

    /**
     * 任务包之前执行
     */
    protected abstract String beforeExecute(String processId, Long beginTime, File taskFile);

    /**
     * 任务包之后执行，超时包，可能会执行不到此方法
     */
    protected abstract String afterExecute(String processId, Long beginTime, Long endTime, String message, ProcessStatusEnum processStatusEnum);

    /**
     * 执行一个任务包,分批次执行（批次会影响吞吐量）
     */
    private void executeTaskPackage(File sites) {
        ThreadUtils.executeQuertz(new Runnable() {
            @Override
            public void run() {
                try {
                    dealTaskFileToRunable(sites);
                } catch (Exception e) {
                    LOG.error("deal task file to runable , error ... ", e);
                }
            }
        }, 1, 5, TimeUnit.SECONDS);

        ThreadUtils.executeQuertz(new Runnable() {
            @Override
            public void run() {
                try {
                    dealFutureResult();
                } catch (Exception e) {
                    LOG.error("deal future result , error ... ", e);
                }
            }
        }, 5, 3, TimeUnit.SECONDS);
    }

    /**
     * 处理任务文件，并且加入到任务队列汇总
     */
    public void dealTaskFileToRunable(File sites) {
        List<File> allFiles = new ArrayList<File>();
        files(allFiles, sites);
        //============================================================
        for (int i = 0, j = allFiles.size(); i < j; i++) {
            File taskFile = allFiles.get(i);
            // 任务队列达到包限制最大  或者结果等待队列已满
            while (FUTURES.size() >= ProxyConstant.MAX_THREAD_COUNT || THREAD_ACTIVE_NUMBER.intValue() >= ProxyConstant.MAX_THREAD_COUNT) {
                ThreadUtils.sleepThreadSeconds(1);
            }
            // 排除正在执行的文件，防止执行过快，同一个文件被多个线程同时执行，浪费资源
            if (taskFile != null && RUNNING_FILES.contains(taskFile.getAbsolutePath())) {
                continue;
            }
            FutureModel futureModel = new FutureModel();
            String processId = SEQUENCE.getSequenceId();
            futureModel.setProcessId(processId);
            futureModel.setExecuteFile(taskFile);
            Callable<String> taskCallable = new Callable<String>() {
                @Override
                public String call() throws Exception {
                    long beginTime = System.currentTimeMillis();
                    futureModel.setExecute(true);
                    RUNNING_FILES.add(taskFile.getAbsolutePath());
                    beforeExecute(processId, beginTime, taskFile);
                    String msg = null;
                    ProcessStatusEnum processStatusEnum = null;
                    try {
                        executeRunablePackage(processId, taskFile);
                        processStatusEnum = ProcessStatusEnum.SUCCESS;
                    } catch (Exception e) {
                        msg = e.getMessage();
                        processStatusEnum = ProcessStatusEnum.FAIL;
                        LOG.error("[{}] file execute error , [{}]", taskFile.getPath(), e.getMessage());
                    } finally {
                        long endTime = System.currentTimeMillis();
                        long spendTime = endTime - beginTime;
                        LOG.info("[{}] file execute finished , spend time [{}] ms", taskFile.getPath(), spendTime);
                        afterExecute(processId, beginTime, endTime, msg, processStatusEnum);
                    }
                    return taskFile.getPath();
                }
            };
            Future<String> future = FETCHEREXECUTOR.submit(taskCallable);
            futureModel.setFuture(future);
            FUTURES.offer(futureModel);
        }
    }

    /**
     * 处理future的结果
     */
    protected void dealFutureResult() {
        while (THREAD_ACTIVE_NUMBER.intValue() < ProxyConstant.MAX_THREAD_COUNT) {
            FutureModel futureModel = FUTURES.poll();
            if (futureModel == null) {
                continue;
            }
            //还未执行的任务，已被结果任务取出的，休眠数秒，在重试一次，还不行，重新放回队列去。
            if (!futureModel.isExecute()) {
                ThreadUtils.sleepThreadSeconds(2);
                if (!futureModel.isExecute()) {
                    FUTURES.offer(futureModel);
                }
            }
            Future<String> future = futureModel.getFuture();
            String processId = futureModel.getProcessId();
            File executeFile = futureModel.getExecuteFile();
            FETCHER_RESULT_EXECUTOR.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        THREAD_ACTIVE_NUMBER.incrementAndGet();
                        future.get(ProxyConstant.PACKAGE_EXECUTE_TIMEOUT, TimeUnit.MINUTES);
                    } catch (InterruptedException e) {
                        afterExecute(processId, null, null, e.getMessage(), ProcessStatusEnum.FAIL);
                        LOG.error("[{}] process execute interrupted error ...", processId);
                    } catch (ExecutionException e) {
                        afterExecute(processId, null, null, e.getMessage(), ProcessStatusEnum.FAIL);
                        LOG.error("[{}] process execute execution error ...", processId);
                    } catch (CancellationException e) {
                        afterExecute(processId, null, null, e.getMessage(), ProcessStatusEnum.TIMEOUT);
                        LOG.error("[{}] process execute cancel error ...", processId);
                    } catch (TimeoutException e) {
                        afterExecute(processId, null, null, e.getMessage(), ProcessStatusEnum.TIMEOUT);
                        LOG.error("[{}] process execute timeout error ...", processId);
                    } catch (Exception e) {
                        afterExecute(processId, null, null, e.getMessage(), ProcessStatusEnum.FAIL);
                        LOG.error("[{}] process execute other error ...", processId);
                    } finally {
                        // 正常和非正常的情况都会判断任务是否结束，如果没有结束，则cancel任务。cancel参数为true，表示即使任务正在执行，也会interrupt线程。
                        if (!future.isDone()) {
                            future.cancel(true);
                        }
                        RUNNING_FILES.remove(executeFile.getAbsolutePath());
                        if (executeFile.getPath().startsWith(ProxyConstant.SPIDER_JSON_TMP_DIR)) {
                            executeFile.delete();
                        }
                        THREAD_ACTIVE_NUMBER.decrementAndGet();
                    }
                }
            });
        }
    }

    private class FutureModel {
        private String processId;
        private File executeFile;
        private Future<String> future;
        private boolean execute; // 标志线程执行中或者，执行过该任务

        public boolean isExecute() {
            return execute;
        }

        public void setExecute(boolean execute) {
            this.execute = execute;
        }

        public String getProcessId() {
            return processId;
        }

        public void setProcessId(String processId) {
            this.processId = processId;
        }

        public Future<String> getFuture() {
            return future;
        }

        public void setFuture(Future<String> future) {
            this.future = future;
        }

        public File getExecuteFile() {
            return executeFile;
        }

        public void setExecuteFile(File executeFile) {
            this.executeFile = executeFile;
        }
    }
}
