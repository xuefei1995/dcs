package com.xuefei.zk;

import com.xuefei.zk.lock.ZkLock;
import org.junit.jupiter.api.Test;


import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

public class ZkLockTest {

    @Test
    void test() {
        CountDownLatch countDownLatch = new CountDownLatch(10);
        CyclicBarrier cyclicBarrier = new CyclicBarrier(11);
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                try {
                    countDownLatch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //加锁
                ZkLock.lock();
                System.out.println("线程" + Thread.currentThread().getName() + "开始执行业务逻辑...");
//                try {
//                    Thread.sleep(5000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
                //释放锁
                ZkLock.unLock();
                try {
                    cyclicBarrier.await();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
            countDownLatch.countDown();
        }

        try {
            cyclicBarrier.await();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("执行完毕");
    }

}
