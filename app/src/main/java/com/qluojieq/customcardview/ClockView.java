package com.qluojieq.customcardview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by shiliushuo-1 on 16/7/29.
 */

public class ClockView extends View {
    private float mBorderWidth = 1f;
    private int mBorderColor = 0xffffff;

    private Paint mPaint;

    private RectF mBounds;
    private float width;
    private float height;
    float radius;
    float smallLength;
    float largeLength;

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mBorderWidth);
        mPaint.setColor(mBorderColor);

    }

    public ClockView(Context context) {
        super(context);
        init();

    }

    public ClockView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ClockView, 0, 0);
        try {
            mBorderColor = typedArray.getColor(R.styleable.ClockView_broadColor, 0xff000000);
            mBorderWidth = typedArray.getDimension(R.styleable.ClockView_broadWidth, 2);
        } finally {
            typedArray.recycle();
        }
        init();
    }

    public ClockView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(0xff000000);
        mPaint.setColor(0x66555555);
        canvas.drawRoundRect(new RectF(20,20, getWidth()-20,getHeight()-20), 40, 40, mPaint);

    }

}
