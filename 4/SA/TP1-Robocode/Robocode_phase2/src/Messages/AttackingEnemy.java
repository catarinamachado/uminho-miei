package Messages;

import java.awt.geom.Point2D;
import java.io.Serializable;

public class AttackingEnemy implements Serializable {

    private Point2D enemy;
    private String name;

    public AttackingEnemy(Point2D enemy, String name) {
        this.enemy = enemy;
        this.name = name;
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
}
