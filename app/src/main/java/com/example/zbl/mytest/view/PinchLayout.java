package com.example.zbl.mytest.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;

/**
 * Created by zbl on 2018/1/23.
 */

public class PinchLayout extends FrameLayout {

    private static final String TAG = "PinchLayout";

    private ScaleGestureDetector scaleGestureDetector;
    private ScaleGestureDetector.OnScaleGestureListener onScaleGestureListener;
    private View childView;
    private float viewX, viewY, viewWidth, viewHeight, translateX, translateY, focusX, focusY;
    private float initX, initY, oX, oY;
    private volatile boolean isPinchBegin = false;

    private TranslateAnimation translateAnimation;

    public PinchLayout(@NonNull Context context) {
        super(context);
        init(context);
    }

    public PinchLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PinchLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        translateAnimation = new TranslateAnimation(0, 0, 0, 0);
        onScaleGestureListener = new ScaleGestureDetector.OnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                childView = getChildAt(0);
                if (childView != null) {
                    Log.i(TAG, "FocusX:" + detector.getFocusX() + ",FocusY:" + detector.getFocusY()
                            + ",ScaleFactor:" + detector.getScaleFactor()
                            + ",SpanX" + detector.getCurrentSpanX() + ",SpanY" + detector.getCurrentSpanY());
                    FrameLayout.LayoutParams layoutParams = (LayoutParams) childView.getLayoutParams();
                    float scale = detector.getScaleFactor();
                    int width = (int) (viewWidth * scale);
                    int height = (int) (viewHeight * scale);
                    int maxHeight = (int) (getHeight() * 1.5f);
                    if (height > maxHeight) {
                        width = maxHeight * width / height;
                        height = maxHeight;
                        layoutParams.width = width;
                        layoutParams.height = height;
                        childView.setLayoutParams(layoutParams);
                        setChildViewLocation(childView.getX(), childView.getY(), width, height, "高度超出");
                    } else {
                        if (width >= getWidth()) {
                            layoutParams.width = width;
                            layoutParams.height = height;
                            childView.setLayoutParams(layoutParams);
                            float x = focusX - translateX * scale;
                            float y = focusY - translateY * scale;
                            setChildViewLocation(x, y, width, height, "宽度超出");
                        } else {
                            layoutParams.width = getWidth();
                            layoutParams.height = getWidth() * height / width;
                            childView.setLayoutParams(layoutParams);
                            setChildViewLocation(0, childView.getY(), layoutParams.width, layoutParams.height, "宽度太小");
                        }
                    }
                }
                return false;
            }

            @Override
            public boolean onScaleBegin(ScaleGestureDetector detector) {
                Log.i(TAG, "onScaleBegin");
                isPinchBegin = true;
                childView = getChildAt(0);
                if (childView != null) {
                    viewX = childView.getX();
                    viewY = childView.getY();
                    viewWidth = childView.getWidth();
                    viewHeight = childView.getHeight();
                    focusX = detector.getFocusX();
                    focusY = detector.getFocusY();
                    translateX = focusX - viewX;
                    translateY = focusY - viewY;
                }
                return true;
            }

            @Override
            public void onScaleEnd(ScaleGestureDetector detector) {
            }
        };
        scaleGestureDetector = new ScaleGestureDetector(context, onScaleGestureListener);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        childView = getChildAt(0);
        if (childView != null && event.getPointerCount() == 1) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    Log.i(TAG, "on ACTION_DOWN");
                    initX = event.getX();
                    initY = event.getY();
                    oX = childView.getX();
                    oY = childView.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    Log.i(TAG, "on ACTION_MOVE");
                    if (!isPinchBegin) {
                        float x = oX + event.getX() - initX;
                        float y = oY + event.getY() - initY;
                        setChildViewLocation(x, y, childView.getWidth(), childView.getHeight(), "单点移动");
                    }
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    Log.i(TAG, "on ACTION_UP");
                    isPinchBegin = false;
                    setChildViewLocation(childView.getX(), childView.getY(), childView.getWidth(), childView.getHeight(), "手指抬起");
                    break;
                default:
                    Log.i(TAG, "on ACTION_default");
                    break;
            }
        }
        return scaleGestureDetector.onTouchEvent(event);
    }

    private void setChildViewLocation(float x, float y, int width, int height, String tag) {
        if (x > 0) {
            x = 0;
        }
        if (x + width < getWidth()) {
            x = getWidth() - width;
        }
        if (height > getHeight()) {
            if (y > 0) {
                y = 0;
            }
            if (y + height < getHeight()) {
                y = getHeight() - height;
            }
        } else {
//            if (y < 0) {
//                y = 0;
//            }
//            if (y + height > getHeight()) {
//                y = getHeight() - height;
//            }
            y = (getHeight() - height) / 2;//画面高度小于容器时，画面Y维度居中
        }
        childView.setX(x);
        childView.setY(y);
        Log.i(TAG, tag + "  x:" + x + ",y:" + y + ",width:" + width + ",height:" + height);
    }
}
