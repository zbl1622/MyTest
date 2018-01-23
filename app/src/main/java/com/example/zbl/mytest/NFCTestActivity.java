package com.example.zbl.mytest;

import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.zbl.mytest.bean.TextRecord;
import com.example.zbl.mytest.utils.NFCTool;
import com.example.zbl.mytest.utils.WLog;

import java.io.IOException;

public class NFCTestActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView iv_support;
    private TextView tv_content;
    private Button btn_write;

    private NfcAdapter nfcAdapter;
    private PendingIntent mPendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfctest);
        mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()), 0);

        iv_support = (ImageView) findViewById(R.id.iv_support);
        tv_content = (TextView) findViewById(R.id.tv_content);
        btn_write = (Button) findViewById(R.id.btn_write);
        btn_write.setOnClickListener(this);

        if (NFCTool.isSupport(this)) {
            iv_support.setBackgroundColor(0xff00ff00);
        } else {
            iv_support.setBackgroundColor(0xffff0000);
        }

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

    @Override
    protected void onPause() {
        super.onPause();
//        //恢复栈
//        if (nfcAdapter != null) {
//            nfcAdapter.disableForegroundDispatch(this);
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        if (nfcAdapter != null) {
//            //设置当前activity为栈顶
//            nfcAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);
//        }
        //得到是否检测到ACTION_TECH_DISCOVERED触发
        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(getIntent().getAction())) {
            //处理该intent
            processIntent(getIntent());
        } else if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            //处理该intent
            processIntent(getIntent());
        }
    }

    /**
     * Parses the NDEF Message from the intent and prints to the TextView
     */
    private void processIntent(Intent intent) {
        if (intent == null) {
            return;
        }
        //取出封装在intent中的TAG
        Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        if (tagFromIntent == null) {
            return;
        }
        for (String tech : tagFromIntent.getTechList()) {
            System.out.println(tech);
        }
        boolean auth = false;
        //读取TAG
//        MifareClassic mfc = MifareClassic.get(tagFromIntent);
//        try {
//            String metaInfo = "";
//            //Enable I/O operations to the tag from this TagTechnology object.
//            mfc.connect();
//            int type = mfc.getType();//获取TAG的类型
//            int sectorCount = mfc.getSectorCount();//获取TAG中包含的扇区数
//            String typeS = "";
//            switch (type) {
//                case MifareClassic.TYPE_CLASSIC:
//                    typeS = "TYPE_CLASSIC";
//                    break;
//                case MifareClassic.TYPE_PLUS:
//                    typeS = "TYPE_PLUS";
//                    break;
//                case MifareClassic.TYPE_PRO:
//                    typeS = "TYPE_PRO";
//                    break;
//                case MifareClassic.TYPE_UNKNOWN:
//                    typeS = "TYPE_UNKNOWN";
//                    break;
//            }
//            metaInfo += "卡片类型：" + typeS + "\n共" + sectorCount + "个扇区\n共"
//                    + mfc.getBlockCount() + "个块\n存储空间: " + mfc.getSize() + "B\n";
//            for (int j = 0; j < sectorCount; j++) {
//                //Authenticate a sector with key A.
//                auth = mfc.authenticateSectorWithKeyA(j,
//                        MifareClassic.KEY_DEFAULT);
//                int bCount;
//                int bIndex;
//                if (auth) {
//                    metaInfo += "Sector " + j + ":验证成功\n";
//                    // 读取扇区中的块
//                    bCount = mfc.getBlockCountInSector(j);
//                    bIndex = mfc.sectorToBlock(j);
//                    for (int i = 0; i < bCount; i++) {
//                        byte[] data = mfc.readBlock(bIndex);
//                        metaInfo += "Block " + bIndex + " : "
//                                + bytesToHexString(data) + "\n";
//                        bIndex++;
//                    }
//                } else {
//                    metaInfo += "Sector " + j + ":验证失败\n";
//                }
//            }
//            tv_content.setText(metaInfo);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        try {
            Ndef ndef = Ndef.get(tagFromIntent);
            ndef.connect();
            //获取标签的类型和最大容量
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(ndef.getType() + "\n最大数据容量：" + ndef.getMaxSize()
                    + " bytes\n\n");
            NdefMessage ndefMessage = ndef.getNdefMessage();
            NdefRecord[] ndefRecords = ndefMessage.getRecords();
            if (ndefRecords != null) {
                for (NdefRecord record : ndefRecords) {
                    stringBuilder.append(new String(record.getType(), "UTF-8"));
                    stringBuilder.append("\n");
                    stringBuilder.append(new String(record.getPayload(), "UTF-8"));
                    stringBuilder.append("\n");
                }
            }
            //读取NFC标签的数据并解析
            stringBuilder.append(readNFCTag());
            //将标签的相关信息显示在界面上
            tv_content.setText(stringBuilder.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }
    }

    private String readNFCTag() {
        String mTagText = "";
        //判断是否为ACTION_NDEF_DISCOVERED
//        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
        //从标签读取数据（Parcelable对象）
        Parcelable[] rawMsgs = getIntent().getParcelableArrayExtra(
                NfcAdapter.EXTRA_NDEF_MESSAGES);

        NdefMessage msgs[] = null;
        int contentSize = 0;
        if (rawMsgs != null) {
            msgs = new NdefMessage[rawMsgs.length];
            //标签可能存储了多个NdefMessage对象，一般情况下只有一个NdefMessage对象
            for (int i = 0; i < rawMsgs.length; i++) {
                //转换成NdefMessage对象
                msgs[i] = (NdefMessage) rawMsgs[i];
                //计算数据的总长度
                contentSize += msgs[i].toByteArray().length;

            }
        }
        try {

            if (msgs != null) {
                //程序中只考虑了1个NdefRecord对象，若是通用软件应该考虑所有的NdefRecord对象
                NdefRecord record = msgs[0].getRecords()[0];
                //分析第1个NdefRecorder，并创建TextRecord对象
                TextRecord textRecord = TextRecord.parse(msgs[0]
                        .getRecords()[0]);
                //获取实际的数据占用的大小，并显示在窗口上
                mTagText += textRecord.getText() + "\n\n纯文本\n"
                        + contentSize + " bytes";

            }

        } catch (Exception e) {
            return e.getMessage();
        }
//        }
        return mTagText;
    }

    //字符序列转换为16进制字符串
    private String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("0x");
        if (src == null || src.length <= 0) {
            return null;
        }
        char[] buffer = new char[2];
        for (int i = 0; i < src.length; i++) {
            buffer[0] = Character.forDigit((src[i] >>> 4) & 0x0F, 16);
            buffer[1] = Character.forDigit(src[i] & 0x0F, 16);
            System.out.println(buffer);
            stringBuilder.append(buffer);
        }
        return stringBuilder.toString();
    }

    @Override
    public void onClick(View v) {
        if (v == btn_write) {
            Intent intent = new Intent(this, NFCWriteActivity.class);
            startActivity(intent);
        }
    }
}
