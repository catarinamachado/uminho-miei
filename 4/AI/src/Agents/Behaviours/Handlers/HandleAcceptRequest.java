package Agents.Behaviours.Handlers;

import Agents.AgentData;
import Agents.Messages.ExtinguishFireData;
import Agents.Station;
import Logic.Fire;
import Logic.Metric;
import Util.Ocupation;

import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

import java.util.Map;

public class HandleAcceptRequest extends OneShotBehaviour {
    private Station s;
    private ACLMessage msg;
    public HandleAcceptRequest(Station s, ACLMessage msg) {
        super(s);
        this.s = s;
        this.msg = msg;
    }

    @Override
    public void action() {
        try {
            Metric c = s.getMetrics();
            ExtinguishFireData cont = (ExtinguishFireData) msg.getContentObject();
            c.addNewFireAssigned(cont.getFire());
            Map<AID, Fire> treatment_fire = s.getTreatment_fire();
            treatment_fire.put(msg.getSender(), cont.getFire());
            AgentData ag = s.getWorld().getFireman().get(msg.getSender());
            switch (ag.getFiremanType()) {
                case AIRCRAFT:
                    c.addAircraftsUsage();
                    break;
                case DRONE:
                    c.addDroneUsage();
                    break;
                case FIRETRUCK:
                    c.addTrucksUsage();
                    break;
            }
            ag.setOcupation(Ocupation.MOVING);
            ag.setTreating_fire(cont.getFire());

            s.getQuestioning().remove(cont.getFire());
        } catch (UnreadableException e) {
            e.printStackTrace();
        }
    }
}
