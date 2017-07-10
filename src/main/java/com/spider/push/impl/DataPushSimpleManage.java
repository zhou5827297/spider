package com.spider.push.impl;

import com.spider.config.ProxyConstant;
import com.spider.filter.impl.BloomFilterManage;
import com.spider.push.DataPushManage;
import com.spider.util.ThreadUtils;
import com.spider.config.RemoteConstant;
import com.spider.filter.FilterManage;
import com.spider.push.DataPush;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

/**
 * 简单实现推送器，支持记录保存，重发等
 */
public class DataPushSimpleManage implements DataPushManage {
    private final Logger LOG = LoggerFactory.getLogger(getClass());

    private final LinkedBlockingQueue<DataPush> queues = new LinkedBlockingQueue<DataPush>();

    private final int ONEEXECUTE = RemoteConstant.TASK_ONCE_MAX;

    private FilterManage bloomFilter = BloomFilterManage.getInstance();

    private boolean running = false;

    private final static DataPushSimpleManage MANAGE = new DataPushSimpleManage();

    private final static ExecutorService EXECUTOR = ThreadUtils.PUSHEXECUTOR;

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
        }, 0, 30, TimeUnit.SECONDS);
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
        List<DataPush> dataPushs = new ArrayList<DataPush>();
        for (int i = 0, j = queues.size(); i < j && i < ONEEXECUTE; i++) {
            DataPush dataPush = queues.poll();
            if (dataPush == null) {
                break;
            }
            dataPushs.add(dataPush);
        }
        executeTaskPackage(dataPushs);
    }

    @Override
    public void pushSuccess(DataPush push) {
        AbstractPush dataPush = ((AbstractPush) push);
        bloomFilter.add(dataPush.getArticle().getUrl());
        LOG.info("[{}] push success", dataPush.getArticle());
        //TODO 记录推送成功信息
    }

    @Override
    public void pushFail(DataPush push) {
        addPush(push);
        AbstractPush dataPush = ((AbstractPush) push);
        LOG.info("[{}] push fail, retry[{}/{}]", dataPush.getArticle(), dataPush.getPushErrorCount(), dataPush.getPushCount());
        //TODO 记录推送失败信息
    }

    @Override
    public boolean addPush(DataPush push) {
        return queues.offer(push);
    }

    /**
     * 执行一个任务包
     */
    private void executeTaskPackage(List<DataPush> dataPushs) {
        Collection<Callable<String>> tasks = new ArrayList<Callable<String>>();
        for (DataPush dataPush : dataPushs) {
            tasks.add(new Callable<String>() {
                @Override
                public String call() throws Exception {
                    try {
                        AbstractPush push = ((AbstractPush) dataPush);
                        if (push.getPushErrorCount() < RemoteConstant.TASK_OFFLINE_ROUND_MAX_RETRY) {
                            push.incrementPushCount();
                            boolean success = dataPush.push();
                            if (success) {
                                pushSuccess(dataPush);
                            } else {
                                push.incrementPushErrorCount();
                                pushFail(dataPush);
                                addPush(dataPush);
                            }
                        } else {
                            LOG.error("push error [{}]", push);
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
