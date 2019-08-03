package com.sayan.rnd.customviews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.HashMap;


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
    private final String TAG = "PlanetaryView";

    private boolean shouldCircleMove = false;

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
                //return true here to propagate this touch event to ACTION_MOVE
                shouldCircleMove = false;
                return true;
            case MotionEvent.ACTION_UP:
                shouldCircleMove = false;
                break;
            case MotionEvent.ACTION_MOVE:
                if (isOrbitCircleBound(x, y)){
                    //Orbiter touched
                    shouldCircleMove = true;
                    HashMap<String, Double> intersectionPoint =
                            findIntersectionPoint(x, y, midCircleX, midCircleY, midCircleRadius + orbiterCircleRadius + DISTANCE);
                    if (intersectionPoint.get("x") == null || intersectionPoint.get("y") == null){
                        orbiterCircleX = x;
                        orbiterCircleY = y;
                    }else {
                        orbiterCircleX = intersectionPoint.get("x").intValue();
                        orbiterCircleY = intersectionPoint.get("y").intValue();
                    }
                    Log.d(TAG, "x:" + intersectionPoint.get("x") + ", y:" + intersectionPoint.get("y"));
                    intersectionPoint.clear();
                    intersectionPoint = null;
                    // tell the View to redraw the Canvas
                    postInvalidate();
                    return true;
                }
                break;
        }

        // tell the View that we handled the event
        return isEventConsumed;
    }

    private HashMap<String, Double> findIntersectionPoint(double lineX1, double lineY1,
                                       double lineorCenterOfCircleX2, double lineorCenterOfCircleY2,
                                       double midCircleRadius) {
        //ready the output
        HashMap<String, Double> point = new HashMap<>();
        //get the line equation: (y1-y)/(x1-x) = m
        double mSlope = 0;
        Log.d(TAG, "Center:" + lineorCenterOfCircleX2 + "," + lineorCenterOfCircleY2);
        Log.d(TAG, "end:" + lineX1 + "," + lineY1);
        mSlope = (lineorCenterOfCircleY2 - lineY1) /(lineorCenterOfCircleX2 - lineX1);
        double slopAngleInRadian = Math.atan(mSlope);
        double slopAngleInDegree = Math.toDegrees(slopAngleInRadian);
        Log.d(TAG, "ACTUAL_SLOPE:" + slopAngleInDegree);

        if ((lineorCenterOfCircleX2-lineX1) < 0 && (lineorCenterOfCircleY2-lineY1) >= 0 ){
            //1st quadrant
            slopAngleInDegree = -slopAngleInDegree;
        }else if ((lineorCenterOfCircleX2-lineX1) >= 0 && (lineorCenterOfCircleY2-lineY1) >= 0 ) {
            //2nd quadrant
            slopAngleInDegree = 180 - slopAngleInDegree;
        }else if ((lineorCenterOfCircleX2-lineX1) >= 0 && (lineorCenterOfCircleY2-lineY1) < 0 ){
            //3rd quadrant
            slopAngleInDegree = 180 - slopAngleInDegree;
        }else {
            //4th quadrant
            slopAngleInDegree = 360 - slopAngleInDegree;
        }
//        if ((lineX1 - lineorCenterOfCircleX2) <= 0) {
//            mSlope = -mSlope;
//        }
        Log.d(TAG, "SLOPE:" + mSlope + ", RAD:" + slopAngleInRadian + ", DEG:" + slopAngleInDegree);
        Log.d(TAG, "Radius:" + midCircleRadius + ", centerX:" + lineorCenterOfCircleX2 + ", centerY:" + lineorCenterOfCircleY2);

        /* For a circle with origin (j, k) and radius r
         * x(t) = r cos(t) + j, y(t) = r sin(t) + k
         * where you need to run this equation for t taking values within the range from 0 to 360,
         * then you will get your x and y each on the boundary of the circle.
         */
        double x = 0, y = 0;
        //find intersection point with respect to (0,0) as center
        x = (midCircleRadius * Math.cos(Math.toRadians(slopAngleInDegree))) + lineorCenterOfCircleX2;
        y = -(midCircleRadius * Math.sin(Math.toRadians(slopAngleInDegree))) + lineorCenterOfCircleY2;
        //find intersection point with respect to the actual circle's center

        point.put("x", x);
        point.put("y", y);
        return point;
    }

    private boolean isOrbitCircleBound(int x, int y) {
//        if (x > (orbiterCircleX - orbiterCircleRadius) && x < (orbiterCircleX + orbiterCircleRadius)){
//            return y > (orbiterCircleY - orbiterCircleRadius) && y < (orbiterCircleY + orbiterCircleRadius);
//        }
//        return false;

        if (shouldCircleMove) return true;
        double dx = Math.pow(x - orbiterCircleX, 2);
        double dy = Math.pow(y - orbiterCircleY, 2);
        return dx + dy < Math.pow(orbiterCircleRadius, 2);
    }
}
