package com.spider.push.article;

import com.spider.dubbo.vo.Response;
import com.spider.engine.httpclient.HttpPoolManage;
import com.spider.config.RemoteConstant;
import com.spider.push.DataPush;
import common.base.util.JsonUtil;

import java.util.Map;

/**
 * 文章http推送器
 */
public class ArticleHttpPush extends AbstractPush implements DataPush {


    @Override
    public Response pushSpiderData() {
        Response response = new Response();
        String json = JsonUtil.toString(this.getArticle());
        if (json == null) {
            response.setCode(Response.Code.FAIL);
            response.setMessage("json analyser error ...");
        }
        for (int i = 0; i < RemoteConstant.TASK_MAX_RETRY; i++) {
            String res = HttpPoolManage.sendPost(RemoteConstant.ARTICLE_ADD, json);
            if (res != null) {
                Map<?, ?> resMap = JsonUtil.readJsonMap(res);
                if (resMap.get("s") != null && "true".equals(resMap.get("s").toString())) {
                    response.setCode(Response.Code.SUCCESS);
                } else {
                    response.setCode(Response.Code.FAIL);
                }
            }
        }
        return response;
    }

}
