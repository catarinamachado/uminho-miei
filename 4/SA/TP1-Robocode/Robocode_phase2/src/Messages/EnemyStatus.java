package Messages;

import Utils.RobotData;

import java.io.Serializable;

public class EnemyStatus implements Serializable {
    private RobotData enemy;

    public EnemyStatus(RobotData enemy) {
        this.enemy = enemy;
    }

    public RobotData getEnemy() {
        return enemy;
    }

    public void setEnemy(RobotData enemy) {
        this.enemy = enemy;
    }

    @Override
    public String toString() {
        return "EnemyStatus{" +
                "enemy=" + enemy +
                '}';
    }
}