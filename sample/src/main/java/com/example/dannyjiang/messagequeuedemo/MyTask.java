package com.example.dannyjiang.messagequeuedemo;

import android.util.Log;

import com.example.androidmessagequeue.meta.MessageState;
import com.example.androidmessagequeue.task.Task;

public class MyTask implements Task {
    private static final String TAG = MyTask.class.getSimpleName();

    private final String taskName;

    public MyTask(String taskName) {
        this.taskName = taskName;
    }

    @Override
    public String toString() {
        return MyTask.class.getSimpleName() + ":" + taskName;
    }

    @Override
    public void create() {
        Log.e(TAG, "create : " + this);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        Log.e(TAG, "stop : " + this);
    }

    @Override
    public void reset() {
        Log.e(TAG, "reset : " + this);
    }

    @Override
    public void release() {
        Log.e(TAG, "release : " + this);
    }

    @Override
    public MessageState prepare() {
        Log.e(TAG, "prepare : " + this);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return MessageState.PREPARED;
    }

    @Override
    public void start() {
        Log.e(TAG, "start : " + this);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
