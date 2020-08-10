import robocode.AdvancedRobot;
import robocode.Condition;
import robocode.CustomEvent;
import robocode.ScannedRobotEvent;
import robocode.util.Utils;
import standardOdometer.Odometer;

import java.util.ArrayList;

public class SmartRobotV1 extends AdvancedRobot {

    private static int SIZE = 36;
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

        double vx;
        double vy;
        double x;
        double y;
      
        //First obstacle
        EnemyRobot p = this.objects.get(0);
        vx = (p.getX() - SIZE + p.getCompensation()) - this.getX();
        vy = (p.getY() + SIZE) - this.getY();
        goTo(vx,vy, SIZE/3);

        //Second Obstacle
        p = this.objects.get(1);
        x = p.getX() + SIZE;
        y = p.getY() + SIZE + p.getCompensation();
        if(y > this.getY()) {
            double extra = y - this.getY();
            y += extra;
        }
        vx = x - this.getX();
        vy = y - this.getY();
        goTo(vx,vy, SIZE/3);

        //Third Obstacle
        p = this.objects.get(2);
        x = p.getX() + SIZE + p.getCompensation();
        y = p.getY() - SIZE;
        if(x > this.getX()){
            double extra = x - this.getX();
            x += extra;
        }
        vx = x - this.getX();
        vy = y - this.getY();
        goTo(vx,vy, SIZE/3);

        //Final lap
        vx = 18 - this.getX();
        vy = 18 - this.getY();
        goTo(vx,vy,0);

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
            double extra = 0;
            double enemy_angle = Math.abs(event.getHeading());
            if( (enemy_angle >= 43.5 && enemy_angle <= 45.6) || (enemy_angle >= 120 && enemy_angle <= 150) || (enemy_angle >= 210 && enemy_angle <= 240)
                    || (enemy_angle >= 300 && enemy_angle <= 330) ) {
                extra = 7.5;
                System.out.println(event.getName() + " -> Compensation needed: " + enemy_angle);
            }

            EnemyRobot en = new EnemyRobot(enemyX,enemyY,event.getName(), extra);
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
