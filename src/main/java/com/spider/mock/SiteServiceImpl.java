package com.spider.mock;

import com.zhoukai.service.monitor.SiteService;
import com.zhoukai.entity.Site;
import org.springframework.beans.factory.FactoryBean;

/**
 * mock程序
 */
public class SiteServiceImpl implements SiteService, FactoryBean {
    @Override
    public int deleteByPrimaryKey(Long id) {
        return 0;
    }

    @Override
    public int insert(Site record) {
        return 0;
    }

    @Override
    public int insertSelective(Site record) {
        return 0;
    }

    @Override
    public Site selectByPrimaryKey(Long id) {
        return null;
    }

    @Override
    public int updateByPrimaryKeySelective(Site record) {
        return 0;
    }

    @Override
    public int updateByPrimaryKey(Site record) {
        return 0;
    }

    @Override
    public Object getObject() throws Exception {
        return this;
    }

    @Override
    public Class<?> getObjectType() {
        return SiteService.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
