package Messages;

import java.io.Serializable;

public class DeathOfRobot implements Serializable {
    private String name;

    public DeathOfRobot(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "DeathOfRobot{" +
                "name='" + name + '\'' +
                '}';
    }
}
