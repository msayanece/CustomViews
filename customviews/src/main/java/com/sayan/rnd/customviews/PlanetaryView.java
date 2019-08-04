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

import com.sayan.rnd.customviews.utils.PlanetaryViewUtil;

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
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
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
        midCircleRadius = mViewHeight < mViewWidth ? quarterHeight : quarterHeight;

        orbiterCircleRadius = mViewHeight < mViewWidth ? smallerHeight : smallerWidth;
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
        //Save the boolean value from super method for the case of not-handling this event later
        boolean isEventConsumed = super.onTouchEvent(event);
        int eventAction = event.getAction();

        // you may need the x/y location
        int x = (int) event.getX();
        int y = (int) event.getY();

        // put your code in here to handle the event
        switch (eventAction) {
            case MotionEvent.ACTION_DOWN:
                /* First touch - reset circle move flag for checking isOrbitCircleBound
                 * No need, but precaution
                 */
                shouldCircleMove = false;
                //return true here to propagate this touch event to ACTION_MOVE
                return true;
            case MotionEvent.ACTION_UP:
                //Last touch - reset circle move flag
                shouldCircleMove = false;
                break;
            case MotionEvent.ACTION_MOVE:
                //Check if the touch happen inside the circle bound
                if (shouldCircleMove || PlanetaryViewUtil.isOrbitCircleBound(x, y, orbiterCircleX, orbiterCircleY, orbiterCircleRadius)) {
                    //Orbiter touched
                    shouldCircleMove = true;
                    setNewCenterPositionOfOrbiter(x, y);
                    // tell the View to redraw the Canvas
                    postInvalidate();
                    // tell other listeners that we handled the event
                    return true;
                }
                break;
        }

        // We did not handle the event, will work same as super method implementation.
        return isEventConsumed;
    }

    /**
     * This method will set the new position of the orbiter circle after calculating from
     * the touch point (inside the circle)
     * @param x the x value of the touch point
     * @param y the y value of the touch point
     */
    private void setNewCenterPositionOfOrbiter(int x, int y) {
        /* Find the intersection point of the line through the center of orbit &
         * the touch point and the orbit, so that the orbiter can be placed on the
         * intersection point.
         */
        HashMap<String, Double> intersectionPoint = PlanetaryViewUtil.findIntersectionPoint(
                x,
                y,              //the touch point(x,y) must be a point of line
                midCircleX,
                midCircleY,     //the center point of the orbit
                midCircleRadius + orbiterCircleRadius + DISTANCE    //The orbit radius
        );

        if (intersectionPoint.get("x") == null || intersectionPoint.get("y") == null) {
            /* This is an impossible case, but needs to be checking,
             * set the orbiter center on the touch point
             */
            orbiterCircleX = x;
            orbiterCircleY = y;
        } else {
            //Set the orbiter center on the intersection point
            orbiterCircleX = intersectionPoint.get("x").intValue();
            orbiterCircleY = intersectionPoint.get("y").intValue();
        }
        Log.d(TAG, "x:" + intersectionPoint.get("x") + ", y:" + intersectionPoint.get("y"));

        //Unbind the collection object for Garbage Collector
        intersectionPoint.clear();
        intersectionPoint = null;
    }
}
