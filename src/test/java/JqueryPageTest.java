import com.spider.engine.htmlunit.WebClientFactory;
import com.gargoylesoftware.htmlunit.ScriptResult;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.UrlUtils;

/**
 * Created by zhoukai on 2017/3/7.
 */
public class JqueryPageTest extends WebClientFactory {


    private static String URL = "http://news.xinhuanet.com/mil/2017-06/07/c_129626888.htm";


    private static String JQUERY = "(function(){\n" +
            "    var jquery = document.createElement('script');\n" +
            "    jquery.type= 'text/javascript';\n" +
            "    jquery.src = \"http://libs.baidu.com/jquery/1.11.1/jquery.min.js\";\n" +
            "    document.getElementsByTagName('head')[0].appendChild(jquery);\n" +
            "}())";


    private static String JS = "(function(){\n" +
            "    var body = '';\n" +
            "    for (var i = 0; i <= 10; i = i + 1) {\n" +
            "        var url = 'c_129626888(_${index}).htm';\n" +
            "        if(i == 0){\n" +
            "            url = url.replace(/\\(.*\\)/,'');\n" +
            "        }else{\n" +
            "            url = url.replace(/\\(/,'').replace(/\\)/,'').replace('${index}',i);\n" +
            "        }\n" +
            "        for (var j = 0; j < 3; j = j + 1) {\n" +
            "            var success = false;\n" +
            "            $.ajax({\n" +
            "                url: url,\n" +
            "                async: false,\n" +
            "                success: function (data) {\n" +
            "                    var tagTpl = document.createElement('div');\n" +
            "                    tagTpl.innerHTML = data;\n" +
            "                    var content = (function(){ var strCon=tagTpl.getElementsByClassName('article')[0].getElementsByTagName('img'); for(var i=0;i<strCon.length;i++){ var img=strCon[i].src;strCon[i].src=img;};var a1=tagTpl.getElementsByClassName('article')[0].innerHTML;return a1;})();\n" +
            "                    body += content;\n" +
            "                    success = true;\n" +
            "                }\n" +
            "            });\n" +
            "            if (success) {\n" +
            "                break;\n" +
            "            }\n" +
            "        }\n" +
            "    }\n" +
            "    return body;\n" +
            "}())";

    public static void main(String[] args) {

        JqueryPageTest test = new JqueryPageTest();

        WebClient webClient = null;
        int statusCode = 0;
        for (int i = 0; i < 10; i++) {
            try {
                webClient = test.getWebClient(null);
                webClient.getOptions().setTimeout(10 * 1000);
                webClient.waitForBackgroundJavaScript(10 * 1000);

                webClient.getOptions().setThrowExceptionOnScriptError(true);

                WebRequest webRequest = new WebRequest(UrlUtils.toUrlUnsafe(URL));
                webRequest.setCharset("UTF-8");
//                ProxyBean proxyBean = PROXYMANAGE.getProxyBean();
//                webRequest.setProxyHost(proxyBean.getIp());
//                webRequest.setProxyPort(proxyBean.getPort());
                webClient.getOptions().setJavaScriptEnabled(false);
                HtmlPage rootPage = webClient.getPage(webRequest);
                statusCode = rootPage.getWebResponse().getStatusCode();

                webClient.getOptions().setJavaScriptEnabled(true);
                ScriptResult scriptResult = rootPage.executeJavaScript(JQUERY);


                scriptResult = rootPage.executeJavaScript(JS);
                if (scriptResult != null && scriptResult.getJavaScriptResult() != null) {
                    String result = scriptResult.getJavaScriptResult().toString();
                    System.out.println(result);
//                    System.out.println(((HtmlPage)scriptResult.getNewPage()).asXml());
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
        System.out.println("end=========");
    }

}

