package com.spider.engine.htmlunit;

import com.gargoylesoftware.htmlunit.WebClient;

/**
 * 浏览器池
 */
public class WebClientPool {

    private static final WebClientPool INSTANCE = new WebClientPool();
    //    private final GenericObjectPool clientPool = new GenericObjectPool();
    private final WebClientFactory webClientFactory = new WebClientFactory();
    private final ThreadLocal<WebClient> local = new ThreadLocal<WebClient>();


    public WebClientPool() {
//        clientPool.setMaxWait(ProxyConstant.POOL_MAXWAIT);
//        clientPool.setMaxActive(ProxyConstant.POOL_MAXACTIVE);
//        clientPool.setMaxIdle(ProxyConstant.POOL_MAXACTIVE / 2);
//        clientPool.setMinIdle(ProxyConstant.POOL_MAXACTIVE / 5);
//        clientPool.setFactory(new PoolableObjectFactory() {
//            @Override
//            public Object makeObject() throws Exception {
//                WebClient client = webClientFactory.getWebClient(null);
//                return client;
//            }
//
//            @Override
//            public void destroyObject(Object obj) throws Exception {
//                WebClient client = (WebClient) obj;
//                client.closeAllWindows();
//                client = null;
//            }
//
//            @Override
//            public boolean validateObject(Object obj) {
//                return false;
//            }
//
//            @Override
//            public void activateObject(Object obj) throws Exception {
//
//            }
//
//            @Override
//            public void passivateObject(Object obj) throws Exception {
//
//            }
//        });
    }

    /**
     * 单例获取
     */
    public static WebClientPool getInstance() {
        return INSTANCE;
    }

    /**
     * 从池中获取一个浏览器
     */
    public WebClient getClient() throws Exception {
        WebClient webClient = local.get();
        if (webClient == null) {
//            webClient = (WebClient) this.clientPool.borrowObject();
            webClient = webClientFactory.getWebClient(null);
            local.set(webClient);
        } else {
            //防止该线程上轮被强制取消，对象未释放掉
            webClient.close();
        }
        return webClient;
    }

    /**
     * 归还到池中
     */
    public void returnClient() throws Exception {
        WebClient webClient = local.get();
        webClient = null;
//        this.clientPool.returnObject(webClient);
        local.remove();
    }

}
