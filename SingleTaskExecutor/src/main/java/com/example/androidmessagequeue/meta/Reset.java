package com.example.androidmessagequeue.meta;

import com.example.androidmessagequeue.MessageProcessCallback;
import com.example.androidmessagequeue.task.Task;

public class Reset extends CommonMessage {
    public Reset(Task mCurrentTask, MessageProcessCallback messageProcessCallback) {
        super(mCurrentTask, messageProcessCallback);
    }

    @Override
    protected MessageState messageStateBefore() {
        return MessageState.RESETTING;
    }

    @Override
    protected MessageState messageStateAfter() {
        return MessageState.RESET;
    }

    @Override
    protected void performRunMessage() {
        if (mCurrentTask != null) {
            mCurrentTask.reset();
        }
    }
}
