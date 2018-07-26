package com.example.androidmessagequeue;

import com.example.androidmessagequeue.meta.MessageState;
import com.example.androidmessagequeue.task.Task;

public interface MessageProcessCallback {

    void setCurrentTask(Task task);

    void setMessageState(MessageState messageState);

    MessageState getMessageState();
}
