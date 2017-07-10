package mq;

import com.spider.mq.publisher.Publisher;

/**
 * Created by zhoukai on 2017/5/16.
 */
public class PubSubDemo {
    public static void main(String[] args) {
//        SubThread subThread = new SubThread();
//        subThread.start();

        Publisher publisher = new Publisher();
        publisher.start();
    }
}
