package com.spider.push.article;

import com.alibaba.dubbo.common.utils.ConcurrentHashSet;
import com.spider.config.ProxyConstant;
import com.spider.filter.impl.BloomFilterManage;
import com.spider.config.RemoteConstant;
import com.spider.filter.FilterManage;
import com.spider.push.DataPushManage;
import com.spider.util.ThreadUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 简单实现推送器，支持记录保存，重发等
 */
public class DataPushSimpleManage implements DataPushManage<AbstractPush> {
    private final Logger LOG = LoggerFactory.getLogger(getClass());

    private final ConcurrentLinkedQueue<AbstractPush> queues = new ConcurrentLinkedQueue<AbstractPush>();

    private final int ONEEXECUTE = RemoteConstant.TASK_ONCE_MAX;

    private FilterManage bloomFilter = BloomFilterManage.getInstance();

    private boolean running = false;

    private final static DataPushSimpleManage MANAGE = new DataPushSimpleManage();

    private final static ExecutorService EXECUTOR = ThreadUtils.PUSHEXECUTOR;

    /**
     * 记录离线池的记录标记，用来排重,不采用队列的contains,会遍历所有，效率较低
     */
    private final static Set<String> POOLS = new ConcurrentHashSet<String>();

    @Override
    public synchronized void start() {
        if (running) {
            return;
        }
        ThreadUtils.executeQuertz(new Runnable() {
            @Override
            public void run() {
                try {
                    long start = System.currentTimeMillis();
                    pushAll();
                    long end = System.currentTimeMillis();
                    LOG.info("push batch time [{}] ms", end - start);
                } catch (Exception ex) {
                    LOG.error("push batch error", ex);
                }
            }
        }, 0, 1, TimeUnit.MINUTES);
        running = true;
    }

    @Override
    public void shutdown() {
        queues.clear();
    }

    @Override
    public void pushAll() {
        if (queues.isEmpty()) {
            return;
        }
        List<AbstractPush> dataPushs = new ArrayList<AbstractPush>();
        for (int i = 0, j = queues.size(); i < j && i < ONEEXECUTE; i++) {
            AbstractPush dataPush = queues.poll();
            if (dataPush == null) {
                break;
            }
            dataPushs.add(dataPush);
        }
        executeTaskPackage(dataPushs);
    }

    @Override
    public void pushSuccess(AbstractPush push) {
        bloomFilter.add(push.getArticle().getUrl());
        LOG.info("[{}] push success", push.getArticle());
        //TODO 记录推送成功信息
        if (push.getArticle() != null) {
            POOLS.remove(push.getArticle().getUrl());
        }
    }

    @Override
    public void pushFail(AbstractPush push) {
        addPush(push);
        LOG.error("[{}] push fail, retry[{}/{}]", push, push.getPushErrorCount() + 1, RemoteConstant.TASK_OFFLINE_ROUND_MAX_RETRY);
        //TODO 记录推送失败信息
    }

    @Override
    public boolean addPush(AbstractPush push) {
        if (POOLS.contains(push.getArticle().getUrl())) { // 离线推送池中已经存在，跳过
            return false;
        } else {
            POOLS.add(push.getArticle().getUrl());
        }
        return queues.offer(push);
    }

    /**
     * 执行一个任务包
     */
    private void executeTaskPackage(List<AbstractPush> dataPushs) {
        Collection<Callable<String>> tasks = new ArrayList<Callable<String>>();
        for (AbstractPush dataPush : dataPushs) {
            tasks.add(new Callable<String>() {
                @Override
                public String call() throws Exception {
                    try {
                        if (dataPush.getPushErrorCount() < RemoteConstant.TASK_OFFLINE_ROUND_MAX_RETRY) {
                            dataPush.incrementPushCount();
                            boolean success = dataPush.push();
                            if (success) {
                                pushSuccess(dataPush);
                            } else {
                                dataPush.incrementPushErrorCount();
                                pushFail(dataPush);
                                addPush(dataPush);
                            }
                        } else {
                            LOG.error("push error [{}]", dataPush);
                        }

                    } catch (Throwable e) {
                        LOG.error("Callable call error:" + e.getMessage(), e);
                    }
                    return null;
                }
            });
        }
        try {
            LOG.info("invokeAll start... tasks size: " + tasks.size());
            EXECUTOR.invokeAll(tasks, ProxyConstant.PACKAGE_EXECUTE_TIMEOUT, TimeUnit.MINUTES);
            LOG.info("invokeAll end... tasks size: " + tasks.size());
        } catch (InterruptedException e) {
            LOG.error("invokeAll error:", e);
        } finally {
            tasks.clear();
        }
    }

    public static DataPushSimpleManage getInstance() {
        if (MANAGE.running == false) {
            MANAGE.start();
        }
        return MANAGE;
    }
}
