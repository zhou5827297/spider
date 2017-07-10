package com.spider.mq.publisher;

import com.spider.util.RedisUtil;
import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 发布
 */
public class Publisher {

    private final String channel = "spider-site";

    private final String json = "{\n" +
            "  \"id\": 3027413142446083,\n" +
            "  \"context\": \"http://www.cpnn.com.cn/\",\n" +
            "  \"index\": \"dlcj/default.htm\",\n" +
            "  \"configs\": [\n" +
            "    {\n" +
            "      \"article\": true,\n" +
            "      \"reg\": \"http://www.cpnn.com.cn/.*?.html\",\n" +
            "      \"js\": {\n" +
            "        \"title\": [\n" +
            "          \"document.getElementsByTagName('h1')[0].textContent.trim()\"\n" +
            "        ],\n" +
            "        \"head\": [\n" +
            "          \"document.getElementsByClassName('cpnn-zhengwen-time')[0].textContent.trim()\"\n" +
            "        ],\n" +
            "        \"author\": [\n" +
            "\n" +
            "          \"document.getElementsByClassName('cpnn-zhengwen-time')[0].textContent.split('来源：')[1].split('日期')[0].trim()\"\n" +
            "        ],\n" +
            "        \"body\": [\n" +
            "          \"document.getElementsByClassName('cpnn-con-zhenwen')[0].innerHTML.split('<p align=\\\"center\\\">')[0]\"\n" +
            "        ],\n" +
            "        \"ptime\": [\n" +
            "          \"'20'+document.getElementsByClassName('cpnn-zhengwen-time')[0].textContent.split('日期：')[1].trim()\"\n" +
            "        ]\n" +
            "      }\n" +
            "    },\n" +
            "    {\n" +
            "      \"article\": false,\n" +
            "      \"uri\": \"dlcj/default.htm\",\n" +
            "      \"date\": {\n" +
            "        \"regex\": \"\\\\d{4}\\\\d{2}\\\\d{2}\",\n" +
            "        \"format\": \"yyyyMMdd\"\n" +
            "      },\n" +
            "\n" +
            "      \"js\": {\n" +
            "        \"links\": [\n" +
            "          \"document.getElementsByClassName('cpnn-content-left-list')[0].getElementsByTagName('ul')[0].innerHTML\"\n" +
            "        ]\n" +
            "      }\n" +
            "    }\n" +
            "  ]\n" +
            "}";

    public void start() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        RedisUtil redis = RedisUtil.getInstance();
        while (true) {
            String line = null;
            try {
                line = reader.readLine();
                if (!"quit".equals(line)) {
                    redis.getJedis().publish(channel, json);
                } else {
                    break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                redis.returnJedis();
                IOUtils.closeQuietly(reader);
            }
        }
    }
}
