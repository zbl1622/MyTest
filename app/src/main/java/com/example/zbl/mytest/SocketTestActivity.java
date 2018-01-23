package com.example.zbl.mytest;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.zbl.mytest.socket.SocketClient;

/**
 * Created by zbl on 2017/2/27.
 */

public class SocketTestActivity extends AppCompatActivity implements View.OnClickListener {

    private Context context;
    private Button btn_send;
    private ImageView iv_anim;
    private TextView tv_content;
    private SocketClient client;
    private ScrollView scrollView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_sockettest);
        btn_send = (Button) findViewById(R.id.btn_send);
        btn_send.setOnClickListener(this);
        tv_content = (TextView) findViewById(R.id.tv_content);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        iv_anim = (ImageView) findViewById(R.id.iv_anim);
        client = new SocketClient(new SocketClient.SocketClientListener() {
            @Override
            public void onConnectSuccess() {

            }

            @Override
            public void onReceive(final String text) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        log(text);
                    }
                });
            }
        });
        client.connect("192.168.253.1", 8088);

//        LoadingDrawable loadingDrawable = new LoadingDrawable();
//        iv_anim.setImageDrawable(loadingDrawable);
//        PendulumDrawable pendulumDrawable = new PendulumDrawable();
//        iv_anim.setImageDrawable(pendulumDrawable);
    }

    @Override
    protected void onDestroy() {
        if (client != null) {
            client.disconnect();
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        if (v == btn_send) {
            client.send("hello world!");
        }
    }

    private void log(String text) {
        tv_content.append(text);
        tv_content.append("\n");
        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
    }
}
