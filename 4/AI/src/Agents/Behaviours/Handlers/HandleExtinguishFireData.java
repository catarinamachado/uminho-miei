package Agents.Behaviours.Handlers;

import Agents.Fireman;
import Agents.Messages.ExtinguishFireData;
import Logic.Fire;
import Util.Ocupation;

import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

import java.io.IOException;

public class HandleExtinguishFireData extends OneShotBehaviour {
    private Fireman f;
    private ACLMessage msg;

    public HandleExtinguishFireData(Fireman f, ACLMessage msg) {
        super(f);
        this.f = f;
        this.msg = msg;
    }

    @Override
    public void action() {
        try {
            ExtinguishFireData cont = (ExtinguishFireData) msg.getContentObject();
            ACLMessage res = new ACLMessage(ACLMessage.AGREE);
            res.setContentObject(cont);
            res.addReceiver(msg.getSender());

            if(f.getOcupation() == Ocupation.RESTING || f.getOcupation() == Ocupation.RETURNING){
                res.setPerformative(ACLMessage.AGREE);
                System.out.println("[FIREMAN " + f.getName() + "] Accepted Fire " + cont.getFire().toString());
                this.myAgent.send(res);

                f.setOcupation(Ocupation.MOVING);
                Fire fire = ((ExtinguishFireData) msg.getContentObject()).getFire();
                f.setTreating_fire(fire);
                f.setDestiny(fire.getPositions().get(0));
            }
            else{
                res.setPerformative(ACLMessage.REFUSE);
                System.out.println("[FIREMAN " + f.getName() + "] Refused Fire " + cont.getFire().toString());
                this.myAgent.send(res);
            }
        } catch (UnreadableException | IOException e) {
            e.printStackTrace();
        }
    }
}
