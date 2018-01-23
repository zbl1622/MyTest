package com.example.zbl.mytest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.zbl.mytest.view.AutoScrollTextView;

import java.util.ArrayList;
import java.util.Arrays;

public class AutoScrollViewActivity extends AppCompatActivity {

    private static final String[] text = {
            "关闭开关", "打开开关", "打开开关名称", "关闭开关名称",
            "关闭插座", "打开插座", "打开插座名称", "关闭插座名称"
    };

    private AutoScrollTextView tv_auto_scroll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_scroll_view);
        tv_auto_scroll = (AutoScrollTextView) findViewById(R.id.tv_auto_scroll);
        ArrayList<String> list = new ArrayList<>(Arrays.asList(text));
        tv_auto_scroll.setText(list);
        tv_auto_scroll.start();
    }

}
