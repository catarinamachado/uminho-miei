package Utils;

import java.awt.geom.Point2D;
import java.util.Map;

public class Utility {

    public static String getClosest(Point2D me, Map<String, RobotData> lst, RobotData.TYPE t){
        String target = "";
        double dist_target = Integer.MAX_VALUE;
        for (String en : lst.keySet()){
            if(lst.get(en).getType() == t) {
                Point2D pos = lst.get(en).getPos();
                double distance = pos.distance(me);
                if (distance < dist_target) {
                    dist_target = distance;
                    target = en;
                }
            }
        }
        return target;
    }

    // computes the absolute bearing between two points
    public static double absoluteBearing(double x1, double y1, double x2, double y2) {
        double xo = x2-x1;
        double yo = y2-y1;
        double hyp = Point2D.distance(x1, y1, x2, y2);
        double arcSin = Math.toDegrees(Math.asin(xo / hyp));
        double bearing = 0;

        if (xo > 0 && yo > 0) { // both pos: lower-Left
            bearing = arcSin;
        } else if (xo < 0 && yo > 0) { // x neg, y pos: lower-right
            bearing = 360 + arcSin; // arcsin is negative here, actually 360 - ang
        } else if (xo > 0 && yo < 0) { // x pos, y neg: upper-left
            bearing = 180 - arcSin;
        } else if (xo < 0 && yo < 0) { // both neg: upper-right
            bearing = 180 - arcSin; // arcsin is negative here, actually 180 + ang
        }

        return bearing;
    }

    // normalizes a bearing to between +180 and -180
    public static double normalizeBearing(double angle) {
        while (angle >  180) angle -= 360;
        while (angle < -180) angle += 360;
        return angle;
    }
}