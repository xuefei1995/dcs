package com.xuefei.zk;

import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;

public class ZkUtils {

    private static Logger logger = LoggerFactory.getLogger(ZkUtils.class);

    private static final String ADDRESS = "127.0.0.1:2181";

    private static final String BASE_PATH = "/base";

    private static final int TIMEOUT = 3000;

    public static ZooKeeper getZkConnect() {

        ZooKeeper zk = null;
        try {
            CountDownLatch countDownLatch = new CountDownLatch(1);
            zk = new ZooKeeper(ADDRESS + BASE_PATH, TIMEOUT, new DefaultWatch(countDownLatch));
            countDownLatch.await();
        } catch (Exception e) {
            logger.error("获取zk连接失败....", e);
        }

        return zk;
    }

    public static void zkClose(ZooKeeper zooKeeper) {
        if (zooKeeper != null) {
            try {
                zooKeeper.close();
            } catch (InterruptedException e) {
                logger.info("关闭zk连接失败....", e);
            }
        }
    }



}
