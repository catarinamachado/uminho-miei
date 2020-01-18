package Logic;

public class Metric {
    private int time_to_handle_fire;
    private int n_fires_to_handle;
    private int time_to_assign_fire;
    private int n_fires_to_assign;
    private int drones_usage;
    private int trucks_usage;
    private int aircrafts_usage;
    private int fuel_usage;

    public Metric() {
        this.time_to_assign_fire = 0;
        this.n_fires_to_assign = 0;
        this.n_fires_to_handle = 0;
        this.time_to_handle_fire = 0;
        this.drones_usage = 0;
        this.trucks_usage = 0;
        this.aircrafts_usage = 0;
        this.fuel_usage = 0;
    }

    public void addNewFireAssigned(Fire f){
        this.time_to_assign_fire += f.getDuration_time();
        this.n_fires_to_assign++;
    }

    public void addNewFireResolved(Fire f){
        this.time_to_handle_fire = f.getDuration_time() - f.getBeing_resolved_time();
        this.n_fires_to_handle++;
    }

    public void addDroneUsage(){
        this.drones_usage++;
    }

    public void addTrucksUsage(){
        this.trucks_usage++;
    }

    public void addAircraftsUsage(){
        this.aircrafts_usage++;
    }

    public void addFuelUsage(){
        this.fuel_usage++;
    }

    public int getDrones_usage() {
        return drones_usage;
    }

    public int getTrucks_usage() {
        return trucks_usage;
    }

    public int getAircrafts_usage() {
        return aircrafts_usage;
    }

    public int getFuel_usage() {
        return fuel_usage;
    }

    public float getTimeToAssignFire(){
        float time;
        try{
            time = this.time_to_assign_fire / this.n_fires_to_assign;
        }
        catch (ArithmeticException e){
            time = 0;
        }
        return time;
    }

    public float getTimeToHandleFire(){
        float time;
        try{
            time = this.time_to_handle_fire / this.n_fires_to_handle;
        }
        catch (ArithmeticException e){
            time = 0;
        }
        return time;
    }
}
