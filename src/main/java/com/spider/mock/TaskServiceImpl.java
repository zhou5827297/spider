package com.spider.mock;

import com.zhoukai.service.monitor.TaskService;
import com.zhoukai.entity.Task;
import org.springframework.beans.factory.FactoryBean;

/**
 * mock程序
 */
public class TaskServiceImpl implements TaskService, FactoryBean {
    @Override
    public int deleteByPrimaryKey(Long id) {
        return 0;
    }

    @Override
    public int insert(Task record) {
        return 0;
    }

    @Override
    public int insertSelective(Task record) {
        return 0;
    }

    @Override
    public Task selectByPrimaryKey(Long id) {
        return null;
    }

    @Override
    public int updateByPrimaryKeySelective(Task record) {
        return 0;
    }

    @Override
    public int updateByPrimaryKey(Task record) {
        return 0;
    }

    @Override
    public Object getObject() throws Exception {
        return this;
    }

    @Override
    public Class<?> getObjectType() {
        return TaskService.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
