package Messages;

import Utils.RobotData;

import java.awt.geom.Point2D;
import java.io.Serializable;

public class AttackEnemy implements Serializable {

    public enum Type {Cancel, Accept, Refuse, Ask};

    private Point2D enemy;
    private String name;
    private Type type;
    private RobotData r;

    public AttackEnemy(Point2D enemy, String name, Type type, RobotData r){
        this.enemy = enemy;
        this.name = name;
        this.type = type;
        this.r = r;
    }

    public Point2D getEnemy() {
        return enemy;
    }

    public void setEnemy(Point2D enemy) {
        this.enemy = enemy;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public RobotData getR() {
        return r;
    }

    public void setR(RobotData r) {
        this.r = r;
    }

    @Override
    public String toString() {
        return "AttackEnemy{" +
                "enemy=" + enemy +
                ", name='" + name + '\'' +
                ", type=" + type +
                '}';
    }
}