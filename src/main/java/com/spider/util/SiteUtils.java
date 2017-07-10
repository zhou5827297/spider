package com.spider.util;

import com.spider.model.Site;
import common.base.util.DateUtils;
import common.base.util.JsonUtil;
import common.base.util.StringUtil;

import java.util.Map;

/**
 * 站点文件特殊处理
 */
public class SiteUtils {

    /**
     * 替换文件中的替换符
     */
    public static void analyserReplace(Site site) {
        String replaceArgs = site.getReplaceArgs();
        if (StringUtil.isEmpty(replaceArgs)) {
            return;
        }
        Map<?, ?> mapArgs = JsonUtil.readJsonMap(replaceArgs);
        if (mapArgs == null) {
            return;
        }
        for (Map.Entry<?, ?> entry : mapArgs.entrySet()) {
            String key = entry.getKey().toString();
            String value = entry.getValue().toString();
            if (key.equals("NOWDATE")) {
                String realValue = DateUtils.getDateStr(DateUtils.getCurrentDate(), value);
                replaceArgs = replaceArgs.replace(value, realValue);
            }
        }

        site.setReplaceArgs(replaceArgs);
        mapArgs = JsonUtil.readJsonMap(replaceArgs);

        // 替换配置文件中的替换符号
        site.setContext(replaceText(mapArgs, site.getContext()));
        site.setIndex(replaceText(mapArgs, site.getIndex()));
    }

    private static String replaceText(Map<?, ?> mapArgs, String text) {
        for (Map.Entry<?, ?> entry : mapArgs.entrySet()) {
            String key = entry.getKey().toString();
            String value = entry.getValue().toString();
            text = text.replace("${" + key + "}", value);
        }
        return text;
    }

    public static String replaceText(String replaceArgs, String text) {
        Map<?, ?> mapArgs = JsonUtil.readJsonMap(replaceArgs);
        if (mapArgs == null) {
            return text;
        }
        for (Map.Entry<?, ?> entry : mapArgs.entrySet()) {
            String key = entry.getKey().toString();
            String value = entry.getValue().toString();
            text = text.replace("${" + key + "}", value);
        }
        return text;
    }
}
