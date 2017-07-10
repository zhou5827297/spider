package com.spider.engine.httpclient;

import com.spider.proxy.ProxyBean;
import com.spider.engine.AbstractRequest;
import com.spider.model.DataInfo;
import com.spider.util.CharacterUtils;
import com.gargoylesoftware.htmlunit.util.UrlUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.util.EntityUtils;

import java.util.Map;

/**
 * http-request请求
 */
public class HttpClientRequest extends AbstractRequest<String> {

    @Override
    protected String sendRequest(DataInfo dataInfo) throws Exception {
        String url = UrlUtils.toUrlUnsafe(dataInfo.getUrl()).toString();
        ProxyBean proxyBean = dataInfo.getProxyBean();
        HttpGet method = null;
        try {
            HttpClient httpClient = HttpPoolManage.getHttpClient(proxyBean);
            method = new CustomGetMethod(url);
            setRequestHeader(method, dataInfo.getHeader());

            RequestConfig config = HttpPoolManage.getCommonBuilder().build();
            method.setConfig(config);
            HttpResponse httpResponse = httpClient.execute(method);
            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = httpResponse.getEntity();
                String content = EntityUtils.toString(entity);
                if (CharacterUtils.isMessyCode(content)) {
                    return null;
                } else {
                    return content;
                }
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
            HttpPoolManage.disposeHttpClient(method);
        }
        return null;
    }

    /**
     * 设置请求头参数
     */

    private void setRequestHeader(HttpRequestBase method, Map<String, String> headerMap) {
        if (method != null && headerMap != null) {
            for (Map.Entry<String, String> headEntry : headerMap.entrySet()) {
                String key = headEntry.getKey();
                String value = headEntry.getValue();
                method.addHeader(key, value);
            }
        }
    }
}
