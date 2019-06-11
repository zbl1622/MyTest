package com.example.zbl.mytest;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.zbl.mytest.utils.DisplayUtil;
import com.example.zbl.mytest.view.PinchLayout;

/**
 * Created by zbl on 2017/2/27.
 */

public class PinchViewActivity extends AppCompatActivity {

    private Context context;
    private PinchLayout pinchLayout;
    private ImageView imageView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_pinchview);
        pinchLayout = (PinchLayout) findViewById(R.id.pinchLayout);
        imageView = new ImageView(context);
        imageView.setImageResource(R.mipmap.doorbell_default);
        imageView.setBackgroundColor(0xff808080);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                DisplayUtil.dip2Pix(context, 300));
        layoutParams.gravity = Gravity.CENTER;
        pinchLayout.addView(imageView, layoutParams);
    }
}
