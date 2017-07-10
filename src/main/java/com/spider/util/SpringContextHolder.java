package com.spider.util;

import com.spider.config.RemoteConstant;
import com.spider.mock.ProcessServiceImpl;
import com.spider.mock.SiteServiceImpl;
import com.spider.mock.TaskPushServiceImpl;
import com.spider.mock.TaskServiceImpl;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class SpringContextHolder implements ApplicationContextAware, BeanPostProcessor {
	private static ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		SpringContextHolder.applicationContext = applicationContext;
	}

	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public static Object getBean(String beanName) {
		return applicationContext.getBean(beanName);
	}

	public static <T> T getBean(String beanName, Class<T> clazz) {
		return applicationContext.getBean(beanName, clazz);
	}

	public static <T> T getBean(Class<T> clazz) {
		return applicationContext.getBean(clazz);
	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

	/**
	 * 替换相关的bean逻辑
	 *
	 * @param bean
	 * @param beanName
	 * @return
	 * @throws BeansException
	 */
	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if (RemoteConstant.DUBBO_MONITOR_SWITCH == 0 && beanName.equalsIgnoreCase("processService")) {
			return new ProcessServiceImpl();
		}
		if (RemoteConstant.DUBBO_MONITOR_SWITCH == 0 && beanName.equalsIgnoreCase("siteService")) {
			return new SiteServiceImpl();
		}
		if (RemoteConstant.DUBBO_MONITOR_SWITCH == 0 && beanName.equalsIgnoreCase("taskPushService")) {
			return new TaskPushServiceImpl();
		}
		if (RemoteConstant.DUBBO_MONITOR_SWITCH == 0 && beanName.equalsIgnoreCase("taskService")) {
			return new TaskServiceImpl();
		}
		return bean;
	}
}