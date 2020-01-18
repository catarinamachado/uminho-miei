package Agents.Behaviours;

import Agents.AgentData;
import Agents.Station;
import Logic.Fire;
import Logic.World;
import Logic.Zone;

import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;

public class UpdateOcupationRate extends TickerBehaviour {

    public UpdateOcupationRate(Agent a, long period) {
        super(a, period);
    }

    @Override
    protected void onTick() {
        Station s = (Station) myAgent;
        World w = s.getWorld();
        for(Zone z: w.getZones()){
            int fires = 0;
            int firemen = 0;
            for (Fire f: w.getFire()){
                if(f.getZone_id() == z.getId()) {
                    fires++;
                }
            }
            for (AgentData a: w.getFireman().values()){
                if(z.equals(a.getZone())){
                    firemen++;
                }
            }
            float tmp = ((float) fires) / firemen;
            z.setOcupation_rate(tmp);
        }
    }
}
