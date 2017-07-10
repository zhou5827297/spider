package com.spider.monitor;

import com.spider.config.ProxyConstant;
import com.spider.util.FileUtil;
import com.spider.config.RemoteConstant;
import com.spider.config.ServerConstant;
import com.spider.util.RemoteServiceUtil;
import com.spider.util.TarUtil;
import com.spider.util.ThreadUtils;
import org.apache.zookeeper.*;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 站点文件监控
 */
public class SiteMonitor extends AbstractMonitor {
    private final static Logger LOG = LoggerFactory.getLogger(SiteMonitor.class);
    private final static String SITEFILE = "sites.tar.gz";
    private final static int RETRY = 5;
    private final static String GROUPNODE = ServerConstant.ZOOKEEPER_GROUP_NODE;
    private final static String CLIENTHOST = ServerConstant.CLIENT_HOST;
    private static boolean STARTFLAG = false; // 定时更新心跳任务标志
    private static SiteMonitor MONITOR = new SiteMonitor();
    private String serverNodePath = "";

    /**
     * 连接zookeeper服务器，并在集群总结点下创建EPHEMERAL类型的子节点，把服务器名称存入子节点的数据
     */
    @Override
    public void connectNode() throws IOException, KeeperException, InterruptedException {
        Stat stat = zookeeper.exists(GROUPNODE, false);
        if (null == stat) {
            zookeeper.create(GROUPNODE, null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }
        // 注册当前节点
        serverNodePath = zookeeper.create(GROUPNODE + "/" + CLIENTHOST, String.valueOf(0).getBytes(ENCODING), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        LOG.info("create service node ：[{}]", serverNodePath);
        uploadHeart();
    }

    /**
     * 每隔段时间上传一次心跳
     */
    private synchronized void uploadHeart() {
        if (STARTFLAG) {
            return;
        }
        ThreadUtils.executeQuertz(new Thread(new Runnable() {
            public void run() {
                try {
                    byte[] data = zookeeper.getData(serverNodePath, false, stat);
                    if (data.length > 7) { // 代表文件数据
                        dealSiteFileFromZk(data);
                    }
                    int heartVal = new Random().nextInt(100000);
                    String l = String.valueOf(heartVal);
                    LOG.debug("update heart ：[{}]", heartVal);
                    zookeeper.setData(serverNodePath, l.getBytes(ENCODING), -1);
                } catch (Exception e) {
                    restart();
                    LOG.error(e.getMessage(), e);
                }
            }
        }), 1, 5, TimeUnit.SECONDS);

        STARTFLAG = true;
    }

    /**
     * 处理来自zk的配置文件流
     */
    private void dealSiteFileFromZk(byte[] data) {
        for (int i = 1; i <= RETRY; i++) {
            try {
                FileUtil.writeFile(data, ProxyConstant.DATA_ROOT, "/" + SITEFILE);
                FileUtil.delAllFile(ProxyConstant.CONFIG_ROOT);
                TarUtil.unTarGz(ProxyConstant.DATA_ROOT + "/" + SITEFILE, ProxyConstant.CONFIG_ROOT);
                // 拉取同步最新的抓取列表
                RemoteServiceUtil.pullRecentlyUrl2Filter(RemoteConstant.PUSH_METHOD);
                LOG.info("sync site success ...");
                break;
            } catch (Exception ex) {
                LOG.error("execute sitefile fail:[{}/{}] --- [{}]", i, RETRY, ex);
            }
        }
    }


    public static SiteMonitor getInstance() {
        return MONITOR;
    }

    public static void main(String[] args) throws IOException, KeeperException, InterruptedException {
        SiteMonitor.getInstance().start();
        while (true) {
            ThreadUtils.sleepThreadSeconds(10);
        }
    }
}
