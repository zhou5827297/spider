package com.spider.mq.subscriber;

import com.spider.util.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 订阅执行线程
 */
public class SubThread extends Thread {
    private final Logger LOG = LoggerFactory.getLogger(getClass());

    private final Subscriber subscriber = new Subscriber();

    private final String channel = "spider-site";

    public SubThread() {
        super("spider-site-subscribe");
    }

    @Override
    public void run() {
        LOG.info("subscribe redis, channel {}, thread will be blocked", channel);
        RedisUtil redis = RedisUtil.getInstance();
        while (true) {
            try {
                redis.getJedis().subscribe(subscriber, channel);
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
            } finally {
                redis.returnJedis();
            }
        }
    }
}