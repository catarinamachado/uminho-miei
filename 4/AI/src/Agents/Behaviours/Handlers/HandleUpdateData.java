package Agents.Behaviours.Handlers;

import Agents.AgentData;
import Agents.Messages.UpdateData;
import Agents.Station;
import Logic.Metric;

import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

public class HandleUpdateData extends OneShotBehaviour {
    private Station s;
    private ACLMessage msg;

    public HandleUpdateData(Station s, ACLMessage msg) {
        super(s);
        this.s = s;
        this.msg = msg;
    }

    @Override
    public void action() {
        try {
            Metric c = s.getMetrics();
            c.addFuelUsage();
            UpdateData cont = (UpdateData) msg.getContentObject();
            AgentData ag = s.getWorld().getFireman().get(msg.getSender());
            ag.setActual_position(cont.getP());
            ag.setCap_fuel(cont.getFuel());
            ag.setCap_water(cont.getWater());
            ag.setOcupation(cont.getOcupation());
        } catch (UnreadableException e) {
            e.printStackTrace();
        }
    }
}
