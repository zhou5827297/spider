package com.spider.util;


import com.spider.config.ProxyConstant;
import com.spider.config.RemoteConstant;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程工具类
 */
public class ThreadUtils {
    public static final ScheduledExecutorService SCHEDULEDEXECUTOR = Executors.newScheduledThreadPool(10, new SpiderThreadFactory("spider-scheduled"));
    public static final ExecutorService FETCHEREXECUTOR = Executors.newFixedThreadPool(ProxyConstant.MAX_THREAD_COUNT, new SpiderThreadFactory("spider-fetcher"));
    public static final ExecutorService FETCHER_RESULT_EXECUTOR = Executors.newFixedThreadPool(ProxyConstant.MAX_THREAD_COUNT, new SpiderThreadFactory("spider-fetcher-result"));
    //    public static final ExecutorService FETCHER_RESULT_EXECUTOR = new ThreadPoolExecutor(ProxyConstant.MAX_THREAD_COUNT, ProxyConstant.MAX_THREAD_COUNT, Long.MAX_VALUE, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), new SpiderThreadFactory("spider-fetcher-result"));
    public static final ExecutorService PUSHEXECUTOR = Executors.newFixedThreadPool(RemoteConstant.TASK_ONCE_MAX, new SpiderThreadFactory("spider-push"));

    /**
     * 获取cpu数量
     */
    public static int getCpuProcessors() {
        return Runtime.getRuntime().availableProcessors();
    }

    /**
     * 获取最优执行线程数量 (6 CPU * 4 Core+1 * 2 HT =48 Threading.)
     */
    public static int getThreadCount() {
        try {
            //return 500;
            return getCpuProcessors() * (4 + 1) * 5;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 500;
    }

    /**
     * 定时执行一条任务
     *
     * @param command      执行线程
     * @param initialDelay 延时时间
     * @param period       执行间隔
     * @param timeUnit     间隔类型
     */
    public static void executeQuertz(Runnable command, long initialDelay, long period, TimeUnit timeUnit) {
        SCHEDULEDEXECUTOR.scheduleWithFixedDelay(command, initialDelay, period, timeUnit);
    }

    /**
     * 休眠一条线程，指定时间
     *
     * @param timeUnit timeunit
     * @param timeout  时间
     */
    public static void sleepThreadByTimeUnit(TimeUnit timeUnit, long timeout) {
        try {
            timeUnit.sleep(timeout);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 休眠一条线程timeout秒钟
     */
    public static void sleepThreadSeconds(long timeout) {
        try {
            TimeUnit.SECONDS.sleep(timeout);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭线程池
     */
    public static void shutdown() {
        SCHEDULEDEXECUTOR.shutdown();
        FETCHEREXECUTOR.shutdown();
        PUSHEXECUTOR.shutdown();
    }

    /**
     * spider自定义的线程工厂
     */
    private static class SpiderThreadFactory implements ThreadFactory {
        private static final AtomicInteger poolNumber = new AtomicInteger(1);
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private String namePrefix;

        SpiderThreadFactory() {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() :
                    Thread.currentThread().getThreadGroup();
            namePrefix = "pool-" +
                    poolNumber.getAndIncrement() +
                    "-thread-";
        }

        SpiderThreadFactory(String namePrefix) {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() :
                    Thread.currentThread().getThreadGroup();
            this.namePrefix = namePrefix +
                    "-thread-";
        }

        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r,
                    namePrefix + threadNumber.getAndIncrement(),
                    0);
            if (t.isDaemon())
                t.setDaemon(false);
            if (t.getPriority() != Thread.NORM_PRIORITY)
                t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }
    }

    public static void main(String[] args) {
        System.out.println(getThreadCount());
    }
}
