/*
 * Copyright (C) 2015 hu
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package com.spider.util;

import common.base.util.StringUtil;
import org.apache.commons.lang3.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * jsoup工具类
 */
public class JsoupUtils {

    /**
     * 标签与属性分隔符
     */
    private final static String SPLIT = "@";
    /**
     * 获取cdata中的内容
     */
    private final static Pattern CDATAPATTERN = Pattern.compile("\\<\\!\\[CDATA\\[(.*)]\\]\\>");

    /**
     * 评论部分选择器解析
     * <a href="http://www.baidu.com/" target="_blank">百度翻译</a>
     * 1：a         -->   百度翻译
     * 2：a@target  -->   _blank
     * 3：a@html    -->   <a href="http://www.baidu.com/" target="_blank">百度翻译</a>
     */
    public static String getFirstValueBySelector(Element element, String selector) {
        if (selector == null || element == null) {
            return "";
        }
        String value = "";
        String[] selectors = selector.split(SPLIT);
        if (selectors.length > 0) {
            Elements elements = element.select(selectors[0]);
            if (elements.size() > 0 && selectors.length > 1) {
                if ("html".equals(selectors[1])) {
                    value = elements.first().outerHtml().trim();
                } else {
                    value = elements.first().attr(selectors[1]).trim();
                }
            } else if (elements.size() > 0) {
                value = elements.first().html().trim();
            }
        }
        return value;
    }


    public static String getValueBySelector(Element element, String selector) {
        StringBuilder builder = new StringBuilder();
        String[] selectors = selector.split("\\+");
        for (String select : selectors) {
            String[] strs = select.split("\\|");
            for (Element pageEle : element.select(strs[0])) {
                String value = getFirstValueBySelector(pageEle, strs[1]);
                if (strs[1].indexOf("abs:") != -1) {
                    value = "<br/><img src ='" + value + "'/>";
                }
                builder.append(value);
            }
        }

        String html = StringEscapeUtils.unescapeHtml4(builder.toString());
        Matcher matcher = CDATAPATTERN.matcher(html);
        if (matcher.find()) {
            html = matcher.group(1);
        }
        return html;
    }

    /**
     * 把html文本中的图片，相对路径转绝对路径
     */
    public static String dealAbsImg(String baseUri, String html) {
        String content = html;
        try {
            Document doc = Jsoup.parse(html);
            doc.setBaseUri(baseUri);
            Elements imgEles = doc.getElementsByTag("img");
            for (Element imgEle : imgEles) {
                String absUrl = imgEle.attr("abs:src");
                if (StringUtil.isEmpty(absUrl)) {
                    absUrl = imgEle.attr("src");
                }
                imgEle.attr("src", absUrl);
                imgEle.attr("title", "");
                imgEle.attr("alt", "");
            }
            content = doc.body().html();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content;
    }


}
