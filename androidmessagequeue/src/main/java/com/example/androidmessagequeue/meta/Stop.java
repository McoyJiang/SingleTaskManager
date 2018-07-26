package com.example.androidmessagequeue.meta;

import com.example.androidmessagequeue.MessageProcessCallback;
import com.example.androidmessagequeue.task.Task;

public class Stop extends CommonMessage {
    public Stop(Task mCurrentTask, MessageProcessCallback messageProcessCallback) {
        super(mCurrentTask, messageProcessCallback);
    }

    @Override
    protected MessageState messageStateBefore() {
        return MessageState.STOPPING;
    }

    @Override
    protected MessageState messageStateAfter() {
        return MessageState.STOPPED;
    }

    @Override
    protected void performRunMessage() {
        if (mCurrentTask != null) {
            mCurrentTask.stop();
        }
    }
}
