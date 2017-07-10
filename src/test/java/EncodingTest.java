import com.spider.engine.htmlunit.WebClientFactory;
import com.spider.proxy.ProxyBean;
import com.spider.proxy.ProxyManage;
import com.spider.proxy.impl.ProxyRemoteManage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.UrlUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by zhoukai on 2017/3/7.
 */
public class EncodingTest extends WebClientFactory {

    private static ProxyManage PROXYMANAGE = new ProxyRemoteManage();

    private static String URL = "http://fangchan.qlwb.com.cn/news/2017/quanguo_0410/42525.html";

    public static void main(String[] args) {

        EncodingTest test = new EncodingTest();
        ExecutorService FETCHEREXECUTOR = Executors.newFixedThreadPool(100);

        for (int i = 0; i < 1000; i++) {
            FETCHEREXECUTOR.execute(new Runnable() {
                @Override
                public void run() {
                    WebClient webClient = null;
                    int statusCode = 0;
                    for (int i = 0; i < 10; i++) {
                        try {
                            webClient = test.getWebClient(null);
                            webClient.getOptions().setTimeout(10 * 1000);
                            webClient.waitForBackgroundJavaScript(10 * 1000);


                            WebRequest webRequest = new WebRequest(UrlUtils.toUrlUnsafe(URL));
                            webRequest.setCharset("UTF-8");
                            ProxyBean proxyBean = PROXYMANAGE.getProxyBean();
                            webRequest.setProxyHost(proxyBean.getIp());
                            webRequest.setProxyPort(proxyBean.getPort());
                            webClient.getOptions().setJavaScriptEnabled(false);
                            HtmlPage rootPage = webClient.getPage(webRequest);
//                            String xml = rootPage.getBody().asXml();
//                            System.out.println(rootPage.getPageEncoding() + "====" + (xml.length() > 1000 ? xml.substring(1, 1000) : xml));
                            statusCode = rootPage.getWebResponse().getStatusCode();
                            String charset = rootPage.getWebResponse().getContentCharset().displayName();
                            System.out.println(charset + "=======" + statusCode + "====" + rootPage.getTitleText());
                            if(rootPage.getTitleText().contains("娼")){
                                System.out.println(rootPage.asXml());
                            }
                        } catch (Exception e) {
                            System.out.println(e);
                        } finally {
                            if (webClient != null) {
                                webClient.close();
                            }
                            if (statusCode == 200) {
                                break;
                            }
                        }
                    }
                }
            });
        }
        FETCHEREXECUTOR.shutdown();
    }

}

