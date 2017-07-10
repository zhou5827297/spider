package com.spider.fetcher.impl;

import com.spider.fetcher.AbstractFetcher;
import com.spider.model.Site;
import com.spider.service.impl.SpiderServiceImpl;
import com.zhoukai.entity.Process;
import com.zhoukai.status.ProcessStatusEnum;
import common.base.util.FileUtil;
import common.base.util.JsonUtil;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Date;

/**
 * 文章抓取器
 */
@Component
public class ArticleFetcher extends AbstractFetcher {

    @Override
    protected void executeRunablePackage(String processId, File executeFile) throws Exception {
        SpiderServiceImpl service = new SpiderServiceImpl();
        Site site = read(executeFile);
        if (site != null) {
            site.setDomain(executeFile.getName().replaceFirst(".json$", ""));
            service.setSite(site);
            // 置为处理中状态
            Process process = processService.selectByPrimaryKey(Long.valueOf(processId));
            if (process != null) {
                process.setStatus(ProcessStatusEnum.DEALING.getStatus());
                processService.updateByPrimaryKeySelective(process);
            } else { //构建一个空对象，不影响抓取功能
                process = new Process();
            }
            service.setProcess(process);
            service.nextUri();
        }
    }

    @Override
    protected String beforeExecute(String processId, Long beginTime, File taskFile) {
        Site site = read(taskFile);
        Process process = new Process();
        process.setId(processId);
        process.setBeginTime(new Date(beginTime));
        process.setStatus(ProcessStatusEnum.WAIT.getStatus());
        if (site != null) {
            process.setSiteId(site.getId().toString());
        } else {
            process.setStatus(ProcessStatusEnum.FAIL.getStatus());
            process.setMessage("[" + taskFile.getPath() + "]json file error ...");
            LOG.error("article site file [{}]  error ...", taskFile.getPath());
        }
        processService.insert(process);
        return processId;
    }

    @Override
    protected String afterExecute(String processId, Long beginTime, Long endTime, String message, ProcessStatusEnum processStatusEnum) {
        Process process = processService.selectByPrimaryKey(Long.valueOf(processId));
        if (process != null) {
            process.setStatus(processStatusEnum.getStatus());
            // 没有结束时间，说明是出异常了
            if (process.getEndTime() == null) {
                endTime = System.currentTimeMillis();
            }
            if (endTime != null) {
                process.setEndTime(new Date(endTime));
                if (beginTime == null) {
                    beginTime = process.getBeginTime().getTime();
                }
                long spendTime = endTime - beginTime;
                process.setExecuteTime(spendTime);
            }
            if (message == null) {
                process.setMessage(message);
            }
            processService.updateByPrimaryKeySelective(process);
            return process.getId();
        }
        return null;
    }

    private Site read(File file) {
        byte[] bytes = FileUtil.read(file);
        Site site = JsonUtil.tryObject(bytes, Site.class);
        return site;
    }

}
