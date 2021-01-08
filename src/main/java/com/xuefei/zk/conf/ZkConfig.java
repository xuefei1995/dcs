package com.xuefei.zk.conf;

import com.xuefei.zk.ZkUtils;
import org.apache.zookeeper.ZooKeeper;

/**
 * 动态同步zk节点上的数据
 * */
public class ZkConfig {

    private static ZooKeeper zkConnect = ZkUtils.getZkConnect();

    private static ConfWatch confWatch = new ConfWatch(zkConnect);

    public static final String PUBPARAM_PATH = "/pubParam";

    public static final String CONTENT = "content";

    //异步方式刷新配置,可在项目启动时候调用该方法 
    public static void asyncFlushConf() {
        zkConnect.exists(PUBPARAM_PATH, confWatch, confWatch, CONTENT);
    }

}
