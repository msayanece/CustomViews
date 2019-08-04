package com.sayan.rnd.customviews;

import android.content.Context;
import android.content.res.TypedArray;
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

    private final String TAG = "PlanetaryView";

    private boolean isFirstInit = false;
    private boolean shouldCircleMove = false;

    //attrs
    private float mCenterCircleRadius;
    private float mOrbiterRadius;
    private float mOrbitDistance;
    private float mOrbitStroke;
    private int mCenterCircleColor;
    private int mOrbiterColor;
    private int mOrbitStrokeColor;

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
        //attrs
        //get TypedArray for PlanetaryView for obtaining the attribute value
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.PlanetaryView);
        //get the attrs
        mCenterCircleRadius = typedArray.getDimension(R.styleable.PlanetaryView_centerCircleRadius, 0);
        mOrbiterRadius = typedArray.getDimension(R.styleable.PlanetaryView_orbiterRadius, 0);
        mOrbitDistance = typedArray.getDimension(R.styleable.PlanetaryView_orbitDistance, 0);
        mOrbitStroke = typedArray.getDimension(R.styleable.PlanetaryView_orbitStroke, 5);

        mCenterCircleColor = typedArray.getColor(R.styleable.PlanetaryView_centerCircleColor, Color.RED);
        mOrbiterColor = typedArray.getColor(R.styleable.PlanetaryView_orbiterColor, Color.BLUE);
        mOrbitStrokeColor = typedArray.getColor(R.styleable.PlanetaryView_orbitStrokeColor, Color.GRAY);
        //free for Garbage Collector
        typedArray.recycle();

        //paints
        mMiddleCirclePaint = new Paint();
        mMiddleCirclePaint.setAntiAlias(true);
        mOrbiterCirclePaint = new Paint();
        mOrbiterCirclePaint.setAntiAlias(true);
        orbitPaint = new Paint();
        orbitPaint.setAntiAlias(true);

        isFirstInit = true;
    }

    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mViewWidth = w;
        mViewHeight = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int halfWidth = mViewWidth / 2;
        int halfHeight = mViewHeight / 2;

        midCircleX = halfWidth;
        midCircleY = halfHeight;

        //other initialization
        int quarterWidth = mViewWidth / 4;
        int smallerWidth = mViewWidth / 8;

        int quarterHeight = mViewHeight / 4;
        int smallerHeight = mViewHeight / 8;

        if (mCenterCircleRadius == 0) {
            midCircleRadius = mViewHeight < mViewWidth ? quarterHeight : quarterWidth;
        } else {
            midCircleRadius = (int) mCenterCircleRadius;
        }

        if (mOrbiterRadius == 0) {
            orbiterCircleRadius = mViewHeight < mViewWidth ? smallerHeight : smallerWidth;
        } else {
            orbiterCircleRadius = (int) mOrbiterRadius;
        }

        float orbitRadius = midCircleRadius + orbiterCircleRadius + mOrbitDistance;

        if (isFirstInit) {
            orbiterCircleX = (int) (midCircleX + orbitRadius);
            orbiterCircleY = (int) (midCircleY);
            isFirstInit = false;
        }

        mMiddleCirclePaint.setColor(mCenterCircleColor);
        mOrbiterCirclePaint.setColor(mOrbiterColor);

        orbitPaint.setColor(mOrbitStrokeColor);
        orbitPaint.setStrokeWidth(mOrbitStroke);
        orbitPaint.setStyle(Paint.Style.STROKE);

        setNewCenterPositionOfOrbiter(orbiterCircleX, orbiterCircleY);
        canvas.drawCircle(midCircleX, midCircleY, midCircleRadius, mMiddleCirclePaint);
        canvas.drawCircle(midCircleX, midCircleY, orbitRadius, orbitPaint);
        canvas.drawCircle(orbiterCircleX, orbiterCircleY, orbiterCircleRadius, mOrbiterCirclePaint);
//        canvas.drawLine(midCircleX, midCircleY, orbiterCircleX, orbiterCircleY, mOrbiterCirclePaint);
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
     *
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
                midCircleRadius + orbiterCircleRadius + mOrbitDistance    //The orbit radius
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

    public void setCenterCircleRadius(float radiusDimen) {
        if (radiusDimen >= 0) {
            mCenterCircleRadius = radiusDimen;
        }
        postInvalidate();
    }

    public void setOrbiterRadius(float orbiterRadiusDimen) {
        if (mOrbiterRadius >= 0) {
            mOrbiterRadius = orbiterRadiusDimen;
        }
        postInvalidate();
    }

    public void setOrbitDistance(float orbitDistanceDimen) {
        mOrbitDistance = orbitDistanceDimen;
        postInvalidate();
    }

    public void setOrbitStroke(float OrbitStrokeDimen) {
        if (mOrbitStroke >= 0) {
            mOrbitStroke = OrbitStrokeDimen;
        }
        postInvalidate();
    }

    public void setCenterCircleColor(int centerCircleColor) {
        mCenterCircleColor = centerCircleColor;
        postInvalidate();
    }

    public void setOrbiterColor(int orbiterColor) {
        mOrbiterColor = orbiterColor;
        postInvalidate();
    }

    public void setOrbitStrokeColor(int orbitStrokeColor) {
        mOrbitStrokeColor = orbitStrokeColor;
        postInvalidate();
    }

    public float getCenterCircleRadius() {
        return mCenterCircleRadius;
    }

    public float getOrbiterRadius() {
        return mOrbiterRadius;
    }

    public float getOrbitDistance() {
        return mOrbitDistance;
    }

    public float getOrbitStroke() {
        return mOrbitStroke;
    }

    public int getCenterCircleColor() {
        return mCenterCircleColor;
    }

    public int getOrbiterColor() {
        return mOrbiterColor;
    }

    public int getOrbitStrokeColor() {
        return mOrbitStrokeColor;
    }
}
