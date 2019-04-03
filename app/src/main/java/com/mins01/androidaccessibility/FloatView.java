package com.mins01.androidaccessibility;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class FloatView extends View {

    public FloatView(Context context) {
        super(context);


    }

    public FloatView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Paint paint = new Paint();
        paint.setColor(Color.BLUE);
        canvas.drawOval(new RectF(0,0,200,200),paint);
    }

}
