package com.example.zbl.mytest.socket.receiver;

import android.support.annotation.NonNull;

import com.example.zbl.mytest.socket.SocketClient;
import com.example.zbl.mytest.utils.DesUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by zbl on 2017/3/14.
 * 永久监听的接收器
 */

public abstract class KeepCmdReceiver implements ISocketReceiver {

    private DesUtil desUtil;
    private String cmd;

    public KeepCmdReceiver(@NonNull String cmd, @NonNull DesUtil desUtil) {
        this.cmd = cmd;
        this.desUtil = desUtil;
    }

    @Override
    public void onReceive(SocketClient client, String text) {
        try {
            JSONObject jsonObject = new JSONObject(desUtil.Decode(text));
            String cmd = jsonObject.optString("cmd");
            if (cmd != null && cmd.equals(this.cmd)) {
                onReceive(jsonObject);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public abstract void onReceive(JSONObject jsonObject);
}
