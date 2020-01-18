package Agents.Messages;

import Logic.Fire;

import java.io.Serializable;

public class CancelFire implements Serializable {
    Fire fire;

    public CancelFire(Fire f){
        this.fire = f;
    }

    public Fire getFire() {
        return fire;
    }
}
