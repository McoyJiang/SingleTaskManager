package com.example.androidmessagequeue.meta;

public interface Message {
    void runMessage();
    void polledFromQueue();
    void messageFinished();
}
