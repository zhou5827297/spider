package com.spider.browser;

import com.spider.config.ProxyConstant;
import com.spider.engine.htmlunit.WebClientRequest;
import com.spider.model.JsResult;
import com.spider.proxy.ProxyBean;
import com.spider.engine.htmlunit.WebClientPool;
import com.spider.model.Config;
import com.spider.model.DataInfo;
import com.spider.util.SpiderUtils;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import java.util.Map;

/**
 * 文章浏览器
 */
public class AriticleBrowser extends BaseBrowser {
    protected WebClientRequest request = new WebClientRequest();

    public AriticleBrowser() {
    }

    /**
     * 浏览页面
     */
    public Page browse(String url, Map<String, String> header, Config config) {
        DataInfo dataInfo = new DataInfo();
        dataInfo.setUrl(url);
        dataInfo.setHeader(header);
        dataInfo.setJavaScriptEnabled(config.getJavaScriptEnabled());
        dataInfo.setTimeout(config.getTimeout());
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
                Page page = request.execute(dataInfo);
                if (page != null && page.getWebResponse() != null && SpiderUtils.isPass(page.getWebResponse().getStatusCode())) {
                    if (ProxyConstant.SHOW_BROWER == 1) {
                        SwingBrowser.showContent(page.getUrl().toExternalForm(), page.getWebResponse().getContentAsString());
                    }
                    proxyManage.noticeSuccess();
                    LOG.info("fetch success [{}]-[{}]-[{}/{}]...", proxyBean, url, j + 1, ProxyConstant.TASK_MAX_RETRY);
                    return page;
                } else {
                    closeWebClient();
                    proxyManage.noticeFail("触发反爬虫");
                    LOG.info("fetch fail [{}]-[{}]-[{}/{}]...", proxyBean, url, j + 1, ProxyConstant.TASK_MAX_RETRY);
                }
            } catch (Exception ex) {
                closeWebClient();
                proxyManage.noticeFail(ex.getMessage());
                LOG.info("fetch fail [{}]-[{}]-[{}/{}]...", proxyBean, url, j + 1, ProxyConstant.TASK_MAX_RETRY);
            }

        }
        return null;
    }

    /**
     * 关闭浏览器
     */
    public void closeWebClient() {
        try {
            request.closeWebClient();
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    /**
     * 执行js代码
     */
    public String executeJs(HtmlPage rootPage, String js) {
        return executeJs(rootPage, js, false);
    }

    /**
     * 执行js代码
     */
    public String executeJs(HtmlPage rootPage, String js, boolean isPage) {
        WebClient webClient = null;
        try {
            webClient = WebClientPool.getInstance().getClient();
            if (webClient != null) { //打开js报错开关，为了采集错误信息
                webClient.getOptions().setThrowExceptionOnScriptError(true);
            }
            JsResult jsResult = request.executeJs(rootPage, js, isPage);
            task.setMessage(task.getMessage() + "|" + jsResult.getMessage());
            return jsResult.getResult();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        } finally {
            if (webClient != null) {
                webClient.getOptions().setThrowExceptionOnScriptError(false);
            }
        }
        return "";
    }
}
