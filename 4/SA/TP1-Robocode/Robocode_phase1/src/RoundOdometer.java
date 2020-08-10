import robocode.Condition;
import robocode.Robot;

import java.text.DecimalFormat;

public class RoundOdometer extends Condition {
    public Robot r;
    private double distanceRound = 0.0;
    private boolean is_racing;
    private boolean finished;
    private double new_x;
    private double new_y;
    private double old_x;
    private double old_y;

    public RoundOdometer(String name, Robot r) {
        super(name);
        this.r = r;
        this.is_racing = false;
        this.finished = false;
    }

    public boolean test() {
        this.r.setDebugProperty("racing", String.valueOf(this.is_racing));
        this.r.setDebugProperty("round_finished", String.valueOf(this.finished));
        return r.getTime()!=0;
    }

    public void beginRace(){
        old_x = r.getX();
        old_y = r.getY();
        new_x = r.getX();
        new_y = r.getY();
        is_racing = true;
    }

    public void addDistanceRound() {
        if (is_racing) {
            new_x = r.getX();
            new_y = r.getY();
            distanceRound += Math.sqrt(Math.pow(new_x - old_x, 2) + Math.pow(new_y - old_y, 2));
            old_x = new_x;
            old_y = new_y;
            r.setDebugProperty("round_distance", (new DecimalFormat("#.##")).format(distanceRound));
        }
    }

    public String endRace() {
        this.is_racing = false;
        this.finished = true;
        return "Race Distance " + (new DecimalFormat("#.##")).format(distanceRound) + " pixels";
    }
}
