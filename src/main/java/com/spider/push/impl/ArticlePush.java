package com.spider.push.impl;

import com.spider.dubbo.service.SpiderSyncService;
import com.spider.config.RemoteConstant;
import com.spider.engine.httpclient.HttpPoolManage;
import com.spider.monitor.DubboServiceUtil;
import common.base.util.JsonUtil;

import java.util.Map;

/**
 * 文章推送功能
 */
public class ArticlePush extends AbstractPush {

    private static SpiderSyncService SPIDERSYNCSERVICE = DubboServiceUtil.getSpiderService();

    @Override
    public boolean push() {
        if ("dubbo".equals(RemoteConstant.PUSH_METHOD)) {
            boolean flag = true;
            try {
                flag = SPIDERSYNCSERVICE.addArticle(this.getArticle());
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
                flag = false;
            } finally {
                return flag;
            }
        }

        String json = JsonUtil.toString(this.getArticle());
        if (json == null) {
            return false;
        }
        for (int i = 0; i < RemoteConstant.TASK_MAX_RETRY; i++) {
            String response = HttpPoolManage.sendPost(RemoteConstant.ARTICLE_ADD, json);
            if (response != null) {
                Map<?, ?> resMap = JsonUtil.readJsonMap(response);
                if (resMap.get("s") != null && "true".equals(resMap.get("s").toString())) {
                    return true;
                } else {
                    return false;
                }
            }

        }
        return false;
    }
}
