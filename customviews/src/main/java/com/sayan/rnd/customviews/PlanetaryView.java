package com.sayan.rnd.customviews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

public class PlanetaryView extends View {
    private int mViewWidth;
    private int mViewHeight;
    private Paint mMiddleCirclePaint;
    private Paint mOrbiterCirclePaint;
    private Paint orbitPaint;
    private int orbiterCircleRadius;
    private int orbiterCircleX;
    private int orbiterCircleY;
    private int midCircleX;
    private int midCircleY;
    private int midCircleRadius;

    private final int DISTANCE = 30;


    public PlanetaryView(Context context) {
        super(context);
        init(null);
    }

    public PlanetaryView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public PlanetaryView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public PlanetaryView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(@Nullable AttributeSet attrs) {
        if (attrs == null) return;
        mMiddleCirclePaint = new Paint();
        mMiddleCirclePaint.setAntiAlias(true);
        mOrbiterCirclePaint = new Paint();
        mOrbiterCirclePaint.setAntiAlias(true);
        orbitPaint = new Paint();
        orbitPaint.setAntiAlias(true);
    }

    @Override
    public void onSizeChanged (int w, int h, int oldw, int oldh){
        super.onSizeChanged(w, h, oldw, oldh);
        mViewWidth = w;
        mViewHeight = h;

        //other initialization
        int halfWidth = mViewWidth / 2;
        int quarterWidth = mViewWidth / 4;
        int smallerWidth = mViewWidth / 8;

        int halfHeight = mViewHeight / 2;
        int quarterHeight = mViewHeight / 4;
        int smallerHeight = mViewHeight / 8;

        midCircleX = halfWidth;
        midCircleY = halfHeight;
        midCircleRadius = mViewHeight<mViewWidth ? quarterHeight : quarterHeight;

        orbiterCircleRadius = mViewHeight<mViewWidth ? smallerHeight : smallerWidth;
        orbiterCircleX = midCircleX + midCircleRadius /*+ orbiterCircleRadius*/ + DISTANCE;
        orbiterCircleY = midCircleY + midCircleRadius /*+ orbiterCircleRadius*/ + DISTANCE;

        mMiddleCirclePaint.setColor(Color.RED);
        mOrbiterCirclePaint.setColor(Color.BLUE);

        orbitPaint.setColor(Color.BLACK);
        orbitPaint.setStrokeWidth(5);
        orbitPaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawCircle(midCircleX, midCircleY, midCircleRadius, mMiddleCirclePaint);
        canvas.drawCircle(midCircleX, midCircleY, midCircleRadius + orbiterCircleRadius + DISTANCE, orbitPaint);
        canvas.drawCircle(orbiterCircleX, orbiterCircleY, orbiterCircleRadius, mOrbiterCirclePaint);
        canvas.drawLine(midCircleX, midCircleY, orbiterCircleX, orbiterCircleY, mOrbiterCirclePaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean isEventConsumed = super.onTouchEvent(event);
        int eventAction = event.getAction();

        // you may need the x/y location
        int x = (int)event.getX();
        int y = (int)event.getY();

        // put your code in here to handle the event
        switch (eventAction) {
            case MotionEvent.ACTION_DOWN:
                return true;
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_MOVE:
                if (isOrbitCircleBound(x, y)){
                    //Orbiter touched
                    orbiterCircleX = x;
                    orbiterCircleY = y;

                    // tell the View to redraw the Canvas
                    postInvalidate();
                    return true;
                }
                break;
        }

        // tell the View that we handled the event
        return isEventConsumed;
    }

    private boolean isOrbitCircleBound(int x, int y) {
//        if (x > (orbiterCircleX - orbiterCircleRadius) && x < (orbiterCircleX + orbiterCircleRadius)){
//            return y > (orbiterCircleY - orbiterCircleRadius) && y < (orbiterCircleY + orbiterCircleRadius);
//        }
//        return false;

        double dx = Math.pow(x - orbiterCircleX, 2);
        double dy = Math.pow(y - orbiterCircleY, 2);
        return dx + dy < Math.pow(orbiterCircleRadius, 2);
    }
}
