package com.spider.util;

import common.base.util.DateUtils;
import common.base.util.JsonUtil;

import java.util.Map;

/**
 * url解析
 */
public class UrlUtils {

    /**
     * 替换URL中的替换符
     *
     * @param url  http://www.baidu.com/?name=${name}
     * @param args {"name":"zhoukai"}
     * @return http://www.baidu.com/?name=zhoukai
     */
    public static String analyserReplace(String url, String args) {
        Map<?, ?> mapArgs = JsonUtil.readJsonMap(args);
        if (mapArgs == null) {
            return url;
        }
        String tempUrl = url;
        for (Map.Entry<?, ?> entry : mapArgs.entrySet()) {
            String key = entry.getKey().toString();
            String value = entry.getValue().toString();
            if (value.equals("NOWDATE")) {
                value = DateUtils.getDateStr(DateUtils.getCurrentDate(), value);
            }
            tempUrl = tempUrl.replace("${" + key + "}", value);
        }
        return tempUrl;
    }
}
