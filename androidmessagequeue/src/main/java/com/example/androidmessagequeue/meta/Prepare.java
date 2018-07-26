package com.example.androidmessagequeue.meta;

import com.example.androidmessagequeue.MessageProcessCallback;
import com.example.androidmessagequeue.task.Task;
import com.example.androidmessagequeue.utils.Logger;

import static com.example.androidmessagequeue.utils.Config.SHOW_LOGS;

public class Prepare extends CommonMessage {
    private static final String TAG = Prepare.class.getSimpleName();

    private MessageState mPrepareResultState;

    public Prepare(Task task, MessageProcessCallback messageProcessCallback) {
        super(task, messageProcessCallback);
    }

    @Override
    protected MessageState messageStateBefore() {
        return MessageState.PREPARING;
    }

    @Override
    protected MessageState messageStateAfter() {
        return mPrepareResultState;
    }

    @Override
    protected void performRunMessage() {
        if (mCurrentTask != null) {
            mPrepareResultState = mCurrentTask.prepare();
            if(SHOW_LOGS) Logger.v(TAG, "resultOfPrepare " + mPrepareResultState);
        }
    }
}
