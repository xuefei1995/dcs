package com.xuefei.juc;

import org.junit.jupiter.api.Test;

import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

public class TestThreadLocal {

    /**
     * ThreadLocal
     * 重要属性
     * 静态内部类ThreadLocalMap，该静态内部类中有类Entry继承了WeakReference<ThreadLocal<T>>弱引用
     * 可以认为key为Entry，即WeakReference<ThreadLocal<?>>，value为Entry中的value即T对象
     * private Entry[] table; 存放Entry节点
     * 相当于 table[i] ---强引用---> (Entry)WeakReference<ThreadLocal<T>> ---弱引用---> ThreadLocal<T>对象
     *                        |——> value ---强引用--->  T对象
     * 如果Entry不为弱引用，当tl = null时候，仍然有table[i]指向ThreadLocal<T>对象，导致内存泄露
     * 当Entry为弱引用，当tl = null时候，发生GC后ThreadLocal<T>对象就会被回收
     * 虽然ThreadLocal<T>被回收，即Entry被回收，但是table[i]又指向(Entry)WeakReference<ThreadLocal<T>>，
     * Entry中value又指向了T对象，导致Entry对象和T对象都没法回收，出现内存泄露
     * 因此每次使用完ThreadLocal后，一定要调用remove方法，把value指向T对象的强引用取消掉
     *
     * */

    /**
     * 在spring声明式事务中，对于Connection连接就是存放在ThreadLocal中
     * 因为在方法层层调用过程中，必须保证使用的是同一个Connection
     * */
    static class A {

    }


    ThreadLocal<String> tl = new ThreadLocal<>();

    @Test
    void test() {
        new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("thread1.." + tl.get());
        }).start();
        new Thread(() -> {
            tl.set("hhh");
            System.out.println("thread2.." + tl.get());
        }).start();

        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        tl.remove();
    }

    @Test
    void test1() {
        // weak ---强引用---> WeakReference对象 ---弱引用---> A对象
        // 下次GC发生时候A对象就会被回收
        WeakReference<A> weak = new WeakReference<>(new A()); //弱引用只要发生GC就回收
        System.out.println(weak.get());
        System.gc();
        System.out.println(weak.get());
    }
}
