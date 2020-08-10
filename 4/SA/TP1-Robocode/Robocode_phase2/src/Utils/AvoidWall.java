package Utils;

import robocode.Condition;
import robocode.Robot;

public class AvoidWall extends Condition {
    private static int margin = 36;
    private Robot r;

    public AvoidWall(String name, Robot r) {
        super(name);
        this.r = r;
    }

    @Override
    public boolean test() {
        return (
                // we're too close to the left wall
                (r.getX() <= margin ||
                        // or we're too close to the right wall
                        r.getX() >= r.getBattleFieldWidth() - margin ||
                        // or we're too close to the bottom wall
                        r.getY() <= margin ||
                        // or we're too close to the top wall
                        r.getY() >= r.getBattleFieldHeight() - margin)
        );
    }
}
