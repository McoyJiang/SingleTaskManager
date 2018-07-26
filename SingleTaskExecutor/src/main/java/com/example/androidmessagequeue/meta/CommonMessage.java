package com.example.androidmessagequeue.meta;

import com.example.androidmessagequeue.MessageProcessCallback;
import com.example.androidmessagequeue.task.Task;

public abstract class CommonMessage implements Message {
    private static final String TAG = CommonMessage.class.getSimpleName();

    protected MessageProcessCallback messageProcessCallback;
    protected Task mCurrentTask;

    public CommonMessage(Task task, MessageProcessCallback messageProcessCallback) {
        mCurrentTask = task;
        this.messageProcessCallback = messageProcessCallback;
    }

    @Override
    public void runMessage() {
        performRunMessage();
    }

    @Override
    public void polledFromQueue() {
        if (messageProcessCallback != null) {
            messageProcessCallback.setMessageState(messageStateBefore());
        }
    }

    @Override
    public void messageFinished() {
        if (messageProcessCallback != null) {
            messageProcessCallback.setMessageState(messageStateAfter());
        }
    }

    protected abstract MessageState messageStateBefore();

    protected abstract MessageState messageStateAfter();

    protected abstract void performRunMessage();
}
