package com.xuefei.juc;

import org.junit.jupiter.api.Test;

import java.util.concurrent.locks.ReentrantLock;

/**
 * ReentrantLock类内部包含一个抽象类Sync，Sync继承了AbstractQueuedSynchronizer(AQS)抽象类
 * Sync有两个实现类NonfairSync和FairSync
 * AbstractQueuedSynchronizer类重要属性
 * volatile int state 0-代表没人获取锁，数字代表加锁的次数
 * transient Thread exclusiveOwnerThread 当前持有锁的线程
 * */
public class TestAQS {

    /**
     * 非公平锁线程park前有最多四次抢锁机会
     * 1.上来先CAS 2.判断state=0时候CAS 3.入队后，判断prev=head，CAS一次 4.设置prev的waitStatus=-1后，再CAS一次
     * 非公平锁 --加锁
     * 1.调用lock方法，对于非公平锁也就是调用了NonfairSync的lock方法
     * 2.先尝试CAS加锁，将state从0设置为1
     * 3.成功即抢到锁，设置exclusiveOwnerThread为当前线程，未成功则代表当前锁有人持有
     * 4.判断state是否为0，如果为0代表刚刚持有锁的人释放了锁，尝试CAS加锁，不为0，代表持有锁的人还未释放锁
     * 5.判断exclusiveOwnerThread是否为当前线程，防止持有锁的人就是自己，是则获取锁，并将state+1
     * 6.持有锁的人不是自己，则没抢到锁，(创建队列)进入队列排队（双向链表），进入队列后再判断当前线程所处队列是否在第二个位置
     *   是则线程再次尝试加锁(可能在线程入队列后，锁释放)，失败或者不在队列第二个位置则线程park
     * */
    /**
     * 公平锁 --加锁
     * 1.调用lock方法，对于非公平锁也就是调用了FairSync的lock方法
     * 2.判断state是否为0，如果为0代表当前锁没人持有，继续判断队列是否为空
     * 3.队列为空，代表当前没人排队，尝试CAS加锁，将state从0设置为1
     * 4.成功即抢到锁，设置exclusiveOwnerThread为当前线程，未成功则代表有其他人抢到了锁
     * 5.队列不为空，或者加锁失败，(创建队列)进入队列排队，进入队列后行为同非公平锁6
     * 6.state不为0，代表当前锁被人持有，判断exclusiveOwnerThread是否为当前线程，防止持有锁的人就是自己，
     *   是则获取锁，并将state+1，否则线程park，进入队列排队，进入队列后行为同非公平锁6
     * */

    /**
     * AQS维护的队列
     * Node节点
     * 重要属性
     * volatile Thread thread; 记录当前节点线程
     * volatile Node prev; 当前节点前一个节点
     * volatile Node next; 当前节点下一个节点
     * volatile int waitStatus; 记录当前节点的状态
     * 1 代表线程已取消
     * -1 代表当前线程后续需要释放锁
     *
     * AbstractQueuedSynchronizer类重要属性
     * transient volatile Node head; 队列头节点
     * transient volatile Node tail; 队列尾节点
     *
     * 公平锁tryAcquire方法中判断队列方法，对应公平锁2步
     * hasQueuedPredecessors()
     * 判断队列是否存在(判断条件队列头尾是否相等)，如果队列存在，再判断队列中第二个Node是否为当前线程
     * (判断条件队列第二个Node记录的thread是否为当前线程),只有队列中第二个节点有权利获得锁,第一个节点代表正在持有锁的节点
     * 因为AQS中，一旦队列生成出来就代表目前已经有线程持有了锁
     *
     * 在tryAcquire未获取到锁之后，线程进入队列的方法
     * acquireQueued(addWaiter(Node.EXCLUSIVE), arg)  arg=1 Node.EXCLUSIVE=null
     *
     * addWaiter(Node.EXCLUSIVE)
     * 1.创建当前线程的node节点
     * 2.判断队列是否存在(判断条件队列尾节点是否为null)
     *   队列存在，讲该节点添加到队列的尾部，设置双向链表指向关系(采用CAS方法设置)
     *   队列不存在，创建队列，创建一个空node节点，并且使该空node跟当前线程node形成双向链表，即当前队列存在两个node。
     * 3.返回当前线程的node节点
     *
     * acquireQueued(node, 1)
     * 1.判断node节点是否为队列的第二个节点(判断条件node节点的prev是否为head)，是的话尝试获取锁(可能在node入队时锁释放)
     * shouldParkAfterFailedAcquire(p, node)
     *  循环执行-----
     * 2.不是第二个节点,判断前一个节点的状态waitStatus，如果为-1代表前一个节点等待释放锁，当前线程park
     * 3.如果前一个节点waitStatus>0代表前面有线程取消了，那么一直往前找找到最近的一个waitStatus=-1的node，创建链表关系
     * 4.如果不为-1，但是<0，那么就将前一个节点的waitStatus设置为-1
     * 在循环执行后，上一个节点的waitStatus变成了-1，因为第4步，使当前线程park
     *
     * 当线程被unPark后，就会重复再执行shouldParkAfterFailedAcquire(p, node)流程
     *
     * */

    @Test
    void test() {
        ReentrantLock reentrantLock = new ReentrantLock(true);
        reentrantLock.lock();

        new Thread(() -> {
            reentrantLock.lock();
        }).start();
//        reentrantLock.unlock();
    }
}
