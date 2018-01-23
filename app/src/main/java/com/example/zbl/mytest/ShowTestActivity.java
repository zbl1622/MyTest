package com.example.zbl.mytest;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

/**
 * Created by zbl on 2017/7/20.
 */

public class ShowTestActivity extends AppCompatActivity {

    private static final String TAG = "ShowTestActivity";

    private Context context;
    private TextView tv_test;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_showtest);
        tv_test = (TextView) findViewById(R.id.tv_test);
        testShow();
    }

    private void testShow() {
        String f1 = String.format("%-2.1f",0f);
        String f2 = String.format("%-2.1f",12f);
        println(f1);
        println(f2);
        println(String.format("%-2.1f",103.08123f));
        println(String.format("%-2.1f",0.08123f));
        println(String.format("%-2.1f",0.01123f));
    }

    private void println(String text){
        tv_test.append(text);
        tv_test.append("\n");
    }

    @Override
    protected void onStart() {
        Log.i(TAG,"onStart");
        super.onStart();
    }

    @Override
    protected void onPause() {
        Log.i(TAG,"onPause");
        super.onPause();
    }

    @Override
    protected void onResume() {
        Log.i(TAG,"onResume");
        super.onResume();
    }

    @Override
    protected void onStop() {
        Log.i(TAG,"onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG,"onDestroy");
        super.onDestroy();
    }
}
