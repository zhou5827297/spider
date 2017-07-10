package com.spider.browser;

import com.spider.config.ProxyConstant;
import com.spider.engine.httpclient.HttpClientRequest;
import com.spider.proxy.ProxyBean;
import com.spider.model.Comment;
import com.spider.model.DataInfo;

/**
 * 评论浏览器
 */
public class CommentBrowser extends BaseBrowser {
    protected HttpClientRequest request = new HttpClientRequest();

    public CommentBrowser() {
    }

    /**
     * 浏览页面
     */
    public String browse(String url, Comment comment) {
        DataInfo dataInfo = new DataInfo();
        dataInfo.setUrl(url);
        if (comment.getRequest() != null) {
            dataInfo.setHeader(comment.getRequest().getHeader());
        }

        for (int j = 0; j < ProxyConstant.TASK_MAX_RETRY; j++) {
            LOG.info("begin fetch  [{}]-[{}/{}]...", url, j + 1, ProxyConstant.TASK_MAX_RETRY);
            ProxyBean proxyBean = null;
            try {
                if (ProxyConstant.PROXY_SWITCH == 1) {
                    proxyBean = proxyManage.getProxyBean();
                }
                // 本机也属于动态ip，最后一次强制使用本机ip尝试。(提高成功率)
                if (j == ProxyConstant.TASK_MAX_RETRY - 1) {
                    proxyBean = null;
                }
                dataInfo.setProxyBean(proxyBean);
                String response = request.execute(dataInfo);
                if (response != null) {
                    String content = response.toString();
                    if (ProxyConstant.SHOW_BROWER == 1) {
                        SwingBrowser.showContent(url, content);
                    }
                    proxyManage.noticeSuccess();
                    LOG.info("fetch success [{}]-[{}]-[{}/{}]...", proxyBean, url, j + 1, ProxyConstant.TASK_MAX_RETRY);
                    return content;
                } else {
                    proxyManage.noticeFail("触发反爬虫");
                    LOG.warn("fetch fail [{}]-[{}]-[{}/{}]...", proxyBean, url, j + 1, ProxyConstant.TASK_MAX_RETRY);
                }
            } catch (Exception ex) {
                proxyManage.noticeFail(ex.getMessage());
                LOG.info("fetch fail [{}]-[{}]-[{}/{}]...", proxyBean, url, j + 1, ProxyConstant.TASK_MAX_RETRY);
            }
        }
        return null;
    }

}
