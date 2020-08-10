import Messages.EnemyStatus;
import Messages.TakeAction;
import Utils.RobotData;
import robocode.*;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static robocode.util.Utils.normalRelativeAngleDegrees;

public class Hulk2 extends TeamRobot implements Droid {
    private Map<String, RobotData> mates = new HashMap<>();
    private int border = 100;
    private boolean rogue = false;
    private byte radarDirection = 1;
    private String attacking = "";

    public void run(){
        this.setColors(Color.GREEN,Color.GREEN,Color.GREEN,Color.GREEN,Color.GREEN);
        this.setAdjustGunForRobotTurn(false);
        while(true) {
            doMoviment();
            doFire();
            this.execute();
        }
    }

    private void doFire(){
        RobotData en = this.mates.get(this.attacking);

        if(en != null){
            if(en.getDistance() < 73) {
                double power = (double) (-1/18)*en.getDistance() + 5;
                setFire(power);
            }
        }
    }

    private void doMoviment(){
        RobotData en = this.mates.get(this.attacking);

        if(en != null) {
            Point2D pos = en.getPos();
            double angle = Utils.Utility.absoluteBearing(getX(), getY(), pos.getX(), pos.getY());

            setTurnLeft(this.getHeading());
            setTurnRight(angle);
            setAhead(en.getDistance());
        }
    }





    @Override
    public void onHitWall(HitWallEvent e) {
        double bearing = e.getBearing();
        turnRight(-bearing);
    }

    @Override
    public void onHitRobot(HitRobotEvent e){
        Random r = new Random();
        int goRandom = r.nextInt(1);
        // attack enemy
        if(!isTeammate(e.getName())) {
            double absoluteBearing = getHeading() + e.getBearing();
            double bearingFromGun = normalRelativeAngleDegrees(absoluteBearing - getGunHeading());
            double bearingFromRadar = normalRelativeAngleDegrees(absoluteBearing - getRadarHeading());
            turnGunRight(bearingFromGun);
            turnRadarRight(bearingFromRadar);
            fire(3);
        }
        //move away randomly
        //he is behind us so set back a bit
        if(e.getBearing() > -90 && e.getBearing() <= 90){
            if(goRandom==1){
                setTurnRight(45);
                back(150);
            }else{
                setTurnLeft(45);
                back(150);
            }
        }else{
            if(goRandom==1){
                setTurnRight(45);
                ahead(150);
            }else{
                setTurnLeft(45);
                ahead(150);
            }
        }
    }

    @Override
    public void onMessageReceived(MessageEvent e) {
        Object msg = e.getMessage();

        // Fire at a point
        if (msg instanceof TakeAction) {
            /*
            TakeAction action = (TakeAction) msg;
            Point2D p = action.getPos();
            // Calculate x and y to target
            double dx = p.getX() - this.getX();
            double dy = p.getY() - this.getY();
            // Calculate angle to target
            double theta = Math.toDegrees(Math.atan2(dx, dy));

            checkFire(theta);
             */
        }
        else if (msg instanceof EnemyStatus) {
            EnemyStatus cont = (EnemyStatus) msg;
            RobotData r = cont.getEnemy();
            this.mates.put(r.getName(),r);
            if(this.attacking.equals(""))
                this.attacking = r.getName();
        }
    }

    private void checkFire(double angleTarget) {
        for (RobotData mate : this.mates.values()) {
            Point2D pos = mate.getPos();
            double dx = pos.getX() - this.getX();
            double dy = pos.getY() - this.getY();
            // Calculate angle to target
            double theta = Math.toDegrees(Math.atan2(dx, dy));

            if (theta == angleTarget) return;
        }

        // Turn gun to target
        turnGunRight(normalRelativeAngleDegrees(angleTarget - getGunHeading()));
        // Fire hard!
        fire(6);
    }

    @Override
    public void onRobotDeath(RobotDeathEvent event) {
        this.mates.remove(event.getName());
        if(event.getName().contains("JBourne")){
            this.rogue = true;
        }
    }
}

