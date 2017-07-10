package com.spider.dubbo;

import com.spider.dubbo.service.SpiderSyncService;
import com.spider.mock.SiteServiceImpl;
import com.spider.mock.TaskServiceImpl;
import com.spider.config.RemoteConstant;
import com.spider.mock.ProcessServiceImpl;
import com.spider.mock.TaskPushServiceImpl;
import com.spider.util.SpringContextHolder;
import com.zhoukai.service.monitor.ProcessService;
import com.zhoukai.service.monitor.SiteService;
import com.zhoukai.service.monitor.TaskPushService;
import com.zhoukai.service.monitor.TaskService;

/**
 * dubbo服务接口
 */
public class DubboService {

    public static SpiderSyncService getSpiderSyncService() {
//        return SpringContextHolder.getBean(SpiderSyncService.class);
        return SpringContextHolder.getBean(DubboServiceRouter.class).getSpiderService();
    }

    public static ProcessService getProcessService() {
        if (RemoteConstant.DUBBO_MONITOR_SWITCH == 0) {
            return new ProcessServiceImpl();
        }
        return SpringContextHolder.getBean(ProcessService.class);
    }

    public static SiteService getSiteService() {
        if (RemoteConstant.DUBBO_MONITOR_SWITCH == 0) {
            return new SiteServiceImpl();
        }
        return SpringContextHolder.getBean(SiteService.class);
    }

    public static TaskPushService getTaskPushService() {
        if (RemoteConstant.DUBBO_MONITOR_SWITCH == 0) {
            return new TaskPushServiceImpl();
        }
        return SpringContextHolder.getBean(TaskPushService.class);
    }

    public static TaskService getTaskService() {
        if (RemoteConstant.DUBBO_MONITOR_SWITCH == 0) {
            return new TaskServiceImpl();
        }
        return SpringContextHolder.getBean(TaskService.class);
    }

}
