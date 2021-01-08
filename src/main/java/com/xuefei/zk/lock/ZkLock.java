package com.xuefei.zk.lock;

import com.xuefei.zk.ZkUtils;
import org.apache.zookeeper.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;

/**
 * zk分布式锁，不可重入
 */
public class ZkLock {

    private static Logger logger = LoggerFactory.getLogger(ZkLock.class);

    public static final String LOCK_PATH = "/lock";

    public static ThreadLocal<ZooKeeper> threadLocal = new ThreadLocal<>();

    public static final String CONTENT = "content";

    //加锁
    public static void lock() {
        threadLocal.set(ZkUtils.getZkConnect());
        tryLock();
        logger.info("线程" + Thread.currentThread().getName() + "获取到锁...");
    }



    private static void tryLock() {
        ZooKeeper zooKeeper = threadLocal.get();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        LockWatch lockWatch = new LockWatch(zooKeeper, countDownLatch);
        zooKeeper.create(LOCK_PATH, Thread.currentThread().getName().getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL, lockWatch, CONTENT);
        logger.info("线程" + Thread.currentThread().getName() + "等待获取锁...");
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            logger.error("获取锁失败...", e);
        }
    }


    //释放锁
    public static void unLock() {
        ZkUtils.zkClose(threadLocal.get());
        logger.info("线程" + Thread.currentThread().getName() + "释放锁...");
        threadLocal.remove();
    }
}
