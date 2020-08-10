package Messages;

import Utils.ACTION;

import java.awt.geom.Point2D;
import java.io.Serializable;

public class TakeAction implements Serializable {

    private ACTION action;
    private Point2D pos;

    public TakeAction(ACTION action, Point2D pos) {
        this.action = action;
        this.pos = pos;
    }

    public ACTION getAction() {
        return action;
    }

    public void setAction(ACTION action) {
        this.action = action;
    }

    public Point2D getPos() {
        return pos;
    }

    public void setPos(Point2D pos) {
        this.pos = pos;
    }
}
