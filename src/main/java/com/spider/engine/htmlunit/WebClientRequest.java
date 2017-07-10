package com.spider.engine.htmlunit;

import com.spider.proxy.ProxyBean;
import com.spider.engine.AbstractRequest;
import com.spider.model.DataInfo;
import com.spider.model.JsResult;
import com.spider.util.CharacterUtils;
import com.spider.util.FreeMarkerUtil;
import com.spider.util.ThreadUtils;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.ScriptResult;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.UrlUtils;
import org.apache.commons.codec.Charsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * WebClient请求
 */
public class WebClientRequest extends AbstractRequest<Page> {
    private final Logger LOG = LoggerFactory.getLogger(WebClientRequest.class);
    private final WebClientPool pool = WebClientPool.getInstance();


    @Override
    protected Page sendRequest(DataInfo dataInfo) throws Exception {
        Page rootPage = null;
        WebClient webClient = pool.getClient();
        if (webClient != null) {
            setJavaScriptEnabled(webClient, dataInfo.getJavaScriptEnabled());
            WebRequest webRequest = new WebRequest(UrlUtils.toUrlUnsafe(dataInfo.getUrl()));
            webRequest.setCharset(Charsets.UTF_8);
            setRequestHeader(webClient, dataInfo.getHeader());
            //设置单次请求的代理
            ProxyBean proxyBean = dataInfo.getProxyBean();
            if (proxyBean != null) {
                webRequest.setProxyHost(proxyBean.getIp());
                webRequest.setProxyPort(proxyBean.getPort());
            }
            rootPage = webClient.getPage(webRequest);
            // 有配置超时时间，加入停顿的超时时间
            if (dataInfo.getTimeout() != null && dataInfo.getTimeout() != 0) {
                ThreadUtils.sleepThreadByTimeUnit(TimeUnit.MILLISECONDS, dataInfo.getTimeout());
            }
        }
        return rootPage;
    }

    /**
     * 设置请求头参数
     */
    private void setRequestHeader(WebClient webClient, Map<String, String> headerMap) {
        if (webClient != null && headerMap != null) {
            for (Map.Entry<String, String> headEntry : headerMap.entrySet()) {
                String key = headEntry.getKey();
                String value = headEntry.getValue();
                // 启用cookie管理器
                if ("cookie".equalsIgnoreCase(key)) {
                    webClient.getCookieManager().setCookiesEnabled(true);
                } else {
                    webClient.addRequestHeader(key, value);
                }
            }
        }
    }

    /**
     * 执行js代码，是否包含分页
     */
    public JsResult executeJs(HtmlPage rootPage, String js, boolean isPage) {
        JsResult jsResult = new JsResult();
        try {
            if (rootPage != null) {
                setJavaScriptEnabled();
                if (isPage || js.startsWith(JQUERY_PREFIX)) {
                    String jquery = FreeMarkerUtil.analysisTemplate("jquery.ftl", null);
                    rootPage.executeJavaScript(jquery); // 加入jquery库

                    //截取jquery的实际语句
                    if (js.startsWith(JQUERY_PREFIX)) {
                        js = js.replace(JQUERY_PREFIX, "");
                    }
                }
                ScriptResult scriptResult = rootPage.executeJavaScript(js);
                if (scriptResult != null && scriptResult.getJavaScriptResult() != null) {
                    String result = scriptResult.getJavaScriptResult().toString();
                    if (CharacterUtils.isMessyCode(result) || result.contains("net.sourceforge.htmlunit.corejs.javascript.Undefined")) {
                        jsResult.setMessage("");
//                        String encoding = rootPage.getPageEncoding();
//                        result = new String(result.getBytes(), encoding);
//                        if (CharacterUtils.isMessyCode(result)) {
//                            encoding = CharsetDetector.guessEncoding(result.getBytes());
//                            result = new String(result.getBytes(), encoding);
//                            if (CharacterUtils.isMessyCode(result)) {
//                                return "";
//                            }
//                        }
                    } else {
                        jsResult.setResult(result);
                    }
                } else {
                    jsResult.setMessage("js execute error");
                    LOG.error("[{}] js execute error [{}]", rootPage.getUrl(), js);
                }
                rootPage.cleanUp();
            }
        } catch (Exception ex) {
            jsResult.setThrowable(ex);
            jsResult.setMessage(ex.getMessage());
            LOG.error("[{}] js execute error [{}] , detail [{}]", rootPage.getUrl(), js, ex.getMessage());
        }
        return jsResult;
    }

    /**
     * 执行js代码
     */
    public JsResult executeJs(HtmlPage rootPage, String js) {
        return executeJs(rootPage, js, false);
    }


    /**
     * 关闭浏览
     */
    public void closeWebClient() throws Exception {
        WebClient webClient = pool.getClient();
        if (webClient != null) {
            try {
//                setJavaScriptEnabled(webClient, false);
//                removeAllHeader(webClient);
                webClient.close();
                pool.returnClient();
            } catch (Exception ex) {
                LOG.error(ex.getMessage(), ex);
            }
        }
    }

    /**
     * 浏览器头部设置的参数
     */
    private void removeAllHeader(WebClient webClient) {
        if (webClient != null) {
            for (String key : header.keySet()) {
                webClient.removeRequestHeader(key);
            }
        }
    }


    /**
     * 启动js引擎
     */
    protected void setJavaScriptEnabled(WebClient webClient, boolean enabled) {
        if (webClient != null) {
            webClient.getOptions().setJavaScriptEnabled(enabled);
        }
    }

    /**
     * 启动js引擎
     */
    protected void setJavaScriptEnabled() throws Exception {
        WebClient webClient = pool.getClient();
        setJavaScriptEnabled(webClient, true);
    }

}
