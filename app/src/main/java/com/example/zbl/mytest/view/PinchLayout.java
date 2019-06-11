package com.example.zbl.mytest.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by zbl on 2018/1/23.
 */

public class PinchLayout extends FrameLayout {

    private static final String TAG = "PinchLayout";
    private static final float MAX_SCALE = 1.5f;
    private static final long ANIMATION_DURATION = 200;

    private ScaleGestureDetector scaleGestureDetector;
    private ScaleGestureDetector.OnScaleGestureListener onScaleGestureListener;
    private GestureDetector gestureDetector;
    private GestureDetector.SimpleOnGestureListener onGestureListener;
    private View childView;
    private float viewX, viewY, viewWidth, viewHeight, translateX, translateY, focusX, focusY;
    private float initX, initY, oX, oY;
    private volatile boolean isPinchBegin = false;
    private boolean scaleEnable = true;

    private OnChildViewLocationChangedListener listener;

    public interface OnChildViewLocationChangedListener {
        /**
         * 子View相对于容器的横向位置
         *
         * @param ratio
         */
        void childViewMoveScaleX(float ratio);

        /**
         * 子View是否已经缩小到最小尺寸
         *
         * @param isMinSize
         */
        void minSize(boolean isMinSize);
    }

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
        onScaleGestureListener = new ScaleGestureDetector.OnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                childView = getChildAt(0);
                if (childView != null) {
                    showLog("FocusX:" + detector.getFocusX() + ",FocusY:" + detector.getFocusY()
                            + ",ScaleFactor:" + detector.getScaleFactor()
                            + ",SpanX" + detector.getCurrentSpanX() + ",SpanY" + detector.getCurrentSpanY());
                    FrameLayout.LayoutParams layoutParams = (LayoutParams) childView.getLayoutParams();
                    float scale = detector.getScaleFactor();
                    int width = (int) (viewWidth * scale);
                    int height = (int) (viewHeight * scale);
                    int maxHeight = (int) (getHeight() * MAX_SCALE);
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
                showLog("onScaleBegin");
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

        onGestureListener = new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                childView = getChildAt(0);
                if (childView != null) {
                    int width = childView.getWidth();
                    int height = childView.getHeight();
                    if (width <= getWidth()) {
                        int maxHeight = (int) (getHeight() * MAX_SCALE);
                        width = maxHeight * width / height;
                        height = maxHeight;
                        setChildViewLocationSmooth((getWidth() - width) / 2, (getHeight() - height) / 2, width, height);
                    } else {
                        height = getWidth() * height / width;
                        width = getWidth();
                        setChildViewLocationSmooth((getWidth() - width) / 2, (getHeight() - height) / 2, width, height);
                    }
                }
                return true;
            }
        };
        gestureDetector = new GestureDetector(context, onGestureListener);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!scaleEnable) {
            return false;
        }
        if (gestureDetector.onTouchEvent(event)) {
            return true;
        }
        childView = getChildAt(0);
        if (childView != null && event.getPointerCount() == 1) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    showLog("on ACTION_DOWN");
                    initX = event.getX();
                    initY = event.getY();
                    oX = childView.getX();
                    oY = childView.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    showLog("on ACTION_MOVE");
                    if (!isPinchBegin) {
                        float x = oX + event.getX() - initX;
                        float y = oY + event.getY() - initY;
                        setChildViewLocation(x, y, childView.getWidth(), childView.getHeight(), "单点移动");
                    }
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    showLog("on ACTION_UP");
                    isPinchBegin = false;
                    setChildViewLocation(childView.getX(), childView.getY(), childView.getWidth(), childView.getHeight(), "手指抬起");
                    break;
                default:
                    showLog("on ACTION_default");
                    break;
            }
        }
        return scaleGestureDetector.onTouchEvent(event);
    }

    public void setChildViewLocation(float x, float y, int width, int height) {
        setChildViewLocation(x, y, width, height, "外部调用");
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
        if (listener != null) {
            float newDeltaWidth = getWidth() - width;
            if (newDeltaWidth >= 0) {
                listener.minSize(true);
            } else {
                listener.minSize(false);
                listener.childViewMoveScaleX(x / newDeltaWidth);
            }

        }
        showLog(tag + "  x:" + x + ",y:" + y + ",width:" + width + ",height:" + height);
    }

    private void setChildViewLocationSmooth(final float x, final float y, final int width, final int height) {
        final float ox = childView.getX();
        final float oy = childView.getY();
        final float oWidth = childView.getWidth();
        final float oHeight = childView.getHeight();
        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(ANIMATION_DURATION);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float progress = (float) animation.getAnimatedValue();
                int dWidth = (int) (oWidth + ((width - oWidth) * progress));
                int dheight = (int) (oHeight + ((height - oHeight) * progress));

                FrameLayout.LayoutParams layoutParams = (LayoutParams) childView.getLayoutParams();
                layoutParams.width = dWidth;
                layoutParams.height = dheight;
                childView.setLayoutParams(layoutParams);
                setChildViewLocation(ox + (x - ox) * progress, oy + (y - oy) * progress, dWidth, dheight, "双击动画");
            }
        });
        animator.start();
    }

    public void setScaleEnable(boolean scaleEnable) {
        this.scaleEnable = scaleEnable;
    }

    public void setOnChildViewLocationChangedListener(OnChildViewLocationChangedListener listener) {
        this.listener = listener;
    }

    private void showLog(String msg) {
        Log.i(TAG, msg);
    }
}
