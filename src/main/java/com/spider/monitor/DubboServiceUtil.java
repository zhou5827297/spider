package com.spider.monitor;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.spider.dubbo.service.SpiderSyncService;
import com.spider.config.RemoteConstant;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 获取dubboservice
 */
public class DubboServiceUtil {

    private static ApplicationConfig APPLICATION = null;
    private static RegistryConfig REGISTRY = null;
    private static ReferenceConfig<SpiderSyncService> SPIDERSYNCSERVICEREFERENCECONFIG = null;
    private static SpiderSyncService SPIDERSYNCSERVICE = null;

    private final static String REMOTEADDR = RemoteConstant.SUBSCRIBE_URL;
    private final static String INTERFACENAME = "com.spider.dubbo.service.SpiderSyncService";

    private static boolean FREASH_OVER = false; //刷新标志，防止多次刷新，由异常去控制

    static {
        APPLICATION = new ApplicationConfig();
        APPLICATION.setName("spider");

        REGISTRY = new RegistryConfig();
        REGISTRY.setAddress("N/A");
    }


    /**
     * 刷新dubbo服务
     */
    public synchronized static void refresh() {
        if (FREASH_OVER) {
            return;
        }
        String dubboAddress = null;
        try {
            dubboAddress = InetAddress.getByName(REMOTEADDR).getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        if (dubboAddress == null) {
            dubboAddress = RemoteConstant.DUBBO_ADDRESS_HOST;
        }
        String remoteAddr = dubboAddress + ":" + RemoteConstant.DUBBO_ADDRESS_PORT;
        SPIDERSYNCSERVICEREFERENCECONFIG.destroy();
        SPIDERSYNCSERVICEREFERENCECONFIG = null;
        createService(remoteAddr);
        FREASH_OVER = true;
    }

    /**
     * 设置必要的dubbo服务参数
     */
    public static void createService(String remoteAddr) {
        // 此实例很重，封装了与注册中心的连接以及与提供者的连接，请自行缓存，否则可能造成内存和连接泄漏
        if (SPIDERSYNCSERVICEREFERENCECONFIG == null) {
            SPIDERSYNCSERVICEREFERENCECONFIG = new ReferenceConfig<SpiderSyncService>();
            SPIDERSYNCSERVICEREFERENCECONFIG.setApplication(APPLICATION);
            SPIDERSYNCSERVICEREFERENCECONFIG.setRegistry(REGISTRY);
            SPIDERSYNCSERVICEREFERENCECONFIG.setInterface(SpiderSyncService.class);
            SPIDERSYNCSERVICEREFERENCECONFIG.setCheck(false);

            String url = "dubbo://" + remoteAddr + "/" + INTERFACENAME;
            SPIDERSYNCSERVICEREFERENCECONFIG.setUrl(url);
            SPIDERSYNCSERVICE = SPIDERSYNCSERVICEREFERENCECONFIG.get();
        }
    }


    /**
     * 获取spider的dubboservice
     */
    public static SpiderSyncService getSpiderService() {
        if (SPIDERSYNCSERVICE == null) {
            String remoteAddr = RemoteConstant.DUBBO_ADDRESS_HOST + ":" + RemoteConstant.DUBBO_ADDRESS_PORT;
            createService(remoteAddr);
        }
        return SPIDERSYNCSERVICE;
    }

    public static void main(String[] args) {
        SpiderSyncService spiderSyncService = DubboServiceUtil.getSpiderService();
        try {
            System.out.println(spiderSyncService.listUrl("create_time desc", 1, 10000));
        } catch (Exception e) {
            e.printStackTrace();
            DubboServiceUtil.refresh();
        }
        System.out.println(DubboServiceUtil.getSpiderService().listUrl("create_time desc", 1, 10000));

    }
}
