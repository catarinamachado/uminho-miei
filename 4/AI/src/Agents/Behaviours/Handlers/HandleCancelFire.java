package Agents.Behaviours.Handlers;

import Agents.Fireman;
import Agents.Messages.CancelFire;
import Logic.Fire;
import Util.Ocupation;

import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

public class HandleCancelFire extends OneShotBehaviour {
    private Fireman f;
    private ACLMessage msg;

    public HandleCancelFire(Fireman f, ACLMessage msg) {
        super(f);
        this.f = f;
        this.msg = msg;
    }

    @Override
    public void action() {
        try {
            Fire fire = ((CancelFire) msg.getContentObject()).getFire();
            if(f.getTreating_fire() != null && f.getTreating_fire().equals(fire)) {
                f.setTreating_fire(null);
                f.setOcupation(Ocupation.RETURNING);
                f.setDestiny(f.getStd_position());
            }
        } catch (UnreadableException e) {
            e.printStackTrace();
        }
    }
}
