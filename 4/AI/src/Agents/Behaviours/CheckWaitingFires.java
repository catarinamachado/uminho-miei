package Agents.Behaviours;

import Agents.Messages.ExtinguishFireData;
import Agents.Station;
import Logic.Fire;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CheckWaitingFires extends CyclicBehaviour {
    @Override
    public void action() {
        Station s = (Station) this.myAgent;
        List<Fire> waiting_fire = s.getWaiting_fire();
        if(waiting_fire.size() == 0){
            block();
            return;
        }
        else{
            Fire f = waiting_fire.get(0);
            System.out.println("Searching fireman to handle fire...");
            AID a = s.findBestFireman(f, new ArrayList<>());
            try {
                ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
                msg.setContentObject(new ExtinguishFireData(f));
                msg.addReceiver(a);
                this.myAgent.send(msg);
                System.out.println("[STATION] Asking " + a.getName() + " to handle fire at " + f.getPositions().toString());

                ArrayList<AID> tmp = new ArrayList<>();
                tmp.add(a);
                s.getQuestioning().put(f,tmp);
                waiting_fire.remove(f);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
