package com.spider.mock;

import com.zhoukai.service.monitor.ProcessService;
import com.zhoukai.entity.Process;
import org.springframework.beans.factory.FactoryBean;

/**
 * mock程序
 */
public class ProcessServiceImpl implements ProcessService,FactoryBean {
    @Override
    public int deleteByPrimaryKey(Long id) {
        return 0;
    }

    @Override
    public int insert(Process record) {
        return 0;
    }

    @Override
    public int insertSelective(Process record) {
        return 0;
    }

    @Override
    public Process selectByPrimaryKey(Long id) {
        return null;
    }

    @Override
    public int updateByPrimaryKeySelective(Process record) {
        return 0;
    }

    @Override
    public int updateByPrimaryKey(Process record) {
        return 0;
    }

    @Override
    public Object getObject() throws Exception {
        return this;
    }

    @Override
    public Class<?> getObjectType() {
        return ProcessService.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
