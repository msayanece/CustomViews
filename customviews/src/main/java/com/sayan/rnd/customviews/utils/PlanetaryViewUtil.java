package com.sayan.rnd.customviews.utils;

import java.util.HashMap;

public class PlanetaryViewUtil {

    /**
     * This method will find the intersection point of a circle with center point & radius and
     * a line crossing through the center of circle and another point.
     * By calculating the slope of the line and mapping the slope to a circle angle 360 degrees,
     * then finding the point on the perimeter of the circle having same circular angle as the slop.
     *
     * @param lineX1                 the x value of the touch point through which the line passes
     * @param lineY1                 the y value of the touch point through which the line passes
     * @param lineorCenterOfCircleX2 the x value of the center of the circle.
     *                               Also the line passes through this point
     * @param lineorCenterOfCircleY2 the y value of the center of the circle.
     *                               Also the line passes through this point
     * @param midCircleRadius        the radius of the circle
     * @return a HashMap object: the collection of (x,y) of the intersection point
     */
    public static HashMap<String, Double> findIntersectionPoint(double lineX1, double lineY1,
                                                                 double lineorCenterOfCircleX2, double lineorCenterOfCircleY2,
                                                                 double midCircleRadius) {
        final String TAG = "findIntersectionPoint";
        System.out.println(TAG + "=> Center:" + lineorCenterOfCircleX2 + "," + lineorCenterOfCircleY2);
        System.out.println(TAG + "=> end:" + lineX1 + "," + lineY1);

        //ready the output holder
        HashMap<String, Double> point = new HashMap<>();
        //initialize other local variables
        double x = 0, y = 0;
        double mSlope = 0;
        double slopAngleInRadian = 0;
        double slopAngleInDegree = 0;

        //Find the slope of the line passing through the center of the circle & the touch point(x,y)
        mSlope = findSlopeOfLine(lineX1, lineY1, lineorCenterOfCircleX2, lineorCenterOfCircleY2);
        slopAngleInRadian = Math.atan(mSlope);
        slopAngleInDegree = Math.toDegrees(slopAngleInRadian);

        System.out.println(TAG + "=> ACTUAL_SLOPE:" + slopAngleInDegree);

        //get the circular angle in degrees
        slopAngleInDegree = convertSlopeToCircleAngle(
                lineX1, lineY1,
                lineorCenterOfCircleX2, lineorCenterOfCircleY2,
                slopAngleInDegree
        );

        System.out.println(TAG + "=> SLOPE:" + mSlope + ", RAD:" + slopAngleInRadian + ", DEG:" + slopAngleInDegree);
        System.out.println(TAG + "=> Radius:" + midCircleRadius + ", centerX:" + lineorCenterOfCircleX2 + ", centerY:" + lineorCenterOfCircleY2);

        /* For a circle with origin (j, k) and radius r
         * x(t) = r cos(t) + j, y(t) = r sin(t) + k
         * where you need to run this equation for t taking values within the range from 0 to 360,
         * then you will get your x and y each on the boundary of the circle.
         */

        //find intersection point values
        x = findPointOnPerimeterUsingSlopeAngleX(lineorCenterOfCircleX2, midCircleRadius, Math.toRadians(slopAngleInDegree));
        y = findPointOnPerimeterUsingSlopeAngleY(lineorCenterOfCircleY2, midCircleRadius, Math.toRadians(slopAngleInDegree));

        //put the point value into the HashMap and return
        point.put("x", x);
        point.put("y", y);
        return point;
    }

    /**
     * Find the x value of the point on the perimeter of a circle with circle-angle given
     * @param centerOfCircleX the x value of the center of the circle
     * @param circleRadius the radius of the circle
     * @param slopAngleInRad the angle of the point in the circle
     * @return the x value of the perimeter-point in double
     */
    public static double findPointOnPerimeterUsingSlopeAngleX(double centerOfCircleX, double circleRadius, double slopAngleInRad) {
        double x;
        x = (circleRadius * Math.cos(slopAngleInRad)) + centerOfCircleX;
        return x;
    }

    /**
     * Find the y value of the point on the perimeter of a circle with circle-angle given
     * @param centerOfCircleY the y value of the center of the circle
     * @param circleRadius the radius of the circle
     * @param slopAngleInRad the angle of the point in the circle
     * @return the y value of the perimeter-point in double
     */
    public static double findPointOnPerimeterUsingSlopeAngleY(double centerOfCircleY, double circleRadius, double slopAngleInRad) {
        double y;
        /* According to the Android display matrix (x,y) point position,
         * actually we are in the 4rd quadrant of a big display graph,
         * so the increase in y value must be mapped to the decrease in y value.
         * Thus taking the negative of the result as y value of the point
         */
        y = -(circleRadius * Math.sin(slopAngleInRad)) + centerOfCircleY;
        return y;
    }

    /**
     * Convert the slope (ratio of rise: run) in double value to degree value according
     * to the angle system of a circle.
     * This is required for the android display pixel position system. Where (x,y) = (0,0) point
     * is on the top left corner. Thus we need to calibrate the degrees.
     * @param lineX1 the x value of the touch point
     * @param lineY1 the y value of the touch point
     * @param lineorCenterOfCircleX2 the x value of the center of the circle
     * @param lineorCenterOfCircleY2 the y value of the center of the circle
     * @param slopAngleInDegree the slope (ratio of rise: run) in double value
     * @return the slope in degree (a double value) according to the circular system of angle
     */
    public static double convertSlopeToCircleAngle(double lineX1, double lineY1, double lineorCenterOfCircleX2, double lineorCenterOfCircleY2, double slopAngleInDegree) {
        //convert the slope according to the quadrant of circular angle
        if ((lineorCenterOfCircleX2 - lineX1) < 0 && (lineorCenterOfCircleY2 - lineY1) >= 0) {
            //1st quadrant
            slopAngleInDegree = -slopAngleInDegree;
        } else if ((lineorCenterOfCircleX2 - lineX1) >= 0 && (lineorCenterOfCircleY2 - lineY1) >= 0) {
            //2nd quadrant
            slopAngleInDegree = 180 - slopAngleInDegree;
        } else if ((lineorCenterOfCircleX2 - lineX1) >= 0 && (lineorCenterOfCircleY2 - lineY1) < 0) {
            //3rd quadrant
            slopAngleInDegree = 180 - slopAngleInDegree;
        } else {
            //4th quadrant
            slopAngleInDegree = 360 - slopAngleInDegree;
        }
        return slopAngleInDegree;
    }

    /**
     * Find the slope (ratio of rise: run) of the line passing through the center of the circle & the touch point(x,y)
     * using this equation m = (y1-y)/(x1-x)
     * @param lineX1 //x value of First point
     * @param lineY1 //Y value of First point
     * @param lineX2 //x value of First point
     * @param lineY2 //y value of Second point
     * @return the slope (ratio of rise: run) in double value
     */
    public static double findSlopeOfLine(double lineX1, double lineY1, double lineX2, double lineY2) {
        double mSlope;
        mSlope = (lineY2 - lineY1) / (lineX2 - lineX1);
        return mSlope;
    }

    /**
     * Check if a point is inside a circle bound or not. If inside return true else false
     * @param x the x value of a point
     * @param y the y value of a point
     * @param centerX the x value of the center of a circle
     * @param centerY the y value of the center of a circle
     * @param radius the radius of a circle
     * @return true if the point is inside the circle, false if the point is outside
     */
    public static boolean isOrbitCircleBound(int x, int y, int centerX, int centerY, int radius) {
        double dx = Math.pow(x - centerX, 2);
        double dy = Math.pow(y - centerY, 2);
        return dx + dy < Math.pow(radius, 2);
    }
}
