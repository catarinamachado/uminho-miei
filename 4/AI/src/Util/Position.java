package Util;

import Agents.AgentData;
import Logic.Fire;
import static Logic.World.dimension;

import java.io.Serializable;
import java.util.Objects;
import java.util.List;

public class Position implements Serializable {
    private int x;
    private int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public boolean isValid(List<Fire> fire, List<Position> fuel, List<Position> water, List<Position> houses, List<AgentData> fireman){
        return !((fuel.stream().filter(p -> p.getX() == x && p.getY() == y).count() > 0) ||
                (water.stream().filter(p -> p.getX() == x && p.getY() == y).count() > 0) ||
                (houses.stream().filter(p -> p.getX() == x && p.getY() == y).count() > 0) ||
                (fire.stream().filter(f -> f.getPositions().stream().filter(p -> p.getX() == x ||
                        p.getY() == y).count() > 0).count() > 0) ||
                (fireman.stream().filter(f -> f.getActual_position().getX() == x &&
                        f.getActual_position().getY() == y).count() > 0) ||
                x < 0 || x > dimension - 1|| y < 0 || y > dimension - 1);
    }

    public Fire haveNeighborFire(List<Fire> fires, int perimeter){
        return fires.stream().filter(f -> f.getPositions().stream().
                        anyMatch(p -> (p.getX() >= this.x - perimeter && p.getX() <= this.x + perimeter &&
                                       p.getY() >= this.y - perimeter && p.getY() <= this.y + perimeter)))
                    .findFirst().orElse(null);
    }

    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return x == position.x &&
                y == position.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}