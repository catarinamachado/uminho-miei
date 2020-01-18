package Agents.Messages;

import Logic.Fire;

import java.io.Serializable;

public class ExtinguishFireData implements Serializable {
    private Fire fire;

    public ExtinguishFireData(Fire fire) {
        this.fire = fire;
    }

    public Fire getFire() {
        return fire;
    }

    public void setFire(Fire fire) {
        this.fire = fire;
    }
}
