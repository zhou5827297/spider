package com.spider.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * 读取Properties文件信息
 */
public class PropertiesUtils {

    /**
     * 读取Properties文件信息
     * 先从文件系统读取，再用classpath路径读取
     */
    public static Properties loadProperties(String path) {
        Properties properties = new Properties();
        //优先从文件系统获取
        FileInputStream inputStream = null;
        try {
            File file = new File(path);
            // 加载不到，从classpath中的路径读
            if (file.exists() == false) {
                String absolutePath = Thread.currentThread().getContextClassLoader().getResource(path).getPath();
                file = new File(absolutePath);
            }
            inputStream = new FileInputStream(file);
            properties.load(inputStream);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return properties;
    }
}
