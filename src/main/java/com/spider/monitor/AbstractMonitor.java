package com.spider.monitor;

import com.spider.config.ServerConstant;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * zk父类监控
 */
public abstract class AbstractMonitor {
    private final static Logger LOG = LoggerFactory.getLogger(AbstractMonitor.class);

    protected final static String ZOOKEEPERSERVERHOST = ServerConstant.ZOOKEEPER_SERVER;
    protected final static String ENCODING = "UTF-8";

    protected ZooKeeper zookeeper;
    protected Stat stat = new Stat();


    protected void connectZookeeper(String zookeeperServerHost) throws IOException, KeeperException, InterruptedException {
        zookeeper = new ZooKeeper(zookeeperServerHost, 10000, new Watcher() {
            public void process(WatchedEvent event) {
                // TODO
            }
        });
    }

    /**
     * 启动站点监控
     */
    public void start() {
        try {
            if (zookeeper == null) {
                this.connectZookeeper(ZOOKEEPERSERVERHOST);
            }
            connectNode();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            restart();
        }
    }

    /**
     * 关闭于zookeeper服务器的连接
     */
    public void close() throws InterruptedException {
        if (null != zookeeper) {
            zookeeper.close();
        }
    }

    /**
     * 重启zk客户端
     */
    protected void restart() {
        try {
            close();
            connectZookeeper(ZOOKEEPERSERVERHOST);
            connectNode();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

    /**
     * 连接初始化的一些操作
     */
    protected abstract void connectNode() throws IOException, KeeperException, InterruptedException;
}
