package com.spider.fetcher.impl;

import com.spider.fetcher.AbstractFetcher;
import com.zhoukai.status.ProcessStatusEnum;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * 视频抓取器
 */
@Component
public class VideoFetcher extends AbstractFetcher {


    @Override
    protected void executeRunablePackage(String processId, File executeFile) throws Exception {

    }

    @Override
    protected String beforeExecute(String processId,Long beginTime, File taskFile) {
        return null;
    }

    @Override
    protected String afterExecute(String processId,Long beginTime, Long endTime, String message, ProcessStatusEnum processStatusEnum) {
        return null;
    }
}
