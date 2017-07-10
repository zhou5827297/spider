package com.spider.util;

import com.spider.filter.impl.BloomFilterManage;
import com.spider.config.RemoteConstant;
import com.spider.dubbo.DubboService;
import com.spider.engine.httpclient.HttpPoolManage;
import common.base.model.ListResponse;
import common.base.model.ListResponseImpl;
import common.base.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 对远程服务的简单封装
 */
public class RemoteServiceUtil {
    private final static Logger LOG = LoggerFactory.getLogger(RemoteServiceUtil.class);

    /**
     * 拉取抓取最新的url加入过滤器
     */
    public static void pullRecentlyUrl2Filter(String type) {
        List<String> urls = null;
        if ("dubbo".equalsIgnoreCase(type)) {
            try {
                urls = DubboService.getSpiderSyncService().listUrl("create_time desc", 1, 10000);
            } catch (Exception e) {
                LOG.error("dubbo request error [{}]", e.getMessage());
            }
        } else {
            ListResponse res = null;
            for (int i = 0; i < RemoteConstant.TASK_MAX_RETRY; i++) {
                String json = HttpPoolManage.sendGet(RemoteConstant.ARTICLE_LIST + "?p=1&l=10000&orderBy=create_time%20desc");
                if (json != null) {
                    try {
                        res = JsonUtil.tryObject(json, ListResponseImpl.class);
                        if (res != null) {
                            break;
                        }
                    } catch (Exception ex) {
                        LOG.error("json analyser [{}]", json);
                    }
                }
            }
            if (res != null) {
                urls = (List<String>) res.getResult();
            }
        }
        // 刷新过滤器中的数据
        BloomFilterManage.getInstance().refresh(urls);
    }

    /**
     * 定时拉取最新的抓取url，同步到本地
     */
    public static void syncRemoteUrls() {
        ThreadUtils.executeQuertz(new Runnable() {
            @Override
            public void run() {
                try {
                    long start = System.currentTimeMillis();
                    pullRecentlyUrl2Filter(RemoteConstant.PUSH_METHOD);
                    long end = System.currentTimeMillis();
                    LOG.info("push syncRemoteUrls time [{}] ms", end - start);
                } catch (Exception ex) {
                    LOG.error("push syncRemoteUrls error", ex);
                }
            }
        }, 2, 1, TimeUnit.MINUTES);
    }
}
