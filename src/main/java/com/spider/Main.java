package com.spider;

import com.spider.config.ProxyConstant;
import com.spider.config.RedisConstant;
import com.spider.config.RemoteConstant;
import com.spider.config.ServerConstant;
import com.spider.fetcher.Fetcher;
import com.spider.fetcher.impl.ArticleFetcher;
import com.spider.fetcher.impl.CommmentFetcher;
import com.spider.fetcher.impl.VideoFetcher;
import com.spider.monitor.SiteMonitor;
import com.spider.mq.subscriber.SubThread;
import com.spider.util.*;

/**
 * 抓取入口函数
 */
public class Main {

    /**
     * <ul>
     * 启动入库函数
     * <li>arg[0]==0 --> 文章抓取</li>
     * <li>arg[0]==1 --> 评论抓取</li>
     * <li>arg[0]==2 --> 视频抓取</li>
     * </ul>
     */
    public static void main(String[] args) {
        try {
            PidUtils.writePid();
            // 注册中心，主要用于集群，同步及负载均衡json配置文件信息
            if (ServerConstant.ZOOKEEPER_SWITCH == 1) {
                SiteMonitor.getInstance().start();
                ThreadUtils.sleepThreadSeconds(10);
            }
            // 连接redis消息通道，优先处理的抓取任务
            if (RedisConstant.REDIS_PULL_SWITCH == 1) {
                SubThread subThread = new SubThread();
                subThread.start();
            }
            // 定时拉取最新的抓取url，同步到本地
            if (RemoteConstant.SPIDER_REMOTE_URL_SYNC == 1) {
                RemoteServiceUtil.syncRemoteUrls();
            }
            ShutdownHookUtil.hook();
            Fetcher fetcher = createFetcher(args);
            fetcher.execute(ProxyConstant.CONFIG_ROOT);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建抓取器
     */
    private static Fetcher createFetcher(String[] args) {
        Fetcher fetcher = null;
        if (args == null || args.length == 0) {
            fetcher = ApplicationContextUtils.getBean(ArticleFetcher.class);
        } else {
            int index = Integer.parseInt(args[0]);
            switch (index) {
                case 0:
                    fetcher = ApplicationContextUtils.getBean(ArticleFetcher.class);
                    break;
                case 1:
                    fetcher = ApplicationContextUtils.getBean(CommmentFetcher.class);
                    break;
                case 2:
                    fetcher = ApplicationContextUtils.getBean(VideoFetcher.class);
                    break;
            }
        }
        return fetcher;
    }
}
