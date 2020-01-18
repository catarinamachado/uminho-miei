package Logic;

import Util.Position;
import Util.Risk;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Fire implements Serializable {
    private String id;
    private List<Position> positions;
    private int zone_id;
    private Risk risk;
    private int duration_time;
    private double base_expansion_rate;
    private int being_resolved_time;

    public Fire(List<Position> positions, double base_expansion_rate, int zone_id) {
        this.id = UUID.randomUUID().toString();
        this.zone_id = zone_id;
        this.positions = positions;
        this.risk = Risk.LOW;
        this.duration_time = 0;
        this.base_expansion_rate = base_expansion_rate;
        this.being_resolved_time = 0;
    }

    public boolean equals(Object object) {
        if (this == object)
            return true;

        if (object == null || (this.getClass() != object.getClass()))
            return false;

        Fire fire = (Fire) object;
        return this.id.equals(fire.getId());
    }

    public int getZone_id() {
        return zone_id;
    }

    public void setZone_id(int zone_id) {
        this.zone_id = zone_id;
    }

    public List<Position> getPositions() {
        return positions;
    }

    public Risk getRisk() {
        return risk;
    }

    public int getDuration_time() {
        return duration_time;
    }

    public double getBase_expansion_rate() {
        return base_expansion_rate;
    }

    public void setPositions(List<Position> positions) {
        this.positions = positions;
    }

    public void setRisk(Risk risk) {
        this.risk = risk;
    }

    public void setDuration_time(int duration_time) {
        this.duration_time = duration_time;
    }

    public void setBase_expansion_rate(double base_expansion_rate) {
        this.base_expansion_rate = base_expansion_rate;
    }

    public int getBeing_resolved_time() {
        return being_resolved_time;
    }

    public void setBeing_resolved_time(int being_resolved_time) {
        this.being_resolved_time = being_resolved_time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Fire{" +
                "positions=" + positions +
                ", risk=" + risk +
                ", duration_time=" + duration_time +
                ", base_expansion_rate=" + base_expansion_rate +
                ", being_resolved_time=" + being_resolved_time +
                ", zone_id=" + zone_id +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(positions, risk, duration_time, base_expansion_rate);
    }

    public void increaseTime() {
        this.duration_time++;
    }

    public void increaseTimeBeingResolved(){
        this.being_resolved_time++;
    }
}