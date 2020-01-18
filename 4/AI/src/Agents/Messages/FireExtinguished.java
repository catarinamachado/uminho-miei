package Agents.Messages;

import Util.Position;

import java.io.Serializable;

public class FireExtinguished implements Serializable {
    String id;

    public FireExtinguished(String p) {
        this.id = p;
    }

    public String getId() {
        return id;
    }
}
