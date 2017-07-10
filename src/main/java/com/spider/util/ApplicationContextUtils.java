package com.spider.util;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


/**
 * 单机容器获取
 */
public class ApplicationContextUtils {
    private static ApplicationContext CONTEXT = null;

    public static ApplicationContext getContext() {
        if (CONTEXT == null) {
            String[] paths = {"classpath*:context/applicationContext.xml"};
            CONTEXT = new ClassPathXmlApplicationContext(paths);
        }
        return CONTEXT;
    }

    public static <T> T getBean(Class<T> className) {
        return getContext().getBean(className);
    }

    public static <T> void setBean(Class<T> className, T model) {
        model = getContext().getBean(className);
    }

    public static Object getBean(String beanName) {
        return getContext().getBean(beanName);
    }


}
