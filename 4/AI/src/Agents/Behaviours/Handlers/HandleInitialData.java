package Agents.Behaviours.Handlers;

import Agents.Fireman;
import Agents.Messages.InitialData;

import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

public class HandleInitialData extends OneShotBehaviour {
    private Fireman f;
    private ACLMessage msg;

    public HandleInitialData(Fireman f, ACLMessage msg) {
        super(f);
        this.f = f;
        this.msg = msg;
    }

    @Override
    public void action() {
        try {
            InitialData cont = (InitialData) msg.getContentObject();
            f.setActual_position(cont.getPos());
            f.setStd_position(cont.getPos());
            f.setStation(msg.getSender());
            System.out.println("[FIREMAN " + f.getName() + "] Posição: " + f.getActual_position().toString());
        } catch (UnreadableException e) {
            e.printStackTrace();
        }
    }
}
