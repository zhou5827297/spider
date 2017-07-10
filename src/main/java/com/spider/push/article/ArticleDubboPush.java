package com.spider.push.article;

import com.spider.dubbo.service.SpiderSyncService;
import com.spider.dubbo.vo.Response;
import com.spider.dubbo.DubboService;
import com.spider.push.DataPush;

/**
 * 文章dubbo推送器
 */
public class ArticleDubboPush extends AbstractPush implements DataPush {

    private static SpiderSyncService SPIDERSYNCSERVICE = DubboService.getSpiderSyncService();

    /**
     * 刷新dubboservice信息
     */
    public static void refresh() {
        SPIDERSYNCSERVICE = DubboService.getSpiderSyncService();
    }

    @Override
    public Response pushSpiderData() {
        Response response = new Response();
        try {
            response = SPIDERSYNCSERVICE.addArticle(this.getArticle());
        } catch (Exception e) {
            LOG.error("dubbo request error [{}]", e.getMessage());
            response.setMessage(e.getMessage());
            response.setCode(Response.Code.FAIL);
        }
        return response;
    }

}
