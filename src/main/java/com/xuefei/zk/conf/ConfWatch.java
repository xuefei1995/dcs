package com.xuefei.zk.conf;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

public class ConfWatch implements Watcher, AsyncCallback.StatCallback, AsyncCallback.DataCallback {

    private ZooKeeper zkConnect;

    public ConfWatch(ZooKeeper zkConnect) {
        this.zkConnect = zkConnect;
    }

    //StatCallback
    @Override
    public void processResult(int i, String s, Object o, Stat stat) {
        if (stat != null) {
            zkConnect.getData(ZkConfig.PUBPARAM_PATH, this, this, ZkConfig.CONTENT);
        }
    }

    //DataCallback
    @Override
    public void processResult(int i, String s, Object o, byte[] bytes, Stat stat) {
        if (bytes != null) {
            PubParam.getPubParam().setLiveData(new String(bytes));
        }
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        Event.EventType type = watchedEvent.getType();
        switch (type) {
            case NodeCreated:
            case NodeDataChanged:
                //节点数据改变重新读取节点数据
                //节点创建读取数据
                zkConnect.getData(ZkConfig.PUBPARAM_PATH, this, this, ZkConfig.CONTENT);
                break;
            case NodeDeleted:
                PubParam.getPubParam().setLiveData(null);
                //节点被删除等待节点创建
                zkConnect.exists(ZkConfig.PUBPARAM_PATH, this, this, ZkConfig.CONTENT);
                break;
            case None:
            case NodeChildrenChanged:
                break;
        }
    }
}
