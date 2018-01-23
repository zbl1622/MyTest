package com.example.zbl.mytest;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by zbl on 2017/2/27.
 */

public class MyView extends View {
    private static final String TAG = "MyView";

    public MyView(Context context) {
        super(context);
        Log.w(TAG, "构造方法1");
    }

    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.w(TAG, "构造方法2");
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        Log.w(TAG, "onAttachedToWindow");
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Log.w(TAG, "onDetachedFromWindow");
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.w(TAG, "onMeasure");
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        Log.w(TAG, "onLayout");
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        Log.w(TAG, "onVisibilityChanged:" + visibility);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.w(TAG, "onDraw");
        canvas.drawColor(0xFFFFFF00);
    }
}
