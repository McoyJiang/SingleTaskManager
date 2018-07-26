package com.example.androidmessagequeue;

import com.example.androidmessagequeue.utils.Logger;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import static com.example.androidmessagequeue.utils.Config.SHOW_LOGS;

/**
 * @author Danny.姜
 *
 * 封装一个重入锁ReentrantLock，实现线程之间的同步
 * ReentrantLock的使用有两点需要注意：
 * 1 ReentrantLock.lock可以在多个不同的线程中调用，用来实现线程的
 *      同步(类似Synchronized)，lock方法必须与unlock方法匹配执行
 * 2 ReentrantLock.newCondition()可以返回不同的Condition对象,
 *      不同的Condition只能解锁相应Condition的线程(类似Object.wait)
 */
public class MessageQueueLocker {
    private static final String TAG = MessageQueueLocker.class.getSimpleName();
    private final ReentrantLock mQueueLock = new ReentrantLock();
    private final Condition mProcessQueueCondition = mQueueLock.newCondition();

    public void lock(String owner){
        if(SHOW_LOGS) Logger.v(TAG, ">> lock, owner [" + owner + "]");
        mQueueLock.lock();
        if(SHOW_LOGS) Logger.v(TAG, "<< lock, owner [" + owner + "]");
    }

    public void unlock(String owner){
        if(SHOW_LOGS) Logger.v(TAG, ">> unlock, owner [" + owner + "]");
        mQueueLock.unlock();
        if(SHOW_LOGS) Logger.v(TAG, "<< unlock, owner [" + owner + "]");
    }

    public boolean isLocked(String owner){
        boolean isLocked = mQueueLock.isLocked();
        if(SHOW_LOGS) Logger.v(TAG, "isLocked, owner [" + owner + "]");
        return isLocked;
    }

    public void wait(String owner) throws InterruptedException {
        if(SHOW_LOGS) Logger.v(TAG, ">> wait, owner [" + owner + "]");
        mProcessQueueCondition.await();
        if(SHOW_LOGS) Logger.v(TAG, "<< wait, owner [" + owner + "]");
    }

    public void notify(String owner) {
        if(SHOW_LOGS) Logger.v(TAG, ">> notify, owner [" + owner + "]");
        mProcessQueueCondition.signal();
        if(SHOW_LOGS) Logger.v(TAG, "<< notify, owner [" + owner + "]");
    }
}
