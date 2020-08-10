import Messages.AttackEnemy;
import Messages.AttackingEnemy;
import Messages.EnemyStatus;
import Messages.TakeAction;
import Utils.*;
import robocode.*;
import robocode.RobotDeathEvent;
import robocode.ScannedRobotEvent;
import robocode.TeamRobot;
import java.awt.*;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.*;
import java.util.List;

import static Utils.Utility.absoluteBearing;
import static Utils.Utility.normalizeBearing;
import static robocode.util.Utils.normalRelativeAngleDegrees;

public class JBourne extends TeamRobot {

    private RobotData enemy = new RobotData();
    private Map<String, RobotData> bots = new HashMap<>();
    private List<String> not_hit = new ArrayList<>();
    private ACTION hulk_mode = ACTION.HOLD;
    private String hulk_target = "";

    public void run() {
        this.setColors(Color.black,Color.black,Color.black,Color.black,Color.black);
        setAdjustRadarForGunTurn(true);
        setAdjustGunForRobotTurn(true);
        enemy.reset();
        setTurnRadarRight(360);

        while (true) {
            System.out.println(enemy.getName());
            setTurnRadarRight(360);
            move();
            fireStuff();
            execute();
        }
    }

    @Override
    public void onMessageReceived(MessageEvent event) {
        Object msg = event.getMessage();
        if(msg instanceof EnemyStatus){
            EnemyStatus cont = (EnemyStatus) msg;
            RobotData r = this.bots.get(cont.getEnemy().getName());
            if(r != null){
                if(r.getTimestamp() < cont.getEnemy().getTimestamp())
                    this.bots.put(r.getName(),cont.getEnemy());
            }
            else
                this.bots.put(cont.getEnemy().getName(),cont.getEnemy());
        }
        else if(msg instanceof AttackingEnemy){
            AttackingEnemy cont = (AttackingEnemy) msg;
            if(!this.not_hit.contains(cont.getName()))
                this.not_hit.add(cont.getName());
        }
    }

    public void onScannedRobot(ScannedRobotEvent e) {
        double bearing = e.getBearing();
        // Calculate enemy's position
        double x = getX() + e.getDistance() * Math.sin(Math.toRadians(this.getHeading() + e.getBearing()));
        double y = getY() + e.getDistance() * Math.cos(Math.toRadians(this.getHeading() + e.getBearing()));
        double energy = e.getEnergy();
        double vel = e.getVelocity();
        double angle = e.getHeading();
        double timestamp = System.currentTimeMillis();

        RobotData r = new RobotData(
                new Point2D.Double(x,y),
                energy,
                vel,
                bearing,
                RobotData.TYPE.Friendly,
                e.getName(),
                timestamp,
                angle,
                e.getDistance(),
                this.getName()
        );
        if(!this.isTeammate(e.getName()))
            r.setType(RobotData.TYPE.Enemy);

        this.bots.put(r.getName(),r);
        try {
            System.out.println("Updated Hulk: " + e.getName());
            sendMessage("Hulk", new EnemyStatus(r));
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        // track if we have no enemy, the one we found is significantly
        // closer, or we scanned the one we've been tracking.
        if(e.getName().equals(enemy.getName()) || enemy.none() && !isTeammate(e.getName()) && this.not_hit.contains(e.getName()))
            enemy.update(e, this);

        if (!isTeammate(e.getName())) {
            if(!this.not_hit.contains(e.getName())) {
                if (hulk_mode == ACTION.ATTACK){
                    System.out.println("Hulk already attacking");
                    if (this.enemy.getName().equals("")) {
                        System.out.println("My new enemy: " + e.getName());
                        this.not_hit.add(e.getName());
                        enemy.update(e, this);
                    }
                }
                else {
                    this.hulk_mode = ACTION.ATTACK;
                    this.hulk_target = e.getName();
                    this.not_hit.add(e.getName());
                    try {
                        sendMessage("Hulk",
                                new TakeAction(ACTION.ATTACK, new Point2D.Double(enemy.getFutureX(18),
                                        enemy.getFutureY(18)))
                        );
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }

    public void onRobotDeath(RobotDeathEvent e) {
        // see if the robot we were tracking died
        if (e.getName().equals(enemy.getName())) {
            enemy.reset();
        }
        this.not_hit.removeIf(r -> r.equals(e.getName()));
        this.bots.remove(e.getName());
        if(hulk_target.equals(e.getName())){
            this.hulk_target = "";
            this.hulk_mode = ACTION.HOLD;
        }
    }

    void fireStuff() {
        if (enemy.none())
            return;

        double firePower = Math.min(500 / enemy.getDistance(), 3);
        double bulletSpeed = 20 - firePower * 3;
        long time = (long)(enemy.getDistance() / bulletSpeed);

        // calculate gun turn to predicted x,y location
        double futureX = enemy.getFutureX(time);
        double futureY = enemy.getFutureY(time);
        double absDeg = absoluteBearing(getX(), getY(), futureX, futureY);

        // turn the gun to the predicted x,y location
        setTurnGunRight(normalizeBearing(absDeg - (getGunHeading())));

        // if the gun is cool and we're pointed in the right direction, shoot!
        if (getGunHeat() == 0 && Math.abs(getGunTurnRemaining()) < 10 && firePower > 2.3) {
            setFire(firePower);
        }
    }

    void move() {
        setTurnRight(enemy.getBearing() + 45);
        // move a little closer
        if (enemy.getDistance() > 200)
            setAhead(enemy.getDistance() / 3);
        // but not too close
        if (enemy.getDistance() < 100)
            setBack(enemy.getDistance());
    }

    public void onHitWall(HitWallEvent e) {
        double bearing = e.getBearing();
        setTurnRight(-bearing);
    }

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
}
