package com.zhoukai.service.monitor;

import com.zhoukai.entity.Site;

public interface SiteService {
    int deleteByPrimaryKey(Long id);

    int insert(Site record);

    int insertSelective(Site record);

    Site selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Site record);

    int updateByPrimaryKey(Site record);
}
