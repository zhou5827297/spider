package com.spider.fetcher.impl;

import com.spider.fetcher.AbstractFetcher;
import com.zhoukai.status.ProcessStatusEnum;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * 评论抓取器
 */
@Component
public class CommmentFetcher  extends AbstractFetcher {


    protected void executeRunablePackage(String processId, File executeFile) throws Exception {
        if (executeFile == null) {
            return;
        }
//        CommentServiceImpl commentService = new CommentServiceImpl();
//        commentService.setFile(executeFiles.get(0));
//        commentService.execute();
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
