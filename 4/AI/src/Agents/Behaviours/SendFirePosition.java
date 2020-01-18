package Agents.Behaviours;

import Agents.Messages.StartedFire;
import Logic.Fire;

import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import java.io.IOException;

public class SendFirePosition extends OneShotBehaviour {
    private Fire fire;

    public SendFirePosition(Fire firePos) {
        this.fire = firePos;
    }

    @Override
    public void action() {
        DFAgentDescription template1 = new DFAgentDescription();
        ServiceDescription sd1 = new ServiceDescription();
        sd1.setType("Station");
        template1.addServices(sd1);

        DFAgentDescription[] station;
        try{
            station = DFService.search(myAgent,template1);
            ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
            msg.setContentObject(new StartedFire(fire));
            msg.addReceiver(station[0].getName());
            myAgent.send(msg);
        } catch (FIPAException | IOException e) {
            e.printStackTrace();
        }
    }
}
