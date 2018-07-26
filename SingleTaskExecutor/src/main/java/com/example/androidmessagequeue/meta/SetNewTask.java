package com.example.androidmessagequeue.meta;

import com.example.androidmessagequeue.MessageProcessCallback;
import com.example.androidmessagequeue.task.Task;

public class SetNewTask extends CommonMessage {
    public SetNewTask(Task task, MessageProcessCallback messageProcessCallback) {
        super(task, messageProcessCallback);
    }

    @Override
    public String toString() {
        return SetNewTask.class.getSimpleName() + ", mCurrentTask " + mCurrentTask;
    }

    @Override
    protected MessageState messageStateBefore() {
        return MessageState.NEW_TASK;
    }

    @Override
    protected MessageState messageStateAfter() {
        return MessageState.IDLE;
    }

    @Override
    protected void performRunMessage() {
        if (messageProcessCallback != null) {
            messageProcessCallback.setCurrentTask(mCurrentTask);
        }
    }
}
