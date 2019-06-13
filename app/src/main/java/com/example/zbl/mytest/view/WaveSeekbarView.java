package com.example.zbl.mytest.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.Choreographer;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.zbl.mytest.R;
import com.example.zbl.mytest.utils.DisplayUtil;

/**
 * 首页守护状态
 */
public class WaveSeekbarView extends View {

    private float max_progress = 100;

    private int viewWidth, viewHeight;
    private int circleColor = 0xff00ff00;
    private int textSize;
    private int r;
    private int line_width;
    private float phase;
    private RectF ovalRectF = new RectF();
    private final int samplingNumber = 10;
    private CornerPathEffect cornerPathEffect;
    private Path sinPath = new Path();

    private Paint paint;

    private float progress_1 = 20, progress_2 = 60;
    private float p1_x, p1_y, p2_x, p2_y;

    public WaveSeekbarView(Context context) {
        super(context);
        initView(context);
    }

    public WaveSeekbarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public WaveSeekbarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
//        circleColor = ContextCompat.getColor(context, R.color.colorPrimary);
        textSize = DisplayUtil.dip2Pix(getContext(), 18);

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(circleColor);

        r = DisplayUtil.dip2Pix(getContext(), 20);
        line_width = DisplayUtil.dip2Pix(getContext(), 2);
        cornerPathEffect = new CornerPathEffect(r);
    }

    private boolean isMove_1, isMove_2;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                if ((x - p1_x) * (x - p1_x) + (y - p1_y) * (y - p1_y) <= r * r) {
                    isMove_1 = true;
                } else if ((x - p2_x) * (x - p2_x) + (y - p2_y) * (y - p2_y) <= r * r) {
                    isMove_2 = true;
                }
            }
            break;
            case MotionEvent.ACTION_MOVE: {
                if (isMove_1) {
                    float p = x * max_progress / viewWidth;
                    if (p + 5 < progress_2) {
                        progress_1 = p;
                    } else {
                        progress_1 = progress_2 - 5;
                    }
                    invalidate();
                } else if (isMove_2) {
                    float p = x * max_progress / viewWidth;
                    if (p > progress_1 + 5) {
                        progress_2 = p;
                    } else {
                        progress_2 = progress_1 + 5;
                    }
                    invalidate();
                }
            }
            break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP: {
                isMove_1 = false;
                isMove_2 = false;
            }
            break;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        viewWidth = getWidth();
        viewHeight = getHeight();

        p1_x = viewWidth * progress_1 / max_progress;
        p1_y = viewHeight / 2f;
        drawCircle(canvas, p1_x, p1_y);
        p2_x = viewWidth * progress_2 / max_progress;
        p2_y = viewHeight / 2f;
        drawCircle(canvas, p2_x, p2_y);

        paint.setStyle(Paint.Style.STROKE);
//        paint.setPathEffect(cornerPathEffect);
        paint.setStrokeWidth(line_width);
        paint.setColor(0xffffffff);

        sinPath.reset();


        /*
        float line_y = p1_y - r * 0.8f;
        sinPath.moveTo(0, line_y);
        sinPath.lineTo(p1_x - r * 2.4f, line_y);

        for (int i = 0; i <= samplingNumber; i++) {
            float angle = (140 - 100f * i / samplingNumber) * 0.017453f;
            float x = (float) (Math.cos(angle) * 2 * r);
            float y = (float) (Math.sin(angle) * 2 * r);
            sinPath.lineTo(x + p1_x, p1_y - y);
        }
        sinPath.lineTo(p1_x + r * 2.4f, line_y);

        sinPath.lineTo(p2_x - r * 2.4f, line_y);
        for (int i = 0; i <= samplingNumber; i++) {
            float angle = (140 - 100f * i / samplingNumber) * 0.017453f;
            float x = (float) (Math.cos(angle) * 2 * r);
            float y = (float) (Math.sin(angle) * 2 * r);
            sinPath.lineTo(x + p2_x, p2_y - y);
        }
        sinPath.lineTo(p2_x + r * 2.4f, line_y);

        sinPath.lineTo(viewWidth, line_y);
        */

        /*
        float rr = 1.5f * r;
        float line_y = p1_y - rr * 0.41421356f;
        sinPath.moveTo(0, line_y);
        float o1_x = p1_x - rr * 1.41421356f;
        sinPath.lineTo(o1_x, line_y);
        sinPath.addArc(o1_x - rr, line_y - 2 * rr, o1_x + rr, line_y, 45, 45);
        sinPath.addArc(p1_x - rr, p1_y - rr, p1_x + rr, p1_y + rr, 225, 90);
        float o2_x = p1_x + rr * 1.41421356f;
        sinPath.addArc(o2_x - rr, line_y - 2 * rr, o2_x + rr, line_y, 90, 45);
        sinPath.moveTo(o2_x, line_y);

        float o3_x = p2_x - rr * 1.41421356f;
        sinPath.lineTo(o3_x, line_y);
        sinPath.addArc(o3_x - rr, line_y - 2 * rr, o3_x + rr, line_y, 45, 45);
        sinPath.addArc(p2_x - rr, p2_y - rr, p2_x + rr, p2_y + rr, 225, 90);
        float o4_x = p2_x + rr * 1.41421356f;
        sinPath.addArc(o4_x - rr, line_y - 2 * rr, o4_x + rr, line_y, 90, 45);
        sinPath.moveTo(o4_x, line_y);

        sinPath.lineTo(viewWidth, line_y);

        canvas.drawPath(sinPath, paint);
        */
        float rr = 1.5f * r;
        float line_y = p1_y - rr * 0.41421356f;
        float o1_x = p1_x - rr * 1.41421356f;
        canvas.drawLine(0, line_y, o1_x, line_y, paint);
        canvas.drawArc(o1_x - rr, line_y - 2 * rr, o1_x + rr, line_y, 45, 45, false, paint);
        canvas.drawArc(p1_x - rr, p1_y - rr, p1_x + rr, p1_y + rr, 225, 90, false, paint);
        float o2_x = p1_x + rr * 1.41421356f;
        canvas.drawArc(o2_x - rr, line_y - 2 * rr, o2_x + rr, line_y, 90, 45, false, paint);

        float o3_x = p2_x - rr * 1.41421356f;
        canvas.drawLine(o2_x, line_y, o3_x, line_y, paint);
        canvas.drawArc(o3_x - rr, line_y - 2 * rr, o3_x + rr, line_y, 45, 45, false, paint);
        canvas.drawArc(p2_x - rr, p2_y - rr, p2_x + rr, p2_y + rr, 225, 90, false, paint);
        float o4_x = p2_x + rr * 1.41421356f;
        canvas.drawArc(o4_x - rr, line_y - 2 * rr, o4_x + rr, line_y, 90, 45, false, paint);

        canvas.drawLine(o4_x, line_y, viewWidth, line_y, paint);

//        paint.setTextSize(textSize);
//        paint.setTextAlign(Paint.Align.CENTER);
//        paint.setColor(0xffffffff);
//        paint.setPathEffect(null);
//
//        paint.setColor(circleColor);
//        paint.setStrokeWidth(line_width);
//        int currentTime = (int) ((System.currentTimeMillis() - startTime) % duration);
//        float percent = ((float) currentTime) / ((float) duration);
//        paint.setStyle(Paint.Style.STROKE);//设置空心
//        ovalRectF.set(viewWidth / 2 - r, viewHeight / 2 - r, viewWidth / 2 + r, viewHeight / 2 + r);
//        float startAngel = 360 * percent;
//        float sweepAngle = 200;
//        canvas.drawArc(ovalRectF, startAngel, sweepAngle, false, paint);
//
//        float sin_sweep_angle = 360 - sweepAngle + 2;
//        float angle_step = sin_sweep_angle / samplingNumber;
//        float sin_start_angle = startAngel + sweepAngle - 1;
//        float angle = sin_start_angle;
//        float rho;
//        float arg1 = 7 * 3.14159f / (360 - sweepAngle);
//        float amplitude = line_width / 2;
//        for (int i = 0; i <= samplingNumber; i++) {
//            float radian = angle * 0.017453f;
//            float current_angle = angle - sin_start_angle;
//            rho = (float) (Math.sin((current_angle + phase) * arg1) * amplitude * (1 - Math.abs(current_angle - sin_sweep_angle / 2) / (sin_sweep_angle / 2)) + r);
//            if (i == 0) {
//                sinPath.moveTo(viewWidth / 2 + (float) (rho * Math.cos(radian)), viewHeight / 2 + (float) (rho * Math.sin(radian)));
//            } else {
//                sinPath.lineTo(viewWidth / 2 + (float) (rho * Math.cos(radian)), viewHeight / 2 + (float) (rho * Math.sin(radian)));
//            }
//            angle += angle_step;
//        }
//        canvas.drawPath(sinPath, paint);
//        phase += 2;
    }

    private void drawCircle(Canvas canvas, float x, float y) {
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(circleColor);
        paint.setStrokeWidth(r * 2 / 3f);
        canvas.drawCircle(x, y, r * 2 / 3f, paint);
    }

}
