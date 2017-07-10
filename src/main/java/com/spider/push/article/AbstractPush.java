package com.spider.push.article;

import com.spider.dubbo.DubboService;
import com.spider.sequence.Sequence;
import com.spider.sequence.impl.SnowflakeSequence;
import com.spider.dubbo.vo.Article;
import com.spider.dubbo.vo.Response;
import com.spider.model.Comment;
import com.spider.push.DataPush;
import com.zhoukai.entity.TaskPush;
import com.zhoukai.service.monitor.TaskPushService;
import com.zhoukai.status.TaskPushStatusEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * 抽象推送功能
 */
public abstract class AbstractPush implements DataPush {
    protected final Logger LOG = LoggerFactory.getLogger(getClass());
    protected final static Sequence SEQUENCE = SnowflakeSequence.getSequence();
    protected final static TaskPushService TASKPUSHSERVICE = DubboService.getTaskPushService();

    /**
     * 推送次数
     */
    private int pushCount;

    /**
     * 推送失败次数
     */
    private int pushErrorCount;

    /**
     * 文章对象
     */
    private Article article;
    /**
     * 评论对象
     */
    private Comment comment;

    /**
     * 任务id
     */
    private String taskId;
    /**
     * 站点id
     */
    private String siteId;

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }

    public Article getArticle() {
        return article;
    }

    public void setArticle(Article article) {
        this.article = article;
    }

    public int getPushCount() {
        return pushCount;
    }

    public void setPushCount(int pushCount) {
        this.pushCount = pushCount;
    }

    public int getPushErrorCount() {
        return pushErrorCount;
    }

    public void setPushErrorCount(int pushErrorCount) {
        this.pushErrorCount = pushErrorCount;
    }

    public synchronized void incrementPushCount() {
        pushCount++;
    }

    public synchronized void incrementPushErrorCount() {
        pushErrorCount++;
    }

    @Override
    public String toString() {
        return "AbstractPush{" +
                "pushCount=" + pushCount +
                ", pushErrorCount=" + pushErrorCount +
                ", article.url=" + article.getUrl() +
                ", comment=" + comment +
                ", taskId=" + taskId +
                '}';
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    /**
     * 推送爬虫数据
     */
    protected abstract Response pushSpiderData();

    @Override
    public boolean push() {
        // 推送给监控中心
        TaskPush taskPush = new TaskPush();
        taskPush.setId(SEQUENCE.getSequenceId());
        taskPush.setTaskId(taskId);
        taskPush.setSiteId(siteId);
        taskPush.setBeginTime(new Date());
        taskPush.setStatus(TaskPushStatusEnum.DEALING.getStatus());
        TASKPUSHSERVICE.insert(taskPush);

        Response response = pushSpiderData();

        boolean success = false;
        TaskPushStatusEnum taskPushStatusEnum;
        if (response.getCode() == Response.Code.SUCCESS) {
            Date endDate = new Date();
            taskPush.setEndTime(endDate);
            long executeTime = endDate.getTime() - taskPush.getBeginTime().getTime();
            taskPush.setExecuteTime(executeTime);
            taskPushStatusEnum = TaskPushStatusEnum.SUCCESS;
            success = true;
        } else {
            taskPushStatusEnum = TaskPushStatusEnum.FAIL;
        }

        taskPush.setStatus(taskPushStatusEnum.getStatus());
        taskPush.setMessage(response.getMessage());
        TASKPUSHSERVICE.updateByPrimaryKeySelective(taskPush);

        return success;
    }


}
