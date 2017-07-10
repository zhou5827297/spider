package sxrd;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.HTMLParserListener;
import com.gargoylesoftware.htmlunit.util.UrlUtils;
import common.base.util.JsonUtil;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.LogFactory;
import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.CSSParseException;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.logging.Level;

/**
 * Created by zhoukai on 2017/5/17.
 */
public class PictureDownload {

    public static void main(String[] args) throws Exception {
        String path = "http://www.sxrd.gov.cn/userfiles/ueditor/image/20170516/1494919435453064469.jpg";

        LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");
        java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
        WebClient webClient = new WebClient(BrowserVersion.FIREFOX_45);
        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
        webClient.getOptions().setTimeout(5 * 1000);
        webClient.waitForBackgroundJavaScript(10 * 1000);
        webClient.setJavaScriptTimeout(10 * 1000);
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setRedirectEnabled(true);
        webClient.getOptions().setUseInsecureSSL(true);
        webClient.getCookieManager().setCookiesEnabled(true);

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
        WebRequest webRequest = new WebRequest(UrlUtils.toUrlUnsafe(path));
        webRequest.setCharset(Charset.defaultCharset());

        //===================================ip被封，会用到代理=======================================
//        String proxyStr = sendGet("http://192.168.0.127:8080/proxy/proxy");
//        Map<String, Object> proxy = (Map<String, Object>) JsonUtil.readJsonMap(proxyStr);
//        webRequest.setProxyHost(proxy.get("ip").toString());
//        webRequest.setProxyPort(Integer.parseInt(proxy.get("port").toString()));
        //=================================================================================

        Page page = webClient.getPage(webRequest);
        String type = page.getWebResponse().getContentType();
        System.out.println(type);
        if ("text/html".equals(type)) {
            System.out.println(page.getWebResponse().getContentAsString());
        } else {
            InputStream inputStream = page.getWebResponse().getContentAsStream();
            OutputStream outputStream = new FileOutputStream("d:\\sxrd.jpg");
            IOUtils.copy(inputStream, outputStream);
            inputStream.close();
            outputStream.close();
        }
        webClient.close();
    }

    public static String sendGet(String url) {
        String result = "";
        BufferedReader in = null;
        try {
            URL realUrl = new URL(url);
            URLConnection conn = realUrl.openConnection();
            conn.setConnectTimeout(3 * 1000);
            conn.setReadTimeout(5 * 1000);
            conn.connect();
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += "\n" + line;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;

    }
}
