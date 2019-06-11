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
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.zbl.mytest.R;
import com.example.zbl.mytest.utils.DisplayUtil;

/**
 * 首页守护状态
 */
public class HomeMonitorView extends View implements Choreographer.FrameCallback {

    private int viewWidth, viewHeight;
    private long startTime;
    private int circleColor = 0xff64ACFE;
    private int textSize;
    private int duration = 3600;
    private int r;
    private int line_width;
    private float phase;
    private RectF ovalRectF = new RectF();
    private final float samplingNumber = 20.0f;
    private CornerPathEffect cornerPathEffect;
    private Path sinPath = new Path();

    private Choreographer choreographer;
    private boolean mIsRunning = false;

    private Paint paint;

    private Bitmap bitmap_bg_guarding;
    private String text_guarding, text_attention;

    public int getStatus() {
        return status;
    }

    /**
     * 0 守护中，1 注意
     */
    public void setStatus(int status) {
        this.status = status;
        postInvalidate();
    }

    private int status = 0;//0 守护中，1 注意

    public HomeMonitorView(Context context) {
        super(context);
        initView(context);
    }

    public HomeMonitorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public HomeMonitorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        circleColor = ContextCompat.getColor(context, R.color.colorPrimary);
        textSize = DisplayUtil.dip2Pix(getContext(), 18);
        bitmap_bg_guarding = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
        text_guarding = "守护中";
        text_attention = "警告";

        choreographer = Choreographer.getInstance();
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(circleColor);

        r = DisplayUtil.dip2Pix(getContext(), 100);
        line_width = DisplayUtil.dip2Pix(getContext(), 20);
        cornerPathEffect = new CornerPathEffect(line_width);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        viewWidth = getWidth();
        viewHeight = getHeight();
//        canvas.drawBitmap(bitmap_bg_guarding, (viewWidth - bitmap_bg_guarding.getWidth()) / 2, (viewHeight - bitmap_bg_guarding.getHeight()) / 2, null);

        paint.setStyle(Paint.Style.FILL);

        paint.setColor(circleColor);
        canvas.drawCircle(viewWidth / 2, viewHeight / 2, r - 2 * line_width, paint);

        paint.setTextSize(textSize);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setColor(0xffffffff);
        paint.setPathEffect(null);
        if (status == 0) {
            canvas.drawText(text_guarding, viewWidth / 2, viewHeight / 2 + textSize / 2, paint);
        } else {
            canvas.drawText(text_attention, viewWidth / 2, viewHeight / 2 + textSize / 2, paint);
        }

        paint.setColor(circleColor);
        paint.setStrokeWidth(line_width);
        int currentTime = (int) ((System.currentTimeMillis() - startTime) % duration);
        float percent = ((float) currentTime) / ((float) duration);
        paint.setStyle(Paint.Style.STROKE);//设置空心
        ovalRectF.set(viewWidth / 2 - r, viewHeight / 2 - r, viewWidth / 2 + r, viewHeight / 2 + r);
        float startAngel = 360 * percent;
        float sweepAngle = 200;
        canvas.drawArc(ovalRectF, startAngel, sweepAngle, false, paint);

        paint.setStyle(Paint.Style.STROKE);
        paint.setPathEffect(cornerPathEffect);
//        paint.setStrokeWidth(10);
//        paint.setColor(0xffffff00);
        sinPath.reset();
//        sweepAngle = 0;
        float sin_sweep_angle = 360 - sweepAngle + 2;
        float angle_step = sin_sweep_angle / samplingNumber;
        float sin_start_angle = startAngel + sweepAngle - 1;
        float angle = sin_start_angle;
        float rho;
        float arg1 = 7 * 3.14159f / (360 - sweepAngle);
        float amplitude = line_width / 2;
        for (int i = 0; i <= samplingNumber; i++) {
            float radian = angle * 0.017453f;
            float current_angle = angle - sin_start_angle;
            rho = (float) (Math.sin((current_angle + phase) * arg1) * amplitude * (1 - Math.abs(current_angle - sin_sweep_angle / 2) / (sin_sweep_angle / 2)) + r);
            if (i == 0) {
                sinPath.moveTo(viewWidth / 2 + (float) (rho * Math.cos(radian)), viewHeight / 2 + (float) (rho * Math.sin(radian)));
            } else {
                sinPath.lineTo(viewWidth / 2 + (float) (rho * Math.cos(radian)), viewHeight / 2 + (float) (rho * Math.sin(radian)));
            }
            angle += angle_step;
        }
        canvas.drawPath(sinPath, paint);
        phase += 2;
//        if (phase > 360) {
//            phase = 0;
//        }
    }

    @Override
    public void doFrame(long frameTimeNanos) {
        if (this.mIsRunning) {
            this.invalidate();
            this.choreographer.postFrameCallback(this);
        }
    }

    public void startAnimation() {
        startTime = System.currentTimeMillis();
        if (!this.mIsRunning) {
            this.choreographer.removeFrameCallback(this);
            this.choreographer.postFrameCallback(this);
            this.mIsRunning = true;
        }
    }

    public void stopAnimation() {
        if (this.mIsRunning) {
            this.choreographer.removeFrameCallback(this);
            this.mIsRunning = false;
        }
        startTime = System.currentTimeMillis();
        invalidate();
    }

}
