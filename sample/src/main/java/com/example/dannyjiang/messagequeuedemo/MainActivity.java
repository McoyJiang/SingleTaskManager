package com.example.dannyjiang.messagequeuedemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.androidmessagequeue.SingleMessageManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void addTask(View view) {
        switch (view.getId()) {
            case R.id.btn1:
                SingleMessageManager.getInstance().startTask(new MyTask("下载 : " + 1));
                break;
            case R.id.btn2:
                SingleMessageManager.getInstance().startTask(new MyTask("下载 : " + 2));
                break;
            case R.id.btn3:
                SingleMessageManager.getInstance().startTask(new MyTask("下载 : " + 3));
                break;
        }
    }
}
