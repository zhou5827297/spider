package com.spider.util;

import com.spider.monitor.SiteMonitor;
import com.spider.engine.httpclient.HttpPoolManage;

/**
 * 销毁机制
 */
public class ShutdownHookUtil {

    /**
     * 正常退出销毁
     */
    public static void hook() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                try {
                    ThreadUtils.shutdown();
                    HttpPoolManage.shutdown();
                    SiteMonitor.getInstance().close();
                    RedisUtil.getInstance().close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
