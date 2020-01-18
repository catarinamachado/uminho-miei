package Agents.Messages;

import Logic.Fire;
import Util.Position;

import java.io.Serializable;

public class StartedFire implements Serializable {
    private Fire fire;

    public StartedFire(Fire fire) {
        this.fire = fire;
    }

    public Fire getFire() {
        return fire;
    }
}
