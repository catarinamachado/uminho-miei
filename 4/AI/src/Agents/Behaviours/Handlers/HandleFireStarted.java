package Agents.Behaviours.Handlers;

import Agents.Messages.StartedFire;
import Agents.Messages.UpdateFire;
import Agents.Station;

import Logic.Fire;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import java.io.IOException;
import java.util.List;

public class HandleFireStarted extends OneShotBehaviour {
    private Station s;
    private ACLMessage msg;

    public HandleFireStarted(Station s, ACLMessage msg) {
        super(s);
        this.s = s;
        this.msg = msg;
    }

    @Override
    public void action() {
        try {
            StartedFire cont = (StartedFire) msg.getContentObject();
            Fire f = cont.getFire();
            if(s.getQuestioning().containsKey(f)){
                for(Fire tmp : s.getQuestioning().keySet()){
                    if(tmp.equals(f))
                        tmp.setPositions(f.getPositions());
                }
            } else if(s.getTreatment_fire().containsValue(f)){
                for(AID a : s.getTreatment_fire().keySet()){
                    Fire old = s.getTreatment_fire().get(a);
                    if(old.equals(f))
                        old.setPositions(f.getPositions());
                }
            } else {
                if(s.getWaiting_fire().contains(f))
                    for(Fire tmp : s.getWaiting_fire()){
                        if(tmp.equals(f))
                            tmp.setPositions(f.getPositions());
                    }
                else
                    s.getWaiting_fire().add(f);
            }

            System.out.println("[STATION] Novo fogo: " + cont.getFire().toString());

            UpdateFire co = new UpdateFire(cont.getFire(),true);
            ACLMessage message = new ACLMessage(ACLMessage.INFORM);
            message.setContentObject(co);
            for(AID ag : s.getWorld().getFireman().keySet()){
                message.addReceiver(ag);
            }
            this.myAgent.send(message);
        } catch (UnreadableException | IOException e) {
            e.printStackTrace();
        }
    }
}
