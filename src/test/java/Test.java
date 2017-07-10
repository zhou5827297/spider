import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.spider.dubbo.service.SpiderSyncService;
import com.gargoylesoftware.htmlunit.util.UrlUtils;

import java.net.InetAddress;

/**
 * Created by zhoukai on 2016/11/23.
 */
public class Test {
    public static void main(String[] args) throws Exception {
        String url = "dubbo://xxxxx:2000/com.spider.dubbo.service.SpiderSyncService";//更改不同的Dubbo服务暴露的ip地址&端口

        // 当前应用配置
        ApplicationConfig application = new ApplicationConfig();
        application.setName("spider");

        // 连接注册中心配置
        RegistryConfig registry = new RegistryConfig();
        registry.setAddress("N/A");

// 注意：ReferenceConfig为重对象，内部封装了与注册中心的连接，以及与服务提供方的连接

// 引用远程服务
        ReferenceConfig<SpiderSyncService> reference = new ReferenceConfig<SpiderSyncService>(); // 此实例很重，封装了与注册中心的连接以及与提供者的连接，请自行缓存，否则可能造成内存和连接泄漏
        reference.setApplication(application);
        reference.setRegistry(registry); // 多个注册中心可以用setRegistries()
        reference.setInterface(SpiderSyncService.class);
        reference.setUrl(url);

// 和本地bean一样使用xxxService
        SpiderSyncService spiderSyncService = reference.get(); // 注意：此代理对象内部封装了所有通讯细节，对象较重，请缓存复用
        System.out.println(spiderSyncService.listUrl("create_time desc", 1, 10000));


//        System.out.println(ProxyConstant.PROXY_GET);
        System.out.println(InetAddress.getByName("xxxx").getHostAddress());
//        System.out.println(URIUtil.encodePathQuery("http://www.linuxidc.com/Linux/2016-05/131701.htm?id=123&name=zhoukai&name=周锴","GBK"));
//        System.out.println("zhoukai${abc}ss${abc}".replaceAll("\\$\\{abc\\}","no"));
//        String content ="zhoukai";
//        System.out.println("0".getBytes().length);
//        System.out.println("100000".getBytes().length);
//        System.out.println(InetAddress.getLocalHost().getHostAddress());
//        String content1 = new String(content.getBytes(),"gbk");
//        System.out.println(content1);
//        System.out.println(UrlUtils.toUrlUnsafe("http://xnc.voc.com.cn/cityListId.asp?FId=15748&FCity=衡阳"));

//        System.out.println(new Date().toString());
//        byte[] body =null;
//        Test article = JsonUtil.tryObject(body, Test.class);
//        System.out.println(article);

//        String url = "http://localhost:8888/subscribe/spider/article";
//        int count = 3000;
//        CountDownLatch end = new CountDownLatch(count);
//        for (int i = 0; i < count; i++) {
//            final int index = i;
//            ThreadUtils.EXECUTOR.execute(new Runnable() {
//                @Override
//                public void run() {
//                    String json = "{\n" +
//                            "  \"url\": \"http://house.qianlong.com/shoudufangchan/2016/1008/987198.shtml" + index + "\",\n" +
//                            "  \"title\": \"多城密集出台楼市调控政策 专家：限购政策或持续存在" + index + "\",\n" +
//                            "  \"sourceId\": \"3016825468199035\",\n" +
//                            "  \"head\": \"2016-10-08 08:41中国广播网\",\n" +
//                            "  \"publishTime\": \"2016-10-08 08:41:00\",\n" +
//                            "  \"author\": \"中国广播网\",\n" +
//                            "  \"body\": \"　8月23日，国务院办公厅再出手，印发《关于建立国有企业违规经营投资责任追究制度的意见》（以下简称《意见》），这是继8月18日国资委、财政部和证监会联合印发的《关于国有控股混合所有制企业开展员工持股试点的意见》（以下简称《试点意见》）后又一项与国企改革配套的重要政策。两份《意见》先后相继出台，前一个针对权力的约束，后一个针对职工的激励，可以看到国企改革已经到了一个非常重要的节点，国家首先在制度和政策层面力求让国企“活”起来，为国企改革保驾护航。,\"\n" +
//                            "}";
//                    String res = HttpPoolManage.sendPost(url, json);
//                    System.out.println(res);
//                    end.countDown();
//                }
//            });
//        }
//        try {
//            end.await();
//            ThreadUtils.EXECUTOR.shutdown();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }

}
