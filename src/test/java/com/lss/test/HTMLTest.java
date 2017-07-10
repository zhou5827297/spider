package com.lss.test;

/**
 * Created by zhoukai on 2017/1/5.
 */
public class HTMLTest {
   private final static  ThreadLocal<String> LOCAL = new ThreadLocal<String>();
    public static void main(String[] args) throws Exception {
//        String s = "<p><span id=\"embed_playerid\" style=\"text-align:center\"><embed type=\"application/x-shockwave-flash\" src=\"http://player.cntv.cn/standard/cntvplayer20160226.swf?v=70.6122653387671335\" width=\"540\" height=\"400\" id=\"v_player\" name=\"v_player\" bgcolor=\"#000000\" quality=\"high\" wmode=\"opaque\" menu=\"false\" allowfullscreen=\"true\" allowscriptaccess=\"always\" flashvars=\"referrer=http://news.cctv.com/&amp;dynamicDataPath=http://vdn.apps.cntv.cn/api/getHt…mp;videoType=0&amp;videoEditMode=1&amp;isAutoPlay=true&amp;languageConfig=\"></span><script type=\"text/javascript\" src=\"http://js.player.cntv.cn/innerShare.js\"></script><script type=\"text/javascript\" src=\"http://js.player.cntv.cn/creator/swfobject.js\"></script><script type=\"text/javascript\">var fo = createPlayer(\"v_player\",540,400);fo.addVariable(\"videoId\",\"vid\");fo.addVariable(\"videoCenterId\",\"9c5edf7b903f456a99e4195ac6f522a8\");fo.addVariable(\"videoType\",\"0\");fo.addVariable(\"videoEditMode\",\"1\");fo.addVariable(\"isAutoPlay\",\"true\");fo.addVariable(\"tai\",\"news\");fo.addVariable(\"languageConfig\",\"\");fo.addParam(\"wmode\",\"opaque\");writePlayer(fo,\"embed_playerid\");</script></p><p>　　<strong>央视网消息：</strong>日前，在广东佛山火车站，安检人员人员发现一名男旅客身上和随身携带的包里竟然藏有子弹！特警立即将这名旅客控制，将他带回派出所进一步搜查处理。</p><p>　　经铁路民警询问，这名旅客是佛山市一家影视城剧组工作人员，负责拍摄现场道具的发放。</p><p style=\"text-align: center;\"><img src=\"http://p1.img.cctvpic.com/photoworkspace/contentimg/2017/01/06/2017010616301531157.jpg\" alt=\"道具子弹\" width=\"500\" isflag=\"1\"></p><p style=\"text-align: center;\"><span style=\"font-family: KaiTi_GB2312;\">道具子弹</span></p><p>　　旅客解释：在拍摄现场用剩下的，放在兜里，忘了拿出来，掉在箱子上了，也没检查，就咔咔一装箱带出来了。</p><p>　　原来这名旅客把剧场用的道具用弹，遗忘在衣服及旅行包里，当天他跟随剧组乘坐火车前往北京，过安检时被检查出带有子弹。</p><p style=\"text-align: center;\"><img src=\"http://p1.img.cctvpic.com/photoworkspace/contentimg/2017/01/06/2017010616303649748.jpg\" alt=\"没收旅客的危险品\" width=\"500\" isflag=\"1\"></p><p style=\"text-align: center;\"><span style=\"font-family: KaiTi_GB2312;\">没收旅客的危险品</span></p><p>　　佛山铁路警方介绍，个别旅客对危险品的认识有待提高，虽多次提醒，但旅客携带危险品进站的情况仍时有发生。仅今年以来，佛山铁路公安处就查处各类危险品2446起，大部分是管制刀具。</p>";
//        s = s.replace("&nbsp;", "").trim();
//        s = HtmlUtil.trimInner(s, "script");
//        s = HtmlUtil.trimComment(s);
//        s = HtmlUtil.trimWithoutTag(s, "p|img|table|tr|td|br");
//        s = HtmlUtil.trimEmptyTag(s);
//        System.out.println(s);

//        String content = " <div class=\"pagenum\" id=\"pagenum\">1/2</div> ";
//        content =JsoupUtils.dealAbsImg("http://192.168.0.127:5678/body.html?#/articleId=3020876465948067",content);
//        System.out.println(content);
//        content =StringEscapeUtils.unescapeHtml4(content);
//        System.out.println(content);
       LOCAL.set("document.getElementById('104').innerHTML.replace(/&lt;/g,'<').replace(/&gt;/g,'>').replace(/\\<record\\>\\<\\!\\[CDATA\\[/g,'').replace(/\\]\\]\\>\\<\\/record\\>/g,'')");
        System.out.println(LOCAL.get());
        LOCAL.remove();
        System.out.println(LOCAL.get());

    }
}
