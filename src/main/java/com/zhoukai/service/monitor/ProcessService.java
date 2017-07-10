package com.zhoukai.service.monitor;


import com.zhoukai.entity.Process;

public interface ProcessService {

    int deleteByPrimaryKey(Long id);

    int insert(Process record);

    int insertSelective(Process record);

    Process selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Process record);

    int updateByPrimaryKey(Process record);

}
