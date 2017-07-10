package com.spider.mq.subscriber;

import com.spider.config.ProxyConstant;
import com.spider.fetcher.AbstractFetcher;
import com.spider.util.ApplicationContextUtils;
import com.spider.fetcher.impl.ArticleFetcher;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisPubSub;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

/**
 * 订阅
 */
public class Subscriber extends JedisPubSub {
    private final Logger LOG = LoggerFactory.getLogger(getClass());

    private final AbstractFetcher fetcher;

    public Subscriber() {
        fetcher = ApplicationContextUtils.getBean(ArticleFetcher.class);
    }

    public void onMessage(String channel, String message) {
        LOG.info("receive redis published message, channel {}, message {}", channel, message);
        FileWriter fout = null;
        PrintWriter out = null;
        File tempFile = null;
        try {
            File fileDic = new File(ProxyConstant.SPIDER_JSON_TMP_DIR);
            if (!fileDic.exists()) {
                fileDic.mkdir();
            }
            tempFile = File.createTempFile("spider-site", ".json", fileDic);
            System.out.println(tempFile.getAbsolutePath());
            fout = new FileWriter(tempFile);
            out = new PrintWriter(fout);
            out.println(message);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        } finally {
            IOUtils.closeQuietly(out);
            IOUtils.closeQuietly(fout);
            if (tempFile != null && tempFile.exists()) {
                fetcher.dealTaskFileToRunable(tempFile);
            }
        }
    }

    public void onSubscribe(String channel, int subscribedChannels) {
        LOG.info("subscribe redis channel success, channel {}, subscribedChannels {}", channel, subscribedChannels);
    }

    public void onUnsubscribe(String channel, int subscribedChannels) {
        LOG.info("unsubscribe redis channel, channel {}, subscribedChannels {}", channel, subscribedChannels);

    }
}
