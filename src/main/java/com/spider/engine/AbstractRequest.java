package com.spider.engine;

import com.spider.model.DataInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * 通用请求抽象类
 */
public abstract class AbstractRequest<T> implements Request<T, DataInfo> {
    /**
     * 头部信息
     */
    protected Map<String, String> header = new HashMap<String, String>();

    /*
     *jquery语法标志前缀
     */
    protected final static String JQUERY_PREFIX = "$jquery$";

    protected AbstractRequest() {
        initCommonHead();
    }

    /**
     * 公共头部信息
     */
    private void initCommonHead() {
        header.put("Accept-Encoding", "gzip, deflate, sdch");
        header.put("Accept-Language", "zh-CN,zh;q=0.8");
    }

    @Override
    public T execute(DataInfo dataInfo) throws Exception {
        beforeDeal(dataInfo);
        T model = sendRequest(dataInfo);
        model = afterDeal(model);
        return model;
    }

    /**
     * 之前处理
     */
    protected void beforeDeal(DataInfo dataInfo) {
        Map<String, String> common = dataInfo.getHeader();
        if (common == null) {
            common = new HashMap<String, String>();
            dataInfo.setHeader(common);
        }
        for (Map.Entry<String, String> headEntry : header.entrySet()) {
            String key = headEntry.getKey();
            if (!common.containsKey(key)) {
                String value = headEntry.getValue();
                common.put(key, value);
            }
        }
    }

    /**
     * 之后处理
     */
    protected T afterDeal(T model) {
        return model;
    }


    /**
     * 发送请求
     */
    protected abstract T sendRequest(DataInfo dataInfo) throws Exception;
}
