package com.example.zbl.mytest.animation;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

/**
 * Created by zbl on 2017/3/9.
 */

public class PendulumDrawable extends Drawable {

    int color0 = 0xc0ff0000;
    int color1 = 0xf0ff0000;
    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    float progress = 0f;
    boolean pf = true;
    float step = 0.02f;
    float ball_r = 12;

    public PendulumDrawable() {
        super();
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeWidth(3);
    }

    @Override
    public void draw(Canvas canvas) {

        Rect rect = getBounds();
        int width = rect.width();
        int height = rect.height();
        float lengh = height * 0.618f;
        double max_angle = Math.asin((width / 2 - ball_r) / lengh);
        float ox = width / 2;
        float oy = 0;
        double current_angle = max_angle * Math.cos(progress * Math.PI / 2);
        float bx = (float) (lengh * Math.sin(current_angle)) + ox;
        float by = (float) (lengh * Math.cos(current_angle));
        paint.setColor(color0);
        canvas.drawLine(ox, oy, bx, by, paint);
        paint.setColor(color1);
        canvas.drawCircle(bx, by, ball_r, paint);
        process();
        invalidateSelf();
    }

    private void process() {
        if (pf) {
            progress += step;
            if (progress > 2) {
                progress = 2;
                pf = !pf;
            }
        } else {
            progress -= step;
            if (progress < 0) {
                progress = 0;
                pf = !pf;
            }
        }
    }

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSPARENT;
    }
}
