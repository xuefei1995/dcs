package com.xuefei.zk;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import java.util.concurrent.CountDownLatch;

public class DefaultWatch implements Watcher {

    private CountDownLatch countDownLatch;

    public DefaultWatch(CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        Event.KeeperState state = watchedEvent.getState();
        switch (state) {
            case Unknown:
            case AuthFailed:
            case NoSyncConnected:
            case ConnectedReadOnly:
            case SaslAuthenticated:
            case Expired:
            case Disconnected:
                break;
            case SyncConnected:
                countDownLatch.countDown(); //确保zk链接创建完成后，才返回调用
                break;
        }
    }
}
