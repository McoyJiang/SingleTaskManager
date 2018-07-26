package com.example.androidmessagequeue;

import com.example.androidmessagequeue.meta.Create;
import com.example.androidmessagequeue.meta.MessageState;
import com.example.androidmessagequeue.meta.Prepare;
import com.example.androidmessagequeue.meta.Release;
import com.example.androidmessagequeue.meta.Reset;
import com.example.androidmessagequeue.meta.SetNewTask;
import com.example.androidmessagequeue.meta.Start;
import com.example.androidmessagequeue.meta.Stop;
import com.example.androidmessagequeue.task.Task;
import com.example.androidmessagequeue.utils.Logger;

import java.util.Arrays;

import static com.example.androidmessagequeue.utils.Config.SHOW_LOGS;

public class SingleMessageManager implements MessageProcessCallback{

    private static final String TAG = SingleMessageManager.class.getSimpleName();

    // 因为SingleMessageManager在系统中是单例的存在，所以MessageQueueHandler
    // 的实例也只有一个, 因此可以实现一个Locker控制线程的过程
    private MessageQueueHandler messageQueueHandler = new MessageQueueHandler();

    // 保存当前Task的实例对象，当启动新的Task时，需要替换此对象实例
    private Task mCurrentTask;
    // 保存当前Task已经进行到哪一种状态, 默认是IDLE状态
    private MessageState mCurrentState = MessageState.IDLE;

    private static SingleMessageManager instance;

    public static SingleMessageManager getInstance() {
        if (instance == null) {
            instance = new SingleMessageManager();
        }
        return instance;
    }

    public void startTask(Task task) {
        if(SHOW_LOGS) Logger.v(TAG, ">> startTask, task " + task + ", mCurrentTask " + mCurrentTask);

        /*
         * 1、先将MessageQueueHandler中访问子线程的操作暂停，避免异步造成问题
         */
        messageQueueHandler.pauseQueueProcessing(TAG);

        boolean currentTaskIsActive = mCurrentTask == task;

        if (SHOW_LOGS) Logger.v(TAG, "startTask, currentTaskIsActive " + currentTaskIsActive);

        if(currentTaskIsActive){
            if(isInPlaybackState()){
                /*
                 * 2、如果task等于当前Task，并且当前Task正在Starting或者Started,则不需要做任何操作
                 */
                if(SHOW_LOGS) Logger.v(TAG, "startTask, task " + task + " is already in state " + mCurrentState);
            } else {
                /*
                 * 3、如果task等于当前Task，并且当前Task不是Starting或者Started,同样需要重新启动一个新的Task
                 */
                startNewTask(task);
            }
        } else {
            /*
             * 4、如果task不等于当前Task，则重新启动一个Task
             */
            startNewTask(task);
        }

        /*
         * 5、启动完新的Task之后，需要重新将MessageQueueHandler中的子线程resume
         */
        messageQueueHandler.resumeQueueProcessing(TAG);

        if(SHOW_LOGS) Logger.v(TAG, "<< startTask, task " + task);
    }

    private void startNewTask(Task task) {
        // 1、先清空MessageQueueHandler中所有的Message
        messageQueueHandler.clearAllPendingMessages(TAG);
        // 2、停止并清空当前Current Task: 包含STOP、RESET、RELEASE
        stopResetReleaseCurrentTask();
        // 3、将task设置为新的Current Task
        setNewTask(task);
        // 4、初始化Task，并执行最新Task的start方法: 包含CREATE、PREPARE、START
        runNewTask(task);
    }

    private void setNewTask(Task task) {
        if(SHOW_LOGS) Logger.v(TAG, "setNewTask, task " + task);
        messageQueueHandler.addMessage(new SetNewTask(task, this));
    }

    private void runNewTask(Task task) {
        if(SHOW_LOGS) Logger.v(TAG, "startPlayback");

        messageQueueHandler.addMessages(Arrays.asList(
                new Create(task, this),
                new Prepare(task, this),
                new Start(task, this)
        ));
    }

    private void stopResetReleaseCurrentTask() {
        if(SHOW_LOGS) Logger.v(TAG, "stopResetReleaseClearCurrentPlayer, mCurrentState "
                + mCurrentState + ", mCurrentTask " + mCurrentTask);

        switch (mCurrentState){
            case IDLE:
                break;
            case INITIALIZED:
            case PREPARING:
            case PREPARED:
            case STARTING:
            case STARTED:
            case PAUSING:
            case PAUSED:
                messageQueueHandler.addMessage(new Stop(mCurrentTask, this));
            case STOPPING:
            case STOPPED:
            case ERROR: // reset if error
            case TASK_COMPLETED:
                messageQueueHandler.addMessage(new Reset(mCurrentTask, this));
                //FALL-THROUGH
            case RESETTING:
            case RESET:
                messageQueueHandler.addMessage(new Release(mCurrentTask, this));
                break;
            case END:
                throw new RuntimeException("unhandled " + mCurrentState);
        }
    }

    private boolean isInPlaybackState() {
        boolean isPlaying = mCurrentState == MessageState.STARTED || mCurrentState == MessageState.STARTING;
        if(SHOW_LOGS) Logger.v(TAG, "isInPlaybackState, " + isPlaying);
        return isPlaying;
    }

    @Override
    public void setCurrentTask(Task task) {

        if(SHOW_LOGS) Logger.v(TAG, ">> setCurrentTask : " + task);

        mCurrentTask = task;
        //mPlayerItemChangeListener.onPlayerItemChanged(currentItemMetaData);

        if(SHOW_LOGS) Logger.v(TAG, "<< setCurrentTask : " + task);
    }

    @Override
    public void setMessageState(MessageState messageState) {
        if(SHOW_LOGS) Logger.v(TAG, ">> setMessageState, messageState "
                + messageState + ", current task " + mCurrentTask);

        mCurrentState = messageState;

        if(SHOW_LOGS) Logger.v(TAG, "<< setMessageState, messageState "
                + messageState + ", current task " + mCurrentTask);

    }

    @Override
    public MessageState getMessageState() {
        return mCurrentState;
    }
}
