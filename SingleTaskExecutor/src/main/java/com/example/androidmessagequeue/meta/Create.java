package com.example.androidmessagequeue.meta;

import com.example.androidmessagequeue.MessageProcessCallback;
import com.example.androidmessagequeue.task.Task;

public class Create extends CommonMessage {
    public Create(Task task, MessageProcessCallback messageProcessCallback) {
        super(task, messageProcessCallback);
    }

    @Override
    protected MessageState messageStateBefore() {
        return MessageState.TASK_INSTANCE_CREATING;
    }

    @Override
    protected MessageState messageStateAfter() {
        return MessageState.TASK_INSTANCE_CREATED;
    }

    @Override
    protected void performRunMessage() {
        if (mCurrentTask != null) {
            mCurrentTask.create();
        }
    }
}
