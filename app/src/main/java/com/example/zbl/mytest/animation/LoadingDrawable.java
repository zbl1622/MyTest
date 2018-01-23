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

public class LoadingDrawable extends Drawable {

    int color0 = 0x80ff0000;
    int color1 = 0xa0ff0000;
    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    float p0 = 1f, p1 = 0f;
    boolean pf0, pf1;
    float step = 0.02f;
    float r0, r1;

    public LoadingDrawable() {
        super();
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    @Override
    public void draw(Canvas canvas) {

        Rect rect = getBounds();
        int width = rect.width();
        int height = rect.height();
        int lengh = Math.min(width, height) / 2;
        r0 = (float) (lengh * Math.sin(p0 * Math.PI / 2));
        r1 = (float) (lengh * Math.sin(p1 * Math.PI / 2));
        paint.setColor(color0);
        canvas.drawCircle(width / 2, height / 2, r0, paint);
        paint.setColor(color1);
        canvas.drawCircle(width / 2, height / 2, r1, paint);
        process();
        invalidateSelf();
    }

    private void process() {
        if (pf0) {
            p0 += step;
            if (p0 > 1) {
                p0 = 1;
                pf0 = !pf0;
            }
        } else {
            p0 -= step;
            if (p0 < 0) {
                p0 = 0;
                pf0 = !pf0;
            }
        }

        if (pf1) {
            p1 += step;
            if (p1 > 1) {
                p1 = 1;
                pf1 = !pf1;
            }
        } else {
            p1 -= step;
            if (p1 < 0) {
                p1 = 0;
                pf1 = !pf1;
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
