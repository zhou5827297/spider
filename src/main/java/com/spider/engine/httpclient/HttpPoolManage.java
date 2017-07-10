package com.spider.engine.httpclient;

import com.spider.config.ProxyConstant;
import com.spider.proxy.ProxyBean;
import com.spider.util.ThreadUtils;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * http连接池
 */
public class HttpPoolManage {

    private static final Logger LOG = LogManager.getLogger(HttpPoolManage.class);

    private static final int TIME_OUT = ProxyConstant.TIMEOUT_READ;

    private static final int CONNECT_TIME_OUT = ProxyConstant.TIMEOUT_CONNECT;

    private static PoolingHttpClientConnectionManager CONNECTIONMANAGER;

    private static final String CONTENTTYPE_JSON = "application/json";

    private static final String CHARSET_UTF_8 = "UTF-8";

    private static HttpClient HTTPCLIENTPROXY;

    private static HttpClient HTTPCLIENTPUSH;


    static {
        init();
    }

    private static void init() {
        try {
            LayeredConnectionSocketFactory sslsf = null;
            try {
                sslsf = new SSLConnectionSocketFactory(SSLContext.getDefault());
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("https", sslsf)
                    .register("http", PlainConnectionSocketFactory.getSocketFactory())
                    .build();
            CONNECTIONMANAGER = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
            // 设置最大连接数
            CONNECTIONMANAGER.setMaxTotal(8000);
            // 设置每个路由最大连接数
            CONNECTIONMANAGER.setDefaultMaxPerRoute(8000);
            // 超时时间
            SocketConfig socketConfig = SocketConfig.custom().setSoTimeout(TIME_OUT).build();
            CONNECTIONMANAGER.setDefaultSocketConfig(socketConfig);
            // 默认编码
            ConnectionConfig connectionConfig = ConnectionConfig.custom().setCharset(Charset.forName(CHARSET_UTF_8)).build();
            CONNECTIONMANAGER.setDefaultConnectionConfig(connectionConfig);


//            CLIENTPARAMS = new HttpClientParams();
//            CLIENTPARAMS.setCookiePolicy(CookiePolicy.IGNORE_COOKIES);
//            CLIENTPARAMS.setSoTimeout(TIME_OUT);
//            CLIENTPARAMS.setConnectionManagerTimeout(CONNECT_TIME_OUT);
//            CLIENTPARAMS.setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");

            HTTPCLIENTPROXY = getHttpClient(null);
            HTTPCLIENTPUSH = getHttpClient(null);

            ThreadUtils.executeQuertz(new Runnable() {
                @Override
                public void run() {
                    dispose();
                }
            }, 2, 10, TimeUnit.MINUTES);
        } catch (Exception e) {
            LOG.error("http连接池加载失败", e);
        }
    }

    /**
     * 释放连接池中连接,其实释放不了
     */
    public static void dispose(HttpClient httpClient) {
        //httpClient.getHttpConnectionManager().closeIdleConnections(30000);
    }

    /**
     * 强制释放连接池中连接
     */
    public static void dispose() {
        CONNECTIONMANAGER.closeIdleConnections(30, TimeUnit.SECONDS);
    }

    /**
     * 关闭连接池
     */
    public static void shutdown() {
        CONNECTIONMANAGER.shutdown();
    }

    /**
     * 获取一条http连接
     *
     * @param proxyBean 代理对象
     */
    public static HttpClient getHttpClient(ProxyBean proxyBean) {
        HttpClientBuilder builder = HttpClients.custom().setConnectionManager(CONNECTIONMANAGER);
        HttpRequestRetryHandler requestRetryHandler = new HttpRequestRetryHandler() {
            @Override
            public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
                if (executionCount >= 5) {
                    //如果超过最大重试次数，则不要重试
                    return false;
                }
                if (exception instanceof InterruptedIOException) {
                    // 时间到
                    return false;
                }
                if (exception instanceof UnknownHostException) {
                    // 未知主机
                    return false;
                }
                if (exception instanceof ConnectTimeoutException) {
                    // 拒绝连接
                    return false;
                }
                if (exception instanceof SSLException) {
                    // SSL握手异常
                    return false;
                }
                HttpClientContext clientContext = HttpClientContext.adapt(context);
                HttpRequest request = clientContext.getRequest();
                boolean idempotent = !(request instanceof HttpEntityEnclosingRequest);
                if (idempotent) {
                    //如果请求被认为是幂等的，请重试
                    return true;
                }
                return false;
            }
        };
        builder.setRetryHandler(requestRetryHandler);
        if (proxyBean != null) {
            HttpHost proxy = new HttpHost(proxyBean.getIp(), proxyBean.getPort());
            DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
            builder.setRoutePlanner(routePlanner);
        }
        HttpClient httpClient = builder.build();
        /*CloseableHttpClient httpClient = HttpClients.createDefault();//如果不采用连接池就是这种方式获取连接*/
        return httpClient;
    }

    /**
     * 释放当前连接到连接池中
     *
     * @param httpMethod 请求对象
     */
    public static void disposeHttpClient(HttpRequestBase httpMethod) {
        if (httpMethod != null) {
            httpMethod.releaseConnection();
        }
    }


    /**
     * 简单发送get请求
     */
    public static String sendGet(String url) {
        HttpGet method = null;
        String response = null;
        try {
//            HttpClient httpClient = getHttpClient(null);
            method = new CustomGetMethod(url);
            RequestConfig config = HttpPoolManage.getCommonBuilder().build();
            method.setConfig(config);
            method.addHeader("Accept-Encoding", "gzip, deflate, sdch");
            HttpResponse httpResponse = HTTPCLIENTPROXY.execute(method);
            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = httpResponse.getEntity();
                response = EntityUtils.toString(entity);
            }

        } catch (Exception ex) {
            LOG.error("deal http-get error", ex);
        } finally {
            disposeHttpClient(method);
        }
        return response;
    }

    /**
     * 获取一个builder对象
     */
    public static RequestConfig.Builder getCommonBuilder() {
        RequestConfig.Builder builder = RequestConfig.custom()
                .setCookieSpec(CookieSpecs.IGNORE_COOKIES)
                .setRedirectsEnabled(true)
                .setConnectionRequestTimeout(CONNECT_TIME_OUT)
                .setConnectTimeout(CONNECT_TIME_OUT)
                .setSocketTimeout(TIME_OUT);
        return builder;
    }


    /**
     * 简单发送get请求
     */
    public static String sendGet(String url, Map<String, List<String>> args) {
        HttpGet method = null;
        String response = null;
        try {
            StringBuffer sb = new StringBuffer(url);
            if (args != null) {
                sb.append("?");
                for (Map.Entry<String, List<String>> entry : args.entrySet()) {
                    String key = entry.getKey();
                    List<String> values = entry.getValue();
                    if (values != null) {
                        for (String value : values) {
                            sb.append(key + "=" + value + "&");
                        }
                    }
                }
            }
            HttpClient httpClient = getHttpClient(null);
            method = new CustomGetMethod(sb.toString());
            method.addHeader("Accept-Encoding", "gzip, deflate, sdch");
            HttpResponse httpResponse = httpClient.execute(method);
            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = httpResponse.getEntity();
                response = EntityUtils.toString(entity);
            }
        } catch (Exception ex) {
            LOG.error("deal http-get error", ex);
        } finally {
            disposeHttpClient(method);
        }
        return response;
    }

    /**
     * 发送post请求
     */
    public static String sendPost(String url, String json) {
        HttpPost method = null;
        String response = null;
        try {
//            HttpClient httpClient = getHttpClient(null);
            method = new CustomPostMethod(url);
//            ByteArrayOutputStream originalContent = new ByteArrayOutputStream();
//            originalContent.write(json.getBytes(Charset.forName("UTF-8")));
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            GZIPOutputStream gzipOut = new GZIPOutputStream(baos);
//            originalContent.writeTo(gzipOut);
//            gzipOut.finish();
//            method.setRequestEntity(new ByteArrayRequestEntity(baos.toByteArray(), CONTENTTYPE_JSON));

            StringEntity stringEntity = new StringEntity(json, ContentType.create(CONTENTTYPE_JSON, CHARSET_UTF_8));
            stringEntity.setChunked(false);

            method.setEntity(stringEntity);
            HttpResponse httpResponse = HTTPCLIENTPUSH.execute(method);
            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = httpResponse.getEntity();
                response = EntityUtils.toString(entity);
            }
        } catch (Exception ex) {
            LOG.error("deal http-post error", ex);
        } finally {
            disposeHttpClient(method);
        }
        return response;
    }


    public static void main(String[] args) {
//        System.out.println(sendPost("http://xxxxx/spider/article", "{\n" +
//                "  \"url\": \"http://www.zhoukai.com/2\",\n" +
//                "  \"title\": \"zhoukai-article-test-02\",\n" +
//                "  \"sourceId\": \"3016825468199035\",\n" +
//                "  \"head\": \"2016-10-08 08:41中国广播网\",\n" +
//                "  \"publishTime\": \"2016-10-08 08:41:00\",\n" +
//                "  \"author\": \"中国广播网\",\n" +
//                "  \"body\": \"　8月23日，国务院办公厅再出手，印发《关于建立国有企业违规经营投资责任追究制度的意见》（以下简称《意见》），这是继8月18日国资委、财政部和证监会联合印发的《关于国有控股混合所有制企业开展员工持股试点的意见》（以下简称《试点意见》）后又一项与国企改革配套的重要政策。两份《意见》先后相继出台，前一个针对权力的约束，后一个针对职工的激励，可以看到国企改革已经到了一个非常重要的节点，国家首先在制度和政策层面力求让国企“活”起来，为国企改革保驾护航。,\"\n" +
//                "}"));

        System.out.println(sendGet("http://www.sxrd.gov.cn/userfiles/ueditor/image/20170516/1494919451078068671.jpg"));
//        System.out.println(sendGet("http://192.168.0.127:8080/proxy/proxy"));
    }
}
