package com.example.androidmessagequeue.meta;

import com.example.androidmessagequeue.MessageProcessCallback;
import com.example.androidmessagequeue.task.Task;
import com.example.androidmessagequeue.utils.Logger;

import static com.example.androidmessagequeue.utils.Config.SHOW_LOGS;

public class Start extends CommonMessage {

    private static final String TAG = Start.class.getSimpleName();

    private MessageState mStartResultState;

    public Start(Task task, MessageProcessCallback messageProcessCallback) {
        super(task, messageProcessCallback);
    }

    @Override
    protected MessageState messageStateBefore() {
        MessageState result = null;
        MessageState currentState = null;
        if (messageProcessCallback != null) {
            currentState = messageProcessCallback.getMessageState();
        }
        if(SHOW_LOGS) Logger.d(TAG, "stateBefore, currentState " + currentState);
        switch (currentState) {

            case PREPARED:
                result = MessageState.STARTING;
                break;

            case NEW_TASK:
            case IDLE:
            case INITIALIZED:
            case PREPARING:
            case RELEASING:
            case RELEASED:
            case RESETTING:
            case RESET:
            case TASK_INSTANCE_CREATING:
            case TASK_INSTANCE_CREATED:
            case TASK_COMPLETED:
            case END:
            case STARTING:
                throw new RuntimeException("unhandled current state " + currentState);

            case ERROR:
                result = MessageState.ERROR;
                break;
            case STARTED:
            case PAUSING:
            case PAUSED:
            case STOPPING:
            case STOPPED:
                // TODO: probably need to handle this
                throw new RuntimeException("unhandled current state " + currentState);

        }

        return result;
    }

    @Override
    protected MessageState messageStateAfter() {
        return mStartResultState;
    }

    @Override
    protected void performRunMessage() {
        MessageState currentState = null;
        if (messageProcessCallback != null) {
            currentState = messageProcessCallback.getMessageState();
        }
        if(SHOW_LOGS) Logger.d(TAG, "currentState " + currentState);

        switch (currentState){
            case NEW_TASK:
            case IDLE:
            case INITIALIZED:
            case PREPARING:
            case PREPARED:
            case RELEASING:
            case RELEASED:
            case RESETTING:
            case RESET:
            case TASK_INSTANCE_CREATING:
            case TASK_INSTANCE_CREATED:
            case TASK_COMPLETED:
            case END:

                throw new RuntimeException("unhandled current state " + currentState);

            case STARTING:
                if (mCurrentTask != null) {
                    mCurrentTask.start();
                    mStartResultState = MessageState.STARTED;
                } else {
                    mStartResultState = MessageState.ERROR;
                }
                break;

            case ERROR:
                mStartResultState = MessageState.ERROR;
                break;
            case STARTED:
            case PAUSING:
            case PAUSED:
            case STOPPING:
            case STOPPED:
                // TODO: probably need to handle this
                throw new RuntimeException("unhandled current state " + currentState);
        }
    }
}
