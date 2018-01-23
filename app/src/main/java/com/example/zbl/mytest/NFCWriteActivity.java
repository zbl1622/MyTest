package com.example.zbl.mytest;

import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zbl.mytest.bean.TextRecord;
import com.example.zbl.mytest.utils.NFCTool;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class NFCWriteActivity extends AppCompatActivity {

    private ImageView iv_support;
    private TextView tv_content;

    private NfcAdapter nfcAdapter;
    private PendingIntent mPendingIntent;

    private String mPackNmae = "heheha";
    private String payloadText = "{}";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc_write);
        iv_support = (ImageView) findViewById(R.id.iv_support);
        tv_content = (TextView) findViewById(R.id.tv_content);

        if (NFCTool.isSupport(this)) {
            iv_support.setBackgroundColor(0xff00ff00);
        } else {
            iv_support.setBackgroundColor(0xffff0000);
        }
        //初始化PendingIntent
        mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()), 0);
        mPackNmae = getPackageName();
        payloadText = getSceneJson();

        // 获取默认的NFC控制器
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            tv_content.setText("设备不支持NFC！");
            return;
        }
        if (!nfcAdapter.isEnabled()) {
            tv_content.setText("请在系统设置中先启用NFC功能！");
            return;
        }
    }


    //当设置android:launchMode="singleTop"时调用
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        //1.获取Tag对象
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        //写入信息
        writeNFC(tag);
    }

    //NFC写入
    private void writeNFC(Tag tag) {
        //null不执行操作，强调写程序的逻辑性
        if (tag == null) {
            return;
        }

        NdefMessage ndefMessage = null;
        try {
            ndefMessage = new NdefMessage(
                    new NdefRecord[]{
//                            NdefRecord.createMime("text/plain","zbltext".getBytes("UTF-8")),
                            NdefRecord.createExternal("wulian", "scene", payloadText.getBytes()),
//                            NdefRecord.createUri("wlsmarthome://cc.wulian.smarthomev6/nfc"),
//                            new NdefRecord(NdefRecord.TNF_WELL_KNOWN,NdefRecord.RTD_TEXT, new byte[0], "zzz.zbl".getBytes("UTF-8")),
//                            NdefRecord.createApplicationRecord(mPackNmae)
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
        //获得写入大小
        int size = ndefMessage.toByteArray().length;
        //2.判断是否是NDEF标签
        try {
            Ndef ndef = Ndef.get(tag);
            if (ndef != null) {
                //说明是NDEF标签,开始连接
                ndef.connect();
                //判断是否可写
                if (!ndef.isWritable()) {
                    Toast.makeText(this, "当前设备不支持写入", Toast.LENGTH_LONG).show();
                    return;
                }
                //判断大小
                if (ndef.getMaxSize() < size) {
                    Toast.makeText(this, "容量太小了", Toast.LENGTH_LONG).show();
                    return;
                }
                //写入
                try {
                    ndef.writeNdefMessage(ndefMessage);
                    Toast.makeText(this, "写入成功", Toast.LENGTH_LONG).show();
                    finish();
                } catch (FormatException e) {
                    e.printStackTrace();
                }

            }
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    //使当前窗口置顶，权限高于三重过滤
    @Override
    protected void onResume() {
        super.onResume();

        if (nfcAdapter != null) {
            //设置当前activity为栈顶
            nfcAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //恢复栈
        if (nfcAdapter != null) {
            nfcAdapter.disableForegroundDispatch(this);
        }
    }


    public String getSceneJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("sceneId","123456");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }
}
