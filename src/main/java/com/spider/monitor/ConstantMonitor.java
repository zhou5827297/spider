package com.spider.monitor;

import com.spider.config.ServerConstant;
import com.spider.dubbo.DubboServiceRouter;
import com.spider.push.article.ArticleDubboPush;
import com.spider.util.ApplicationContextUtils;
import com.spider.util.ThreadUtils;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.TimeUnit;

/**
 * 配置文件监控
 */
@Component
public class ConstantMonitor extends AbstractMonitor implements Watcher, InitializingBean {
    private final static Logger LOG = LoggerFactory.getLogger(ConstantMonitor.class);
    private final static String GROUPNODE = "/spider_config";
    private final static String SPIDERSYNCDUBBOPATH = GROUPNODE + "/spider_sync_dubbo_path";
    private static boolean STARTFLAG = false; // 定时更新心跳任务标志

    @Autowired
    private DubboServiceRouter dubboServiceRouter;

    /**
     * 连接zookeeper服务器，并在集群总结点下创建EPHEMERAL类型的子节点，把服务器名称存入子节点的数据
     */
    @Override
    public void connectNode() throws IOException, KeeperException, InterruptedException {
        Stat stat = zookeeper.exists(GROUPNODE, false);
        if (null == stat) {
            zookeeper.create(GROUPNODE, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }
        uploadHeart();
    }


    private void updateDubboServiceRouter() throws KeeperException, InterruptedException, UnsupportedEncodingException {
        byte[] data = zookeeper.getData(SPIDERSYNCDUBBOPATH, true, stat);
        if (data != null) {
            String dubboPath = new String(data, ENCODING);
            if (dubboPath != null && dubboPath.length() != 0 && !dubboServiceRouter.getDubboUrl().equals(dubboPath)) {
                dubboServiceRouter.refresh(dubboPath);
                ArticleDubboPush.refresh();
                LOG.info("update dubbo address [{}] ... ", dubboPath);
            }
        }
    }

    /**
     * 每隔段时间上传一次心跳
     */
    private void uploadHeart() {
        if (STARTFLAG) {
            return;
        }
        ThreadUtils.executeQuertz(new Thread(new Runnable() {
            public void run() {
                try {
                    updateDubboServiceRouter();
                } catch (Exception e) {
                    restart();
                    LOG.error(e.getMessage(), e);
                }
            }
        }), 1, 5, TimeUnit.SECONDS);
        STARTFLAG = true;
    }


    public static void main(String[] args) throws IOException, KeeperException, InterruptedException {
        ApplicationContextUtils.getContext();
    }

    @Override
    public void process(WatchedEvent event) {
        LOG.info("listen zookeeper event -----eventType:[{}]，path[{}]", event.getType(), event.getPath());
        //列表发生改变
        if (event.getType() == Event.EventType.NodeChildrenChanged && event.getPath().equals(SPIDERSYNCDUBBOPATH)) {

        }
        //数据发生改变
        if (event.getType() == Event.EventType.NodeDataChanged && event.getPath().startsWith(SPIDERSYNCDUBBOPATH)) {
            try {
                updateDubboServiceRouter();
            } catch (Exception e) {
                LOG.info("update dubbo error [{}] ... ", e.getMessage());
            }
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (ServerConstant.ZOOKEEPER_SWITCH == 1) {
            start();
        }
    }
}
