package com.spider.browser;

import com.spider.proxy.impl.ProxyRemoteManage;
import com.spider.proxy.ProxyManage;
import com.zhoukai.entity.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 浏览信息
 */
public class BaseBrowser {
    protected final Logger LOG = LoggerFactory.getLogger(getClass());
    protected ProxyManage proxyManage = new ProxyRemoteManage();

    protected Task task;

    public BaseBrowser() {

    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }
}
