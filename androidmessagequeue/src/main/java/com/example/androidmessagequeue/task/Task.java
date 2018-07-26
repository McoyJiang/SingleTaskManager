package com.example.androidmessagequeue.task;

import com.example.androidmessagequeue.meta.MessageState;

public interface Task {
    void create();

    void stop();

    void reset();

    void release();

    MessageState prepare();

    void start();
}
