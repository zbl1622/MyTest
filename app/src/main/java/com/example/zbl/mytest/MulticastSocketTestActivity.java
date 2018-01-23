package com.example.zbl.mytest;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.zbl.mytest.bean.GatewayBean;
import com.example.zbl.mytest.socket.SocketClient;
import com.example.zbl.mytest.socket.receiver.KeepCmdReceiver;
import com.example.zbl.mytest.socket.receiver.SingleCmdReceiver;
import com.example.zbl.mytest.utils.DesUtil;
import com.example.zbl.mytest.utils.MD5Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zbl on 2017/2/27.
 */

public class MulticastSocketTestActivity extends AppCompatActivity implements View.OnClickListener {

    private Context context;
    private Button btn_send, btn_cmd0, btn_cmd1;
    private ListView listView;
    private GatewayListAdapter adapter;
    private TextView tv_content;
    private ScrollView scrollView;
    private GatewaySearchUnit gatewaySearchUnit;
    private DesUtil desUtil;

    private String gwID, appID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_gateway);
        btn_send = (Button) findViewById(R.id.btn_send);
        btn_send.setOnClickListener(this);
        btn_cmd0 = (Button) findViewById(R.id.btn_cmd0);
        btn_cmd0.setOnClickListener(this);
        btn_cmd1 = (Button) findViewById(R.id.btn_cmd1);
        btn_cmd1.setOnClickListener(this);
        tv_content = (TextView) findViewById(R.id.tv_content);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        listView = (ListView) findViewById(R.id.listView);
        adapter = new GatewayListAdapter();
        listView.setAdapter(adapter);

        gatewaySearchUnit = new GatewaySearchUnit();
        desUtil = createDesKey();

    }

    @Override
    protected void onDestroy() {
        if (gatewaySearchUnit != null) {
//            gatewaySearchUnit.disconnect();
        }
        super.onDestroy();
    }

    private void log(String text) {
        tv_content.append(text);
        tv_content.append("\n");
        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
    }

    @Override
    public void onClick(View v) {
        if (v == btn_send) {
            gatewaySearchUnit.startSearch(new GatewaySearchUnit.SearchGatewayCallback() {
                @Override
                public void result(final List<GatewayBean> list) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            log("获取网关列表成功 ");
                            adapter.getData().clear();
                            adapter.getData().addAll(list);
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
            });
        } else if (v == btn_cmd0) {
            testControlDevice("2255");
        } else if (v == btn_cmd1) {
            testControlDevice("2000");
        }
    }

    private void testControlDevice(String epData){
        if (socketClient != null){

            log("开始控制设备");
            final JSONObject json = new JSONObject();
            try {
                json.put("cmd", "12");
                json.put("gwID", gwID);
                json.put("devID", "1FA8BF05004B1200");
                json.put("type", "91");
                json.put("ep", "14");
                json.put("epType", "91");
                json.put("epData", epData);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            socketClient.registReceiver(new SingleCmdReceiver("12", desUtil) {
                @Override
                public void onReceive(JSONObject jsonObject) {

                }
            });
            socketClient.send(json.toString());
        }

    }

    class GatewayListAdapter extends BaseAdapter {

        private List<GatewayBean> list = new ArrayList<>();

        public List<GatewayBean> getData() {
            return list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                TextView textView = new TextView(context);
                textView.setTextSize(16);
                textView.setPadding(10, 10, 10, 10);
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int p = (int) v.getTag();
                        GatewayBean bean = list.get(p);
                        connectGateway(bean);
                    }
                });
                convertView = textView;
            }
            TextView textView = (TextView) convertView;
            textView.setTag(position);
            GatewayBean bean = list.get(position);
            textView.setText(bean.host + ":" + bean.gwID);
            return convertView;
        }
    }

    private SocketClient socketClient;

    private void connectGateway(final GatewayBean bean) {
        log("开始连接网关:" + bean.gwID);
        if (socketClient == null) {
            socketClient = new SocketClient(new SocketClient.SocketClientListener() {
                @Override
                public void onConnectSuccess() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            log("连接网关成功");
                            registerAppInfo(bean);
                        }
                    });
                }

                @Override
                public void onReceive(final String text) {
                    final String msg = desUtil.Decode(text);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            log("receive:" + msg);
                        }
                    });
                }
            });
        }
        socketClient.connect(bean.host, 7002);
    }

    private void registerAppInfo(final GatewayBean bean) {
        log("开始注册终端:" + bean.gwID);
        JSONObject json = new JSONObject();
        try {
            json.put("cmd", "01");
            TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            // 设备的唯一标识
            String deviceId = tm.getDeviceId();
            json.put("imeiId", deviceId);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        socketClient.registReceiver(new SingleCmdReceiver("01", desUtil) {
            @Override
            public void onReceive(JSONObject jsonObject) {
                if ("0".equals(jsonObject.optString("data"))) {
                    loginGateway(bean);
                }
            }
        });
        socketClient.send(json.toString());
    }

    private void loginGateway(final GatewayBean bean) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                log("开始登录网关:" + bean.gwID);
            }
        });
        JSONObject json = new JSONObject();
        try {
            json.put("cmd", "03");
            json.put("appType", "1");
            //数据
            JSONObject gwJsonObj = new JSONObject();
            gwJsonObj.put("gwID", bean.gwID);
            gwJsonObj.put("gwPwd", MD5Util.encrypt("123456"));
            JSONArray gwJsonArray = new JSONArray();
            gwJsonArray.put(gwJsonObj);
            json.put("data", gwJsonArray);

            json.put("appVer", "V5_1.0.0");

            TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            // 设备的唯一标识
            String deviceId = tm.getDeviceId();
            // SIM卡的唯一标识
            String simId = tm.getSubscriberId();
            // SIM卡序列号
            String simSerialNo = tm.getSimSerialNumber();
            // SIM卡运营商的国家代码
            String simCountryIso = tm.getSimCountryIso();
            // SIM卡运营商名称
            String simOperatorName = tm.getSimOperatorName();
            json.put("appID", "android" + deviceId);
            json.put("netType", "wifi");
            json.put("ismiId", simId);
            json.put("simSerialNo", simSerialNo);
            json.put("simCountryIso", simCountryIso);
            json.put("simOperatorName", simOperatorName);
            json.put("phoneType", "4.4.4");
            json.put("phoneOS", "wifi");

            gwID = bean.gwID;
            appID = "android" + deviceId;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        socketClient.registReceiver(new SingleCmdReceiver("03", desUtil) {
            @Override
            public void onReceive(JSONObject jsonObject) {
                if ("0".equals(jsonObject.optString("data"))) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            log("登录网关成功");
                            getDeviceList();
                        }
                    });
                }
            }
        });
        socketClient.send(desUtil.Encode(json.toString()));
    }

    private void getDeviceList() {
        log("开始获取设备列表");
        final JSONObject json = new JSONObject();
        try {
            json.put("cmd", "11");
            json.put("gwID", gwID);
            json.put("appID", appID);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        socketClient.registReceiver(new SingleCmdReceiver("11", desUtil) {
            @Override
            public void onReceive(JSONObject jsonObject) {

            }
        });
        socketClient.registReceiver(new KeepCmdReceiver("16", desUtil) {
            @Override
            public void onReceive(final JSONObject jsonObject) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        log("设备：" + jsonObject.optString("name") + " 上线");
                    }
                });
            }
        });
        socketClient.send(json.toString());
    }

    protected DesUtil createDesKey() {
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        // 设备的唯一标识
        String deviceId = tm.getDeviceId();
        String md5Key = MD5Util.encrypt(deviceId);
        String desKey = md5Key.substring(0, 8);
        DesUtil desUtil = new DesUtil();
        desUtil.setKey(desKey);
        return desUtil;
    }
}
