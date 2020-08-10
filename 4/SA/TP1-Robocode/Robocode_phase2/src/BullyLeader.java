import Messages.DeathOfRobot;
import Messages.EnemyStatus;
import Utils.ACTION;
import Messages.AttackEnemy;
import Messages.AttackingEnemy;
import Utils.AvoidWall;
import robocode.*;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import Utils.RobotData;
import Utils.Utility;

public class BullyLeader extends TeamRobot{
    private static int SIZE = 36;
    private Map<String, RobotData> bots = new HashMap();
    private ACTION action = ACTION.THINKING;

    private List<String> refused = new ArrayList<>();
    private String attacking = "";
    private String questioning = "";
    private String waiting_awnser = "";
    private String buddy = "";
    private byte radarDirection = 1;
    private boolean rogue = false;
    private boolean tmp_leader = false;


    public void run() {
        this.setColors(Color.red,Color.red,Color.GREEN,Color.red,Color.red);
        this.setAdjustRadarForGunTurn(true);
        //this.addCustomEvent(new AvoidWall("avoid_walls",this));
        System.out.println("Logging...");

        while (true) {
            if(!rogue) {
                switch (this.action) {
                    case THINKING:
                        turnRadarRight(360);
                        if (this.questioning.equals("")) {
                            Point2D me = new Point2D.Double(this.getX(), this.getY());
                            String tmp = Utility.getClosest(me, this.bots, RobotData.TYPE.Enemy);
                            if (!tmp.equals("")) {
                                this.questioning = tmp;
                            } else
                                break;
                        }
                        this.chooseBuddy(this.questioning);
                        break;
                    case ATTACK:
                        doScan();
                        doMoviment();
                        break;
                }
            }
            else{
                if(this.attacking.equals("")) {
                    List<RobotData> ens = this.bots.values().stream().filter(r -> r.getType() == RobotData.TYPE.Enemy).collect(Collectors.toList());
                    RobotData en = null;
                    for(RobotData t : ens) {
                        en = t;
                        break;
                    }
                    if (en != null) {
                        this.attacking = en.getName();
                        doScan();
                        doMoviment();
                    }
                    else{
                        setTurnRadarRight(360);
                    }
                }
                else{
                    doScan();
                    doMoviment();
                }
            }
            this.execute();
        }
    }

    private void doScan(){
        double turn = getHeading() - getRadarHeading() + this.bots.get(this.attacking).getBearing();
        turn += 30 * radarDirection;
        setTurnRadarRight(turn);
        radarDirection *= -1;
    }

    private void doMoviment(){
        RobotData en = this.bots.get(this.attacking);

        if(en.getOwner().equals(this.getName())){
            setTurnRight(en.getBearing());
            setAhead(en.getDistance());
        }
        else{
            double x = en.getPos().getX() - this.getX();
            double y = en.getPos().getY() - this.getY();

            double a = Math.atan2(x,y) - getHeadingRadians();
            double angle = Math.tan(a);
            setTurnRightRadians(angle);
            setAhead(Math.hypot(x, y) * Math.cos(a));
        }
    }

    private void chooseBuddy(String target){
        System.out.println(target);
        if(!this.waiting_awnser.equals("")) {
            doNothing();
            return;
        }
        Point2D me = new Point2D.Double(this.getX(),this.getY());
        Map<String, RobotData> tmp = new HashMap<String, RobotData>(this.bots);

        for (String s : this.refused)
            tmp.remove(s);

        tmp.entrySet().removeIf(entry -> !entry.getKey().contains("BullySlave"));

        if(tmp.isEmpty())
            return;
        String buddy = Utility.getClosest(me, tmp, RobotData.TYPE.Friendly);
        if(action == ACTION.ATTACK)
            return;
        this.waiting_awnser = buddy;
        if(!buddy.equals(""))   {
            try {
                System.out.println("Sending message to " + buddy);
                this.sendMessage(buddy,new AttackEnemy(this.bots.get(target).getPos(),target, AttackEnemy.Type.Ask, this.bots.get(target)));
                System.out.println("Message sent!");
            } catch (IOException | NullPointerException e) {
                e.printStackTrace();
            }
        }
        else
            this.refused = new ArrayList<>();
    }

    @Override
    public void onMessageReceived(MessageEvent event) {
        Object msg = event.getMessage();
        try {
            if (msg instanceof AttackEnemy) {
                AttackEnemy cont = (AttackEnemy) msg;
                System.out.println(cont.toString());
                if (this.action == ACTION.THINKING) {
                    switch (cont.getType()) {
                        case Accept:
                            System.out.println("Received: " + cont.toString());
                            this.action = ACTION.ATTACK;
                            this.broadcastMessage(new AttackingEnemy(cont.getEnemy(),cont.getName()));
                            this.waiting_awnser = "";
                            this.attacking = cont.getName();
                            this.questioning = "";
                            this.buddy = event.getSender();
                            System.out.println("Cleaned: " + waiting_awnser);
                            break;
                        case Refuse:
                            System.out.println("Received: " + cont.toString());
                            this.waiting_awnser = "";
                            this.refused.add(cont.getName());
                            break;
                    }
                }
            }
            else if(msg instanceof DeathOfRobot) {
                DeathOfRobot cont = (DeathOfRobot) msg;
                System.out.println("RobotDeath: " + cont.toString());
                if (this.attacking.equals(cont.getName())) {
                    this.action = ACTION.THINKING;
                    this.buddy = "";
                    this.attacking = "";
                }
                this.bots.remove(cont.getName());
                if (this.bots.values().stream().noneMatch(r -> r.getName().contains("BullySlave"))) {
                    this.rogue = true;
                    System.out.println("Going rogue.");
                }
                if (this.bots.values().stream().filter(r -> r.getName().contains("JBourne") && r.getName().contains("BullyLeader")).count() == 0) {
                    this.tmp_leader = true;
                }
            }
            else if(msg instanceof EnemyStatus){
                EnemyStatus cont = (EnemyStatus) msg;
                RobotData en1 = cont.getEnemy();
                RobotData en2 = this.bots.get(en1.getName());
                if(en2 != null && en2.getTimestamp() < en1.getTimestamp()){
                    System.out.println("Updated!");
                    this.bots.put(en1.getName(),en1);
                }
                else if(en2 == null)
                    this.bots.put(en1.getName(),en1);
                }
            else
                System.out.println("Don't care about this message: " + msg.toString());
        }
        catch (IOException e){
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void onBulletHit(BulletHitEvent event) {
        RobotData r = this.bots.get(event.getName());
        if (r != null) {
            r.setEnergy(event.getEnergy());
            this.bots.put(r.getName(), r);
            System.out.println("I just hit " + event.getName() + ", staying with " + event.getEnergy() + " energy left!");
        }
    }

    @Override
    public void onScannedRobot(ScannedRobotEvent event) {
        if(event.getName().equals(this.attacking)){
            if (Math.abs(getTurnRemaining()) < 10)
                if(event.getDistance() < 73) {
                    double power = (double) (-1/18)*event.getDistance() + 5;
                    System.out.println("Focusing " + attacking + " with " + buddy + ": " + power + " power.");
                    setFire(power);
                }
        }

        double bearing = event.getBearing();
        // Calculate enemy's position
        double x = getX() + event.getDistance() * Math.sin(Math.toRadians(this.getHeading() + event.getBearing()));
        double y = getY() + event.getDistance() * Math.cos(Math.toRadians(this.getHeading() + event.getBearing()));
        double energy = event.getEnergy();
        double vel = event.getVelocity();
        double heading = event.getHeading();
        double timestamp = System.currentTimeMillis();

        RobotData r = new RobotData(
                new Point2D.Double(x,y),
                energy,
                vel,
                bearing,
                RobotData.TYPE.Friendly,
                event.getName(),
                timestamp,
                heading,
                event.getDistance(),
                this.getName()
        );
        if(!this.isTeammate(event.getName()))
            r.setType(RobotData.TYPE.Enemy);
        this.bots.put(event.getName(),r);
        if(this.tmp_leader) {
            try {
                this.sendMessage("Hulk", new EnemyStatus(r));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRobotDeath(RobotDeathEvent event) {
        if(this.attacking.equals(event.getName())){
            this.action = ACTION.THINKING;
            this.buddy = "";
            this.attacking = "";
            this.questioning = "";
        }
        try {
            broadcastMessage(new DeathOfRobot(event.getName()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.bots.remove(event.getName());
        if(this.bots.values().stream().noneMatch(r -> r.getName().contains("BullySlave"))){
            this.rogue = true;
            System.out.println("Going rogue.");
        }
        if(this.bots.values().stream().filter(r -> r.getName().contains("JBourne")).count() == 0){
            this.tmp_leader = true;
        }
    }

    @Override
    public void onWin(WinEvent event) {
        while(true){
            this.turnRight(25);
            fire(1);
            this.turnLeft(25);
        }
    }

    @Override
    public void onHitWall(HitWallEvent e) {
        double bearing = e.getBearing();
        turnRight(-bearing);
    }

    @Override
    public void onHitRobot(HitRobotEvent e){
        System.out.println("Just body hit " + e.getName());
        RobotData r = this.bots.get(e.getName());
        if(r != null)
            r.setPos(new Point2D.Double(this.getX(),this.getY()));

        RobotData en = this.bots.get(this.attacking);
        if(en != null) {
            if (en.getDistance() < 100) {
                if (!e.getName().equals(this.attacking)) {
                    this.turnRight(45);
                    back(5);
                }
            } else {
                if (!e.getName().equals(this.attacking)) {
                    this.turnRight(45);
                    back(36);
                }
            }
        }
    }

    @Override
    public void onHitByBullet(HitByBulletEvent event) {}
}
