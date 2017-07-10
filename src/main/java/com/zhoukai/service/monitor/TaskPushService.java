package com.zhoukai.service.monitor;

import com.zhoukai.entity.TaskPush;

public interface TaskPushService {
    int deleteByPrimaryKey(Long id);

    int insert(TaskPush record);

    int insertSelective(TaskPush record);

    TaskPush selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TaskPush record);

    int updateByPrimaryKey(TaskPush record);
}
