package com.example.zbl.mytest.socket.receiver;

import com.example.zbl.mytest.socket.SocketClient;

/**
 * Created by zbl on 2017/3/14.
 */

public interface ISocketReceiver {
    void onReceive(SocketClient client, String text);
}
