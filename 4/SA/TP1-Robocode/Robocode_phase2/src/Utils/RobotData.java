package Utils;

import robocode.Robot;
import robocode.ScannedRobotEvent;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RobotData extends Robot implements Serializable {

    public enum TYPE {Friendly, Enemy}

    private Point2D pos;
    private double energy;
    private double velocity;
    private double bearing;
    private TYPE type;
    private String name;
    private double timestamp;
    private double heading;
    private double distance;
    private String owner;

    public RobotData(Point2D pos, double energy, double velocity, double bearing, TYPE type, String name, double timestamp, double heading, double distance, String owner) {
        this.pos = pos;
        this.energy = energy;
        this.velocity = velocity;
        this.bearing = bearing;
        this.type = type;
        this.name = name;
        this.timestamp = timestamp;
        this.heading = heading;
        this.distance = distance;
        this.owner = owner;
    }

    public RobotData() {
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public double getHeading() {
        return heading;
    }

    public void setHeading(double heading) {
        this.heading = heading;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public Point2D getPos() {
        return pos;
    }

    public void setPos(Point2D pos) {
        this.pos = pos;
    }

    public double getEnergy() {
        return energy;
    }

    public void setEnergy(double energy) {
        this.energy = energy;
    }

    public double getVelocity() {
        return velocity;
    }

    public void setVelocity(double velocity) {
        this.velocity = velocity;
    }

    public double getBearing() {
        return bearing;
    }

    public void setBearing(double bearing) {
        this.bearing = bearing;
    }

    public TYPE getType() {
        return type;
    }

    public void setType(TYPE type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(double timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "RobotData{" +
                "pos=" + pos +
                ", hp=" + energy +
                ", velocity=" + velocity +
                ", bearing=" + bearing +
                ", type=" + type +
                ", name='" + name + '\'' +
                ", timestamp=" + timestamp +
                ", angle=" + heading +
                ", distance=" + distance +
                ", owner='" + owner + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RobotData robotData = (RobotData) o;
        return type == robotData.type &&
                name.equals(robotData.name);
    }


    public List<Point2D> getVertexPoints(double size){
        double x = this.pos.getX();
        double y = this.pos.getY();
        List<Point2D> pos = new ArrayList<>();
        pos.add(new Point2D.Double(x - size, y - size));
        pos.add(new Point2D.Double(x - size, y));
        pos.add(new Point2D.Double(x - size, y + size));
        pos.add(new Point2D.Double(x, y + size));
        pos.add(new Point2D.Double(x + size, y + size));
        pos.add(new Point2D.Double(x + size, y));
        pos.add(new Point2D.Double(x + size, y - size));
        pos.add(new Point2D.Double(x, y - size));
        return pos;
    }

    public double getFutureX(long when){
        return pos.getX() + Math.sin(Math.toRadians(heading)) * velocity * when;
    }

    public double getFutureY(long when){
        return pos.getY() + Math.cos(Math.toRadians(heading)) * velocity * when;
    }

    public void update(ScannedRobotEvent e, Robot r) {
        bearing = e.getBearing();
        distance = e.getDistance();
        energy = e.getEnergy();
        heading = e.getHeading();
        name = e.getName();
        velocity = e.getVelocity();
        //double x = r.getX() + e.getDistance() * Math.sin(Math.toRadians(this.getHeading() + e.getBearing()));
        //double y = r.getY() + e.getDistance() * Math.cos(Math.toRadians(this.getHeading() + e.getBearing()));
        double absoluteBearing = (r.getHeading() + e.getBearing());
        if (absoluteBearing < 0) absoluteBearing += 360;

        // yes, you use the _sine_ to get the X value because 0 deg is North
        double x = r.getX() + Math.sin(Math.toRadians(absoluteBearing)) * e.getDistance();

        // yes, you use the _cosine_ to get the Y value because 0 deg is North
        double y = r.getY() + Math.cos(Math.toRadians(absoluteBearing)) * e.getDistance();
        pos = new Point2D.Double(x,y);
        owner = r.getName();
    }

    public boolean none() {
        return "".equals(name);
    }

    public void reset(){
        this.pos = new Point2D.Double(0,0);
        this.energy = 0;
        this.velocity = 0;
        this.bearing = 0;
        this.type = TYPE.Enemy;
        this.name = "";
        this.timestamp = 0;
        this.heading = 0;
        this.distance = 0;
        this.owner = "";

        System.out.printf("Reset tracked bot!\n");
    }
}