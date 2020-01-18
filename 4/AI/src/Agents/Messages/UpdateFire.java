package Agents.Messages;

import Logic.Fire;
import java.io.Serializable;


public class UpdateFire implements Serializable {
    private Fire fire;
    private boolean type;

    public UpdateFire(Fire f, boolean b) {
        this.fire = f;
        this.type = b;
    }

    public Fire getFire() {
        return fire;
    }

    public void setFire(Fire fire) {
        this.fire = fire;
    }

    public boolean isType() {
        return type;
    }

    public void setType(boolean type) {
        this.type = type;
    }
}
