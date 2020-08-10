import robocode.AdvancedRobot;
import robocode.Condition;
import robocode.CustomEvent;
import robocode.ScannedRobotEvent;
import robocode.util.Utils;
import standardOdometer.Odometer;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class SmartRobotV2 extends AdvancedRobot {

    private static int SIZE = 38;
    private Odometer  odometer_default = new Odometer("isRacing", this);
    private boolean start = false;
    private boolean scanningObject = false;
    private ArrayList<EnemyRobot> objects = new ArrayList<>();
    private RoundOdometer round_odometer = new RoundOdometer("isMoving", this);;

    public void run(){
        addCustomEvent(odometer_default);
        addCustomEvent(this.round_odometer);
        if(!start)
            getToStartingPosition();
        System.out.println("Starting");
        round_odometer.beginRace();
        for (int i = 0; i < 150; i++) {
            doNothing(); // or perhaps scan();
        }
        startRace();
    }

    private void startRace() {
        //Adjust to reference point
        this.turnLeft(this.getHeading());
        this.scanningObject = true;

        //Starts preparing path
        preparePath();
        System.out.println("Finished probing: " + this.objects.size());
        this.turnLeft(this.getHeading());

        //First obstacle
        EnemyRobot p = this.objects.get(0);
        List<Point2D> vertex = p.getVertexPoints(SIZE);
        int[] index1 = {0,2,4,5};
        for(int i = 0; i < 4; i++){
            Point2D v = vertex.get(index1[i]);
            double x = v.getX() - this.getX();
            double y = v.getY() - this.getY();
            goTo(x,y, 0);
        }

        //Second obstacle
        p = this.objects.get(1);
        vertex = p.getVertexPoints(SIZE);
        int[] index2 = {1,2,4,6,7};
        for(int i = 0; i < 5; i++){
            Point2D v = vertex.get(index2[i]);
            double x = v.getX() - this.getX();
            double y = v.getY() - this.getY();
            goTo(x,y, 0);
        }
        //Third obstacle
        p = this.objects.get(2);
        vertex = p.getVertexPoints(SIZE);
        int[] index3 = {3,4,6,7};
        for(int i = 0; i < 4; i++){
            Point2D v = vertex.get(index3[i]);
            double x = v.getX() - this.getX();
            double y = v.getY() - this.getY();
            goTo(x,y, 0);
        }
        //Final lap
        goTo(18 - this.getX(),18 - this.getY(),0);

        System.out.println("Finishing...");
        System.out.println(this.round_odometer.endRace());
    }

    private double calculateAngle(double x1, double y1, double x2, double y2) {
        double vx = x2 - x1;
        double vy = y2 - y1;

        double angleToTarget = Math.atan2(vx, vy);

        double angle = Utils.normalRelativeAngle(angleToTarget - getHeadingRadians());
        angle = Math.atan(Math.tan(angle));
        angle = angle * (180 / Math.PI);
        System.out.println("Angle: " + angle);
        return angle;
    }

    private void preparePath() {
        while(this.getOthers() != this.objects.size()){
            turnRight(5);
        }
    }


    @Override
    public void onScannedRobot(ScannedRobotEvent event) {
        super.onScannedRobot(event);
        if(scanningObject){
            double enemyBearing = this.getHeading() + event.getBearing();
            // Calculate enemy's position
            double enemyX = getX() + event.getDistance() * Math.sin(Math.toRadians(enemyBearing));
            double enemyY = getY() + event.getDistance() * Math.cos(Math.toRadians(enemyBearing));

            EnemyRobot en = new EnemyRobot(enemyX,enemyY,event.getName(), event.getHeadingRadians());
            if(!this.objects.contains(en))
                this.objects.add(en) ;
        }
    }


    private void getToStartingPosition() {
        while(!(Math.floor(this.getX()) == 18 && Math.floor(this.getY()) == 18)){
            double x = 18 - this.getX();
            double y = 18 - this.getY();
            goTo(x ,y,0);
        }

        double x = 19 - this.getX();
        double y = 19 - this.getY();
        goTo(x ,y,0);

        this.start = true;
    }

    private void goTo(double x, double y, double extra){
        /* Calculate the angle to the target position */
        double angleToTarget = Math.atan2(x, y);

        /* Calculate the turn required get there */
        double targetAngle = Utils.normalRelativeAngle(angleToTarget - getHeadingRadians());

        double distance = Math.hypot(x, y);

        /* This is a simple method of performing set front as back */
        double turnAngle = Math.atan(Math.tan(targetAngle));
        turnRightRadians(turnAngle);
        if(targetAngle == turnAngle) {
            ahead(distance + extra);
        } else {
            back(distance + extra);
        }
        System.out.println("Walking...");
    }

    public void onCustomEvent(CustomEvent ev){
        Condition cd = ev.getCondition();
        if (cd.getName().equals("isMoving")) {
            this.round_odometer.addDistanceRound();
        }
        if(cd.getName().equals("isRacing")){
            this.odometer_default.getRaceDistance();
        }
    }
}
