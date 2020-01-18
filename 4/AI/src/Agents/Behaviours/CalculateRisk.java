package Agents.Behaviours;

import Agents.Station;
import Logic.Fire;
import Util.Position;
import Util.Risk;

import jade.core.behaviours.CyclicBehaviour;
import java.util.ArrayList;
import java.util.List;

public class CalculateRisk extends CyclicBehaviour {
    public CalculateRisk(Station station) {
        super(station);
    }

    @Override
    public void action() {
        Station s = (Station) this.myAgent;
        for (Fire f : s.getWaiting_fire()) {
            ArrayList<Position> risk1 = closePositions(f.getPositions(), 1);
            if (s.getWorld().getHouses().stream().filter(h -> risk1.contains(h)).count() > 0) {
                f.setRisk(Risk.HIGH);
            } else {
                ArrayList<Position> risk2 = closePositions(f.getPositions(), 2);
                if (s.getWorld().getHouses().stream().filter(h -> risk2.contains(h)).count() > 0) {
                    f.setRisk(Risk.MEDIUM);
                } else {
                    f.setRisk(Risk.LOW);
                }
            }
        }
    }

    private ArrayList<Position> closePositions(List<Position> positions, int risk) {
        ArrayList<Position> ret = new ArrayList<>();
        for(Position p : positions){
            ArrayList<Position> tmp = new ArrayList<>();
            for(int i = -risk; i < risk; i++){
                for(int j = -risk; j < risk; j++){
                    tmp.add(new Position(p.getX() + i, p.getY() + j));
                }
            }
            for(Position t : tmp){
                if(!ret.contains(t))
                    ret.add(t);
            }
        }
        return ret;
    }
}
