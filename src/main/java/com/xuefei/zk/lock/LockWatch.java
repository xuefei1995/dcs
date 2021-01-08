package com.xuefei.zk.lock;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.util.List;
import java.util.concurrent.CountDownLatch;

public class LockWatch implements Watcher, AsyncCallback.StringCallback, AsyncCallback.ChildrenCallback, AsyncCallback.StatCallback {

    private ZooKeeper zkConnect;

    private CountDownLatch countDownLatch;

    private String pathName;

    public LockWatch(ZooKeeper zkConnect, CountDownLatch countDownLatch) {
        this.zkConnect = zkConnect;
        this.countDownLatch = countDownLatch;
    }

    //StringCallback
    @Override
    public void processResult(int i, String path, Object o, String name) {
        if (name != null) {
            //不需要对父节点监控
            pathName = name;
            zkConnect.getChildren("/", false, this, ZkLock.CONTENT);
        }
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        Event.EventType type = watchedEvent.getType();
        switch (type) {
            case None:
            case NodeCreated:
            case NodeDataChanged:
            case NodeChildrenChanged:
                break;
            case NodeDeleted:
                zkConnect.getChildren("/", false, this, ZkLock.CONTENT);
                break;
        }
    }

    //ChildrenCallback
    @Override
    public void processResult(int i, String path, Object o, List<String> list) {
        //进入该方法，一定能看到自己创建的节点以及自己之前的节点
        list.sort(String::compareTo);
        int index = list.indexOf(pathName.substring(1));
        //如果当前节点为最小获得锁
        if (index == 0) {
            countDownLatch.countDown();
        } else {
            //否则监听前一个节点
            zkConnect.exists("/" + list.get(index - 1), this,this, ZkLock.CONTENT);
        }

    }

    @Override
    public void processResult(int i, String s, Object o, Stat stat) {
        //在监听前一节点的时候还没监控成功，前一个节点就不存在了
        if (stat == null) {
            zkConnect.getChildren("/", false, this, ZkLock.CONTENT);
        }
    }
}
