package com.spider.mock;

import com.zhoukai.service.monitor.TaskPushService;
import com.zhoukai.entity.TaskPush;
import org.springframework.beans.factory.FactoryBean;

/**
 * mock程序
 */
public class TaskPushServiceImpl implements TaskPushService, FactoryBean {
    @Override
    public int deleteByPrimaryKey(Long id) {
        return 0;
    }

    @Override
    public int insert(TaskPush record) {
        return 0;
    }

    @Override
    public int insertSelective(TaskPush record) {
        return 0;
    }

    @Override
    public TaskPush selectByPrimaryKey(Long id) {
        return null;
    }

    @Override
    public int updateByPrimaryKeySelective(TaskPush record) {
        return 0;
    }

    @Override
    public int updateByPrimaryKey(TaskPush record) {
        return 0;
    }

    @Override
    public Object getObject() throws Exception {
        return this;
    }

    @Override
    public Class<?> getObjectType() {
        return TaskPushService.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
