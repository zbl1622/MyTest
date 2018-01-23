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

import com.example.zbl.mytest.orm.DaoMaster;
import com.example.zbl.mytest.orm.DaoSession;
import com.example.zbl.mytest.orm.User;
import com.example.zbl.mytest.socket.SocketClient;

import org.greenrobot.greendao.database.Database;

/**
 * Created by zbl on 2017/2/27.
 */

public class ORMTestActivity extends AppCompatActivity implements View.OnClickListener {

    private Context context;
    private Button btn_send;
    private ImageView iv_anim;
    private TextView tv_content;
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
        initORM();
    }

    private DaoSession daoSession;

    private void initORM(){
        boolean ENCRYPTED = false;
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, ENCRYPTED ? "notes-db-encrypted" : "notes-db");
        Database db = ENCRYPTED ? helper.getEncryptedWritableDb("super-secret") : helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        if (v == btn_send) {
            daoSession.getUserDao().insert(new User());
        }
    }

    private void log(String text) {
        tv_content.append(text);
        tv_content.append("\n");
        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
    }
}
