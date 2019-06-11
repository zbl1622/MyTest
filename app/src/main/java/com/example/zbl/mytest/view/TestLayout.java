package com.example.zbl.mytest.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by zbl on 2017/11/1.
 */

public class TestLayout extends FrameLayout {
    public TestLayout(@NonNull Context context) {
        super(context);
        init(context);
    }

    public TestLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TestLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(@NonNull Context context){
        TextView textView = new TextView(context);
        textView.setText("hahaha");
        addView(textView);
    }
}
