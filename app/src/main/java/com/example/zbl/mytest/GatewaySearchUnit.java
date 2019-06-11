package com.example.zbl.mytest;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.zbl.mytest.bean.GatewayBean;

import org.json.JSONObject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zbl on 2017/3/10.
 * 网关搜索模块
 */

public class GatewaySearchUnit {
    private static final String TAG = "GatewaySearchUnit";
    //"wuliancc"
    private byte[] keyArray = new byte[]{(byte) 119, (byte) 117, (byte) 108, (byte) 105, (byte) 97, (byte) 110, (byte) 99, (byte) 99};
    private String keyString = new String(keyArray);

    private MulticastSocket multicastSocket;

    public interface SearchGatewayCallback {
        void result(List<GatewayBean> list);
    }

    public GatewaySearchUnit() {

    }

    public void startSearch(@NonNull final SearchGatewayCallback callback) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<GatewayBean> list = new ArrayList<>();
                try {
                    multicastSocket = new MulticastSocket(7302);
                    multicastSocket.setSoTimeout(500);
                    InetAddress group = InetAddress.getByName("224.0.0.1");
                    multicastSocket.joinGroup(group);
                    byte[] buf = new byte[1024];
                    DatagramPacket sendDP = new DatagramPacket(keyArray, keyArray.length, group, 7301);
                    multicastSocket.send(sendDP);
                    InetAddress group2 = InetAddress.getByName("239.255.255.250");
                    multicastSocket.joinGroup(group2);
                    DatagramPacket sendDP2 = new DatagramPacket(keyArray, keyArray.length, group2, 1900);
                    multicastSocket.send(sendDP2);

                    while (true) {
                        DatagramPacket datagramPacket = new DatagramPacket(buf, buf.length);
                        multicastSocket.receive(datagramPacket); // 接收数据，同样会进入阻塞状态

                        byte[] message = new byte[datagramPacket.getLength()]; // 从buffer中截取收到的数据
                        System.arraycopy(buf, 0, message, 0, datagramPacket.getLength());
                        String msg = new String(message).trim();
                        Log.w(TAG, datagramPacket.getAddress().toString());
                        Log.w(TAG, msg);
                        try {
                            JSONObject msgJson = new JSONObject(msg);
                            GatewayBean bean = new GatewayBean();
                            bean.host = datagramPacket.getAddress().toString().replace("/", "");
                            bean.key = msgJson.optString("key");
                            bean.gwID = msgJson.optString("gwID");
                            if (TextUtils.equals(keyString, bean.key)) {
                                list.add(bean);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    callback.result(list);
                }
            }
        }).start();
    }
}
