package com.xuefei.juc;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

public class TestLockSupport {

    private static List<Integer> list = new ArrayList<>();

//    LockSupport.park()使当前线程暂停执行
//    LockSupport.unpark(t)使当前线程恢复执行
    @Test
    void test() {
        Thread t = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                System.out.println(i);
                if (i == 5) {
                    LockSupport.park();
                }
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        t.start();
//        LockSupport.unpark(t); 加这里线程T就不会阻塞，unpark可以先于park调用

        try {
            TimeUnit.SECONDS.sleep(8);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("after 8 seconds");

        LockSupport.unpark(t);

    }

    @Test
    void test2()  {
        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                list.add(i);
                System.out.println("add.." + i);
            }
        }).start();

        new Thread(() -> {
            while (true) {
                if (list.size() == 5) {
                    break;
                }
            }
            System.out.println("线程2退出");
        }).start();
    }

    Thread t1 , t2 = null;
    @Test
    void test3() throws InterruptedException {

        t1 = new Thread(() -> {
            for (int i = 65; i < 91; i++) {
                System.out.print((char) i);
                LockSupport.unpark(t2);
                LockSupport.park();
            }
        });
        t2 = new Thread(() -> {
            LockSupport.park();
            for (int i = 1; i < 27; i++) {
                System.out.print(i);
                LockSupport.unpark(t1);
                LockSupport.park();
            }
        });

        t2.start();
        Thread.sleep(10);
        t1.start();
    }
}
