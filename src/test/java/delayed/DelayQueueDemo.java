package delayed;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * DelayQueue
 * <p>
 * 这是一个无界的BlockingQueue，用于放置实现了Delayed接口的对象，
 * 其中的對象只能在其到时才能从队列中取走。这种队列是有序的，即队头对象的延迟到期的时间最长。
 * 如果没有任何延迟到期，那么就不会有任何头元素，并且poll()将返回null（正因为这样，你不能将
 * null放置到这种队列中）。
 * <p>
 * 下面是一个示例，其中的Delayed对象自身就是任务，而DelayedTaskConsumer将最“紧急”的任务
 * （到期时间最长的任务）从队列中取出，然后运行它。注意，这样DelayQueue就成为了优先级队列的一种变体：
 */

/**
 * DelayedTask包含一个称为sequence的List<DelayedTask>，它保存了任务被创建的顺序，因此我们可以看到
 * 排序是按照实际发生的顺序执行的。
 * <p>
 * Delayed接口有一个方法名为getDelay()，它可以用来告知延迟到期有多长时间，或者延迟在多长时间之前已经到期。
 * 这个方法将强制我们去使用TimeUnit类，因此这就是参数类型。这会产生一个非常方便的类，因为你可以很容易地
 * 转换单位而无需任何声明。例如，delta的值时以毫秒为单位存储的，但是java SE5的方法System.nanoTime()产生
 * 的时间则是以纳米为单位的。你可以转换delta的值，这个方法是声明它的单位以及你希望以什么单位来表示，
 * 就像下面这样：
 * NANOSECONDS.convert(delta,MILLISECONDS);
 * <p>
 * 在getDelay()中，希望使用的单位是作为unit参数传递进来的，你使用它将当前时间与触发时间之间的差转换为调用者
 * 要求的单位，而无需只掉这些单位是什么（这是策略设计模式的一个简单示例，在这种模式中，算法的一部分是作为参数
 * 传递进来的）。
 * <p>
 * 为了排序，Delayed接口还继承了Comparable接口，因此必须实现compareTo()，使其可以产生合理的比较。toString()
 * 和summary()提供了输出格式化，而嵌套的EndSentinel类提供了一种关闭所有事物的途径，具体做法是将其放置为队列的
 * 最后一个元素。
 *
 * @create @author Henry @date 2017-1-3
 */
class DelayedTask implements Runnable, Delayed {
    private static int counter = 0;
    private final int id = counter++;
    private final int delta;
    private final long trigger;
    protected static List<DelayedTask> sequence = new ArrayList<DelayedTask>();

    public DelayedTask(int delayInMilliseconds) {
        delta = delayInMilliseconds;
        trigger = System.nanoTime() + TimeUnit.NANOSECONDS.convert(delta, TimeUnit.MILLISECONDS);
        sequence.add(this);
    }

    @Override
    public void run() {
        System.out.println(this + " ---");
    }

    @Override
    public int compareTo(Delayed o) {
        DelayedTask that = (DelayedTask) o;
        if (trigger < that.trigger)
            return -1;
        if (trigger > that.trigger)
            return 1;
        return 0;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(trigger - System.nanoTime(), TimeUnit.NANOSECONDS);
    }

    @Override
    public String toString() {
        return String.format("[%1$-4d]", delta) + " Task " + id;
    }

    public String summary() {
        return "(" + id + ":" + delta + ")";
    }

    public static class EndSentinel extends DelayedTask {
        private ExecutorService exec;

        public EndSentinel(int delay, ExecutorService e) {
            super(delay);
            exec = e;
        }

        @Override
        public void run() {
            for (DelayedTask pt : sequence) {
                System.out.println(pt.summary() + " ++");
            }
            System.out.println();
            System.out.println(this + " Calling shutdownNow()");
            exec.shutdownNow();
        }
    }
}

/**
 * 注意，因为DelayedTaskConsumer自身是一个任务，所以它由自己的Thread，它可以使用这个线程来运行从队列中获取
 * 的所有任务。由于这个任务是按照队列优先级的顺序执行的，因此在本例中不需要启动任何单独的线程来运行DelayedTask.
 *
 * @create @author Henry @date 2017-1-3
 */
class DelayedTaskConsumer implements Runnable {
    private DelayQueue<DelayedTask> q;

    public DelayedTaskConsumer(DelayQueue<DelayedTask> q) {
        this.q = q;
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted())
                q.take().run();
            //new Thread(q.take()).start();
        } catch (InterruptedException e) {
            System.err.println("InterruptedException");
        }
        System.out.println("Finished DelayedTaskConsumer");
    }
}

/**
 * 从输出中可以看到，任务创建的顺序对执行顺序没有任何影响，任务是按照所期望的延迟顺序执行的。
 *
 * @create @author Henry @date 2017-1-3
 */
public class DelayQueueDemo {
    public static void main(String[] args) {
        Random rand = new Random(47);
        ExecutorService exec = Executors.newCachedThreadPool();
        DelayQueue<DelayedTask> queue = new DelayQueue<DelayedTask>();
        // Fill with tasks that have random delays;
        for (int i = 0; i < 5; i++)
            queue.put(new DelayedTask(rand.nextInt(5000)));
        // Set the stopping point
        queue.add(new DelayedTask.EndSentinel(5000, exec));
        exec.execute(new DelayedTaskConsumer(queue));
    }
}
