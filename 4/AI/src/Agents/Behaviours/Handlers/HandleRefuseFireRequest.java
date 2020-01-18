package Agents.Behaviours.Handlers;

import Agents.Messages.ExtinguishFireData;
import Agents.Station;
import Logic.Fire;

import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class HandleRefuseFireRequest extends OneShotBehaviour {
    private Station s;
    private ACLMessage msg;

    public HandleRefuseFireRequest(Station s, ACLMessage msg) {
        super(s);
        this.s = s;
        this.msg = msg;
    }

    @Override
    public void action() {
        try {
            ExtinguishFireData cont = (ExtinguishFireData) msg.getContentObject();
            Fire f = cont.getFire();
            Map<Fire, List<AID>> questioning = s.getQuestioning();
            List<AID> unavailable = questioning.get(f);
            System.out.println("Someone refused, searching another one...");
            AID agent = s.findBestFireman(f,unavailable);

            ACLMessage res = new ACLMessage(ACLMessage.REQUEST);
            res.setContentObject(cont);
            res.addReceiver(agent);
            this.myAgent.send(res);

            unavailable.add(agent);
        } catch (UnreadableException | IOException e) {
            e.printStackTrace();
        }
    }
}
