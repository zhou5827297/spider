package com.spider.dubbo;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.spider.dubbo.service.SpiderSyncService;
import com.spider.dubbo.service.SpiderSyncService;
import com.spider.util.ApplicationContextUtils;
import com.spider.util.ThreadUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * DubboService路由器
 */
@Component
public class DubboServiceRouter implements InitializingBean {

    @Autowired
    private ApplicationConfig applicationConfig;
    @Autowired
    private RegistryConfig registry;

    private ReferenceConfig<SpiderSyncService> referenceConfig = null;
    private SpiderSyncService spiderSyncService = null;

    private final static int TIMEOUT = 30000;

    @Value("${DUBBO.FETCH.ADDRESS}")
    private String dubboUrl;

    /**
     * 刷新dubbo服务
     */
    public synchronized void refresh(String remoteAddr) {
        if (referenceConfig != null) {
            referenceConfig.destroy();
            referenceConfig = null;
            spiderSyncService = null;
        }
        createService(remoteAddr);
        if (spiderSyncService == null) {
            createService(dubboUrl);
        } else {
            this.setDubboUrl(remoteAddr);
        }
    }


    /**
     * 设置必要的dubbo服务参数
     */
    public void createService(String remoteAddr) {
        // 此实例很重，封装了与注册中心的连接以及与提供者的连接，请自行缓存，否则可能造成内存和连接泄漏
        if (referenceConfig == null) {
            referenceConfig = new ReferenceConfig<SpiderSyncService>();
            referenceConfig.setApplication(applicationConfig);
            referenceConfig.setRegistry(registry);
            referenceConfig.setInterface(SpiderSyncService.class);
            referenceConfig.setCheck(false);
            referenceConfig.setTimeout(TIMEOUT);
            referenceConfig.setUrl(remoteAddr);
            spiderSyncService = referenceConfig.get();
        }
    }

    /**
     * 获取spider的dubboservice
     */
    public SpiderSyncService getSpiderService() {
        return spiderSyncService;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        createService(dubboUrl);
    }


    public String getDubboUrl() {
        return dubboUrl;
    }

    public void setDubboUrl(String dubboUrl) {
        this.dubboUrl = dubboUrl;
    }

    public static void main(String[] args) {
        ApplicationContextUtils.getContext();
        DubboServiceRouter router = ApplicationContextUtils.getBean(DubboServiceRouter.class);
        SpiderSyncService spiderSyncService = router.getSpiderService();
        try {
            System.out.println(spiderSyncService.listUrl("create_time desc", 1, 10000));
        } catch (Exception e) {
            e.printStackTrace();
            router.refresh(router.dubboUrl);
        }
        ThreadUtils.sleepThreadSeconds(10000);
    }
}
