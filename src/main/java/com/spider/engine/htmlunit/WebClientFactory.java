package com.spider.engine.htmlunit;

import com.spider.config.ProxyConstant;
import com.spider.proxy.ProxyBean;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.IncorrectnessListener;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HTMLParserListener;
import org.apache.commons.logging.LogFactory;
import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.CSSParseException;

import java.net.URL;
import java.util.logging.Level;

/**
 * 浏览器工厂
 */
public class WebClientFactory {

    private final static int TIMEOUT = ProxyConstant.TIMEOUT_READ;
    private final static int JAVASCRIPTTIMEOUT = ProxyConstant.JS_EXECUTE_TIMEOUT_READ;

    /**
     * 构建一个浏览器对象
     */
    protected WebClient getWebClient(ProxyBean proxyBean) {
        LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");
        java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
//        WebClient webClient = new WebClient(SpiderUtils.getBrowserVersion());
        WebClient webClient = new WebClient(BrowserVersion.FIREFOX_45);
        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
        webClient.getOptions().setTimeout(TIMEOUT);
        webClient.waitForBackgroundJavaScript(JAVASCRIPTTIMEOUT);
        webClient.setJavaScriptTimeout(JAVASCRIPTTIMEOUT * 2);
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setRedirectEnabled(true);
        webClient.getOptions().setUseInsecureSSL(true);
        webClient.getCookieManager().setCookiesEnabled(false);
        webClient.setIncorrectnessListener(new IncorrectnessListener() {
            @Override
            public void notify(String arg0, Object arg1) {
            }
        });
        webClient.setCssErrorHandler(new org.w3c.css.sac.ErrorHandler() {
            @Override
            public void warning(CSSParseException arg0) throws CSSException {
            }

            @Override
            public void fatalError(CSSParseException arg0) throws CSSException {
            }

            @Override
            public void error(CSSParseException arg0) throws CSSException {
            }
        });
        webClient.setHTMLParserListener(new HTMLParserListener() {
            @Override
            public void warning(String arg0, URL arg1, String arg2, int arg3, int arg4, String arg5) {
            }

            @Override
            public void error(String arg0, URL arg1, String arg2, int arg3, int arg4, String arg5) {
            }
        });
        return webClient;
    }

}
