package com.example.androidmessagequeue.meta;

import com.example.androidmessagequeue.MessageProcessCallback;
import com.example.androidmessagequeue.task.Task;

public class Release extends CommonMessage {
    public Release(Task mCurrentTask, MessageProcessCallback messageProcessCallback) {
        super(mCurrentTask, messageProcessCallback);
    }

    @Override
    protected MessageState messageStateBefore() {
        return MessageState.RELEASING;
    }

    @Override
    protected MessageState messageStateAfter() {
        return MessageState.RELEASED;
    }

    @Override
    protected void performRunMessage() {
        if (mCurrentTask != null) {
            mCurrentTask.release();
        }
    }
}
