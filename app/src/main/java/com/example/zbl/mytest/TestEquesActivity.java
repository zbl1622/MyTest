package com.example.zbl.mytest;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.zbl.mytest.utils.FileUtil;
import com.example.zbl.mytest.view.TestLayout;

/**
 * Created by zbl on 2017/6/5.
 */

public class TestEquesActivity extends AppCompatActivity implements View.OnClickListener {

    private FrameLayout layout_content;
    private Button btn_action1, btn_action2, btn_action3;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testeques);
        btn_action1 = (Button) findViewById(R.id.btn_action1);
        btn_action2 = (Button) findViewById(R.id.btn_action2);
        btn_action3 = (Button) findViewById(R.id.btn_action3);
        btn_action1.setOnClickListener(this);
        btn_action2.setOnClickListener(this);
        btn_action3.setOnClickListener(this);

        layout_content = (TestLayout) findViewById(R.id.layout_content);
        TextView textView = (TextView) layout_content.getChildAt(0);
        layout_content.setVisibility(View.VISIBLE);

        Log.i("TestEquesActivity", "childVisible:" + (textView.getVisibility() == View.VISIBLE));
    }

    @Override
    public void onClick(View v) {
        if (v == btn_action1) {
            Intent intent = new Intent("com.eques.action.PING.a9048a3c38de2d7a");
            sendBroadcast(intent);
        } else if (v == btn_action2) {
//            btn_action2.setBackgroundColor(Color.BLUE);
//            FileUtil.copyAssetsToExternalStorage(getApplicationContext(), new FileUtil.CopyTaskCallback() {
//                @Override
//                public void onFinish() {
//                    btn_action2.setBackgroundColor(Color.GREEN);
//                }
//            });

        } else if (v == btn_action3) {

        }
    }
}
