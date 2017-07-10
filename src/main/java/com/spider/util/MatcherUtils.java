package com.spider.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则匹配取值
 */
public class MatcherUtils {

    /**
     * 正则匹配取值
     */
    public static String matcher(String reg, String content) {
        try {
            if (reg == null || reg.length() == 0) {
                return content;
            }
            Pattern pattern = Pattern.compile(reg);
            Matcher matcher = pattern.matcher(content);
            if (matcher.find()) {
                return matcher.group(1);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return content;
    }
}
