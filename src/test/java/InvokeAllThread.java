
import com.spider.util.ThreadUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

/**
 * 批量任务的限时 invokeAll(tasks) 批量提交不限时任务
 * <p>
 * invokeAll(tasks, timeout, unit) 批量提交限时任务
 * <p>
 * InvokeAll方法处理一个任务的容器（collection），并返回一个Future的容器。两个容器具有相同的结构：
 * invokeAll将Future添加到返回的容器中，这样可以使用任务容器的迭代器，从而调用者可以将它表现的Callable与Future 关联起来。
 * 当所有任务都完成时、调用线程被中断时或者超过时限时，限时版本的invokeAll都会返回结果。 超过时限后，任务尚未完成的任务都会被取消。
 *
 * @author hadoop
 */
public class InvokeAllThread {
    // 固定大小的线程池，同时只能接受5个任务
//    static ExecutorService mExecutor = Executors.newFixedThreadPool(5);
    static ThreadPoolExecutor mExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);
    static ThreadPoolExecutor mExecutor1 = (ThreadPoolExecutor) Executors.newFixedThreadPool(50);

    /**
     * 计算价格的任务
     *
     * @author hadoop
     */
    private class QuoteTask implements Callable<BigDecimal> {
        public final double price;
        public final int num;

        public QuoteTask(double price, int num) {
            this.price = price;
            this.num = num;
        }

        @Override
        public BigDecimal call() throws Exception {
            try {
//                throw new Exception("error");
                Random r = new Random();
                long time = (r.nextInt(10) + 1) * 1000;
//                Thread.sleep(time);

                BigDecimal d = BigDecimal.valueOf(price * num).setScale(2);
                System.out.println("耗时：" + time / 1000 + "s,单价是：" + price + ",人数是："
                        + num + "，总额是：" + d);
//            TimeUnit.SECONDS.sleep(60);
                return d;
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("sleep interrupted");
            return null;
        }
    }

//    /**
//     * 在预定时间内请求获得旅游报价信息
//     *
//     * @return
//     */
//    public void getRankedTravelQuotes() throws InterruptedException {
//        List<QuoteTask> tasks = new ArrayList<QuoteTask>();
//        // 模拟10个计算旅游报价的任务
//        for (int i = 1; i <= 20; i++) {
//            tasks.add(new QuoteTask(200, i));
//        }
//
//        /**
//         * 使用invokeAll方法批量提交限时任务任务 预期15s所有任务都执行完,没有执行完的任务会自动取消
//         *
//         */
//        List<Future<BigDecimal>> futures = mExecutor.invokeAll(tasks, 4, TimeUnit.MILLISECONDS);
//        // 报价合计集合
//        List<BigDecimal> totalPriceList = new ArrayList<BigDecimal>();
//
//        Iterator<QuoteTask> taskIter = tasks.iterator();
//
//        for (Future<BigDecimal> future : futures) {
//            QuoteTask task = taskIter.next();
//            try {
//                totalPriceList.add(future.get());
//            } catch (ExecutionException e) {
//                // 返回计算失败的原因
//                // totalPriceList.add(task.getFailureQuote(e.getCause()));
//                totalPriceList.add(BigDecimal.valueOf(-1));
//                System.out.println("任务执行异常,单价是" + task.price + "，人数是：" + task.num);
//            } catch (CancellationException e) {
//                // totalPriceList.add(task.getTimeoutQuote(e));
//                totalPriceList.add(BigDecimal.ZERO);
//                System.out.println("任务超时，取消计算,单价是" + task.price + "，人数是：" + task.num);
//            }
//        }
//        for (BigDecimal bigDecimal : totalPriceList) {
//            System.out.println(bigDecimal);
//        }
//        mExecutor.shutdown();
//    }

    /**
     * 在预定时间内请求获得旅游报价信息
     *
     * @return
     */
    private static ConcurrentLinkedQueue<Future<BigDecimal>> futures = new ConcurrentLinkedQueue<Future<BigDecimal>>();

    public void getRankedTravelQuotes() throws InterruptedException {
//        List<QuoteTask> tasks = new ArrayList<QuoteTask>();
        ConcurrentLinkedQueue<QuoteTask> tasks = new ConcurrentLinkedQueue<QuoteTask>();
//        ConcurrentLinkedQueue<Future<BigDecimal>> futures = new ConcurrentLinkedQueue<Future<BigDecimal>>();
        // 模拟10个计算旅游报价的任务
        for (int i = 1; i <= 20; i++) {
            tasks.add(new QuoteTask(200, i));
            Future<BigDecimal> future = mExecutor.submit(new QuoteTask(200, i));
            futures.add(future);
        }
        // 报价合计集合
        List<BigDecimal> totalPriceList = new ArrayList<BigDecimal>();
        Iterator<QuoteTask> taskIter = tasks.iterator();

        ThreadUtils.executeQuertz(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (mExecutor.getActiveCount() < mExecutor.getMaximumPoolSize()) {
                        Future<BigDecimal> future = mExecutor.submit(new QuoteTask(200, 100));
                        futures.add(future);
                        if (futures.size() > 60) {
                            ThreadUtils.sleepThreadSeconds(10);
                        }
                    }
                }
            }
        }, 1, 10, TimeUnit.SECONDS);


        ThreadUtils.executeQuertz(new Runnable() {
            @Override
            public void run() {
                System.out.println("====================begin========================");
                Iterator<Future<BigDecimal>> iterator = futures.iterator();
                System.out.println("====================futures========================");
                while (iterator.hasNext()) {
//                    while (mExecutor1.getActiveCount() >= mExecutor1.getMaximumPoolSize()) {
//                        ThreadUtils.sleepThreadSeconds(3);
//                    }
                    Future<BigDecimal> future = iterator.next();
                    mExecutor1.execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                // 10, TimeUnit.SECONDS
                                totalPriceList.add(future.get(2, TimeUnit.SECONDS));
                            } catch (ExecutionException e) {
                                // 返回计算失败的原因
                                // totalPriceList.add(task.getFailureQuote(e.getCause()));
                                totalPriceList.add(BigDecimal.valueOf(-1));
//                        System.out.println("任务执行异常,单价是" + task.price + "，人数是：" + task.num);
                                System.out.println("ExecutionException");
                            } catch (CancellationException e) {
                                // totalPriceList.add(task.getTimeoutQuote(e));
                                totalPriceList.add(BigDecimal.ZERO);
                                System.out.println("CancellationException");
//                        System.out.println("任务超时，取消计算,单价是" + task.price + "，人数是：" + task.num);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            futures.remove(future);
                            System.out.println("remove");
                        }
                    });
                }
//                for (BigDecimal bigDecimal : totalPriceList) {
//                    System.out.println(bigDecimal);
//                }
                System.out.println("====================end========================");
        }
        }, 1, 5, TimeUnit.SECONDS);


        // mExecutor.shutdown();
        // mExecutor1.shutdown();
    }


    public static void main(String[] args) {
        try {
            InvokeAllThread it = new InvokeAllThread();
            it.getRankedTravelQuotes();
//            LockSupport.parkNanos(100000);
//            System.exit(-1);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
