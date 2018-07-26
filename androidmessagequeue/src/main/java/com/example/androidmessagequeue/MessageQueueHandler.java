package com.example.androidmessagequeue;

import com.example.androidmessagequeue.meta.Message;
import com.example.androidmessagequeue.utils.Logger;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.example.androidmessagequeue.utils.Config.SHOW_LOGS;

/**
 * @author Danny.姜
 *
 * MessageQueue的管理类，内部封装一个Message的队列
 * 开启一个子线程同步的从队列中取出Message进行访问操作
 * 如果队列为空则暂停此子线程
 */
public class MessageQueueHandler {

    private static final String TAG = MessageQueueHandler.class.getSimpleName();

    private final Queue<Message> mMessagesQueue = new ConcurrentLinkedQueue<>();
    private final MessageQueueLocker mQueueLock = new MessageQueueLocker();
    private final Executor mQueueProcessingExecutor = Executors.newSingleThreadExecutor();

    private AtomicBoolean mTerminated = new AtomicBoolean(false);
    private Message mLastMessage;

    public MessageQueueHandler() {
        // 使用Executor启动单线程，遍历Queue中的Message
        // 如果Queue为空则调用Condition.await()暂停此子线程
        mQueueProcessingExecutor.execute(new Runnable() {
            @Override
            public void run() {
                if (SHOW_LOGS) Logger.v(TAG, "start worker thread");
                do {
                    // 访问Message队列的操作需要保证同步
                    mQueueLock.lock(TAG);
                    if (SHOW_LOGS) Logger.v(TAG, "mMessagesQueue " + mMessagesQueue);
                    if (mMessagesQueue.isEmpty()) {
                        try {
                            mQueueLock.wait(TAG);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    // 从队列中取出一个Message分别执行polledFromQueue、runMessage、messageFinished操作
                    mLastMessage = mMessagesQueue.poll();

                    mLastMessage.polledFromQueue();
                    if (SHOW_LOGS) Logger.v(TAG, "poll mLastMessage " + mLastMessage);
                    mQueueLock.unlock(TAG);

                    if (SHOW_LOGS) Logger.v(TAG, "run, mLastMessage " + mLastMessage);
                    mLastMessage.runMessage();

                    mQueueLock.lock(TAG);
                    mLastMessage.messageFinished();
                    mQueueLock.unlock(TAG);

                } while (!mTerminated.get());
            }
        });
    }

    /**
     * 想队列中添加一个Message, 并调用Condition.signal()方法唤醒相应的线程
     * @param message
     */
    public void addMessage(Message message){

        if (SHOW_LOGS) Logger.v(TAG, ">> addMessage, lock " + message);
        mQueueLock.lock(TAG);

        mMessagesQueue.add(message);
        mQueueLock.notify(TAG);

        if (SHOW_LOGS) Logger.v(TAG, "<< addMessage, unlock " + message);
        mQueueLock.unlock(TAG);
    }

    public void addMessages(List<? extends Message> messages) {
        if (SHOW_LOGS) Logger.v(TAG, ">> addMessages, lock " + messages);
        mQueueLock.lock(TAG);

        mMessagesQueue.addAll(messages);
        mQueueLock.notify(TAG);

        if (SHOW_LOGS) Logger.v(TAG, "<< addMessages, unlock " + messages);
        mQueueLock.unlock(TAG);
    }

    /**
     * 在其他线程做MessageQueue的操作(比如addMessage)之前，需要先在其他线程中调用
     * mQueueLock.lock方法获取到锁，这样可以避免其他线程同时也在访问MessageQueue
     * @param outer
     */
    public void pauseQueueProcessing(String outer){
        if (SHOW_LOGS) Logger.v(TAG, "pauseQueueProcessing, lock " + mQueueLock);
        mQueueLock.lock(outer);
    }

    /**
     * 在其他线程做MessageQueue的操作(比如addMessage)之后，需要先在其他线程中调用
     * mQueueLock.unlock释放锁
     * @param outer
     */
    public void resumeQueueProcessing(String outer){
        if (SHOW_LOGS) Logger.v(TAG, "resumeQueueProcessing, unlock " + mQueueLock);
        mQueueLock.unlock(outer);
    }

    /**
     * 清空队列中所有的Message
     * @param outer
     */
    public void clearAllPendingMessages(String outer) {
        if (SHOW_LOGS) Logger.v(TAG, ">> clearAllPendingMessages, mPlayerMessagesQueue "
                + mMessagesQueue);

        if(mQueueLock.isLocked(outer)){
            mMessagesQueue.clear();
        } else {
            throw new RuntimeException("cannot perform action, you are not holding a lock");
        }
        if (SHOW_LOGS) Logger.v(TAG, "<< clearAllPendingMessages, mPlayerMessagesQueue "
                + mMessagesQueue);
    }

    /**
     * 终止访问MessageQueue的子线程
     */
    public void terminate(){
        mTerminated.set(true);
    }
}
