package com.xinxin.eshop.cache.zk;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 创建zookeeper集群session
 * 单例模式
 */
public class ZookeeperSession {
    private static final Logger log = LoggerFactory.getLogger(ZookeeperSession.class);

    private static CountDownLatch connectSemaphore = new CountDownLatch(1);

    private ZooKeeper zooKeeper;

    /**
     * 创建zookeeper连接
     */
    private ZookeeperSession() {
        try {
            this.zooKeeper = new ZooKeeper("192.168.56.102:2181,192.168.56.103:2181,192.168.56.104:2181",
                    50000, new ZooKeeperWatcher());

            log.info("current zookeeper session state is :{}，because create session is asynchronous, so use countDownLatch for await()", zooKeeper.getState());
            // 与zookeeper建立session是异步的 所有用countDownLatch进行阻塞 一旦watch监听到建立session成功，会进行countDown()放行
            connectSemaphore.await();
            log.info("zookeeper session  established success");
        } catch (InterruptedException e) {
            log.info("countDownLatch happen exception is [:{}]", e);
        } catch (IOException e) {
            log.info("zookeeper await create session happen exception is [:{}]", e);
        }
    }

    /**
     * 自定义zookeeper创建session监听器watcher
     */
    private class ZooKeeperWatcher implements Watcher {

        @Override
        public void process(WatchedEvent watchedEvent) {
            if (Event.KeeperState.SyncConnected == watchedEvent.getState()) {
                log.info("zookeeper session create success!");
                connectSemaphore.countDown();
            }
        }
    }

    /**
     * 获取分布式锁
     * 通过zookeeper获取分布式锁
     * 创建临时节点成功 线程放行
     * 创建临时节点失败 线程阻塞 继续循环尝试创建节点
     *
     * @param productId
     */
    public void acquireDistributeLock(Long productId) {
        String path = "/product-lock-" + productId;
        log.info("one acquire distribute lock from zookeeper start ......");
        try {
            zooKeeper.create(path, Thread.currentThread().getName().getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        } catch (Exception e) {
            log.info("one acquire distribute lock from zookeeper failure,so for try cycle create lock start ...........");
            AtomicInteger count = new AtomicInteger(0);// try cycle number
            while (true) {
                try {
                    TimeUnit.MILLISECONDS.sleep(1000);
                    zooKeeper.create(path, Thread.currentThread().getName().getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
                } catch (Exception ex) {
                    log.info("try cycle lock from zookeeper happen exception [:{}]", ex.getClass());
                    count.incrementAndGet();
                    continue;
                }
                log.info("success to acquire lock for product [id={}] after {} times try ......", productId, count);
                break;
            }
        }
    }

    /**
     * 释放一个分布式锁
     * 主动删除zookeeper中的临时节点
     *
     * @param productId
     */
    public void releaseDistributeLock(Long productId) {
        log.info("start for release distribute lock from product [id={}]", productId);
        String path = "/product-lock-" + productId;
        try {
            zooKeeper.delete(path, -1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
        log.info("release distribute lock success from zookeeper");
    }

    /**
     * 获取一个快速失败的锁
     * 一次获取失败
     * 将不在重新获取
     *
     * @param path
     * @return
     */
    public boolean acquireFastFailedDistributedLock(String path) {
        log.info("start for acquire FastFailed distribute lock from path [{}]", path);
        try {
            zooKeeper.create(path, "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
            return true;
        } catch (Exception e) {
            log.info("acquire FastFailed distribute lock from path [{}] is failed", path);
        }
        return false;
    }

    /**
     * 根据指定节点释放锁
     *
     * @param path
     */
    public void releaseDistributeLock(String path) {
        log.info("start for release distribute lock from path [{}]", path);
        try {
            zooKeeper.delete(path, -1);
        } catch (Exception e) {
            log.info("release distribute lock failed from zookeeper,exception [{}]",e);
        }
        log.info("release distribute lock success from zookeeper");
    }

    public void acquireDistributeLock(String path) {
        try {
            zooKeeper.create(path, Thread.currentThread().getName().getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        } catch (Exception e) {
            log.info("one acquire distribute lock from zookeeper failure,so for try cycle create lock start ...........");
            AtomicInteger count = new AtomicInteger(0);// try cycle number
            while (true) {
                try {
                    TimeUnit.MILLISECONDS.sleep(1000);
                    zooKeeper.create(path, Thread.currentThread().getName().getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
                } catch (Exception ex) {
                    log.info("try cycle lock from zookeeper happen exception [:{}]", ex.getClass());
                    count.incrementAndGet();
                    continue;
                }
                log.info("success to acquire lock for product [{}] after {} times try ......", path, count);
                break;
            }
        }
    }

    public void createNode(String path) {
        try {
            if (null == zooKeeper.exists(path, false)) {
                zooKeeper.create(path, "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void setNodeData(String path, String data) {
        try {
            zooKeeper.setData(path, data.getBytes(), -1);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public String getNodeData(String path) {
        try {
            if (null == zooKeeper.exists(path, false)) {
                zooKeeper.create(path, "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
            return new String(zooKeeper.getData(path, null, new Stat()), "UTF-8");
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    private static class Singleton {
        private static ZookeeperSession instance;

        static {
            instance = new ZookeeperSession();
        }

        public static ZookeeperSession getInstance() {
            return instance;
        }
    }

    public static ZookeeperSession getInstance() {
        return Singleton.getInstance();
    }

    public static void init() {
        getInstance();
    }
}
