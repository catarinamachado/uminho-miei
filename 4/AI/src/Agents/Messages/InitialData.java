package Agents.Messages;

import Util.Position;

import java.io.Serializable;

public class InitialData implements Serializable {
    private Position pos;

    public InitialData(Position pos) {
        this.pos = pos;
    }

    public Position getPos() {
        return pos;
    }

    public void setPos(Position pos) {
        this.pos = pos;
    }
}
