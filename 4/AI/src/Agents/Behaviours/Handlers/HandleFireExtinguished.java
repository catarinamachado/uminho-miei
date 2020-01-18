package Agents.Behaviours.Handlers;

import Agents.AgentData;
import Agents.Messages.CancelFire;
import Agents.Messages.FireExtinguished;
import Agents.Messages.UpdateFire;
import Agents.Station;
import Logic.Fire;
import Logic.Metric;
import Util.Ocupation;
import Util.Position;

import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HandleFireExtinguished extends OneShotBehaviour {
    private Station s;
    private ACLMessage msg;

    public HandleFireExtinguished(Station s, ACLMessage msg) {
        super(s);
        this.s = s;
        this.msg = msg;
    }

    @Override
    public void action() {
        AID aid_sender = msg.getSender();

        try {
            FireExtinguished fireExtinguished = (FireExtinguished) msg.getContentObject();
            List<Fire> fires = s.getWorld().getFire().stream().filter(a -> a.getId().equals(fireExtinguished.getId()))
                                           .collect(Collectors.toList());
            if(fires.size() > 0) {
                Fire f = fires.get(0);

                if (f != null) {
                    try {
                        Metric c = s.getMetrics();
                        List<Fire> tmp = new ArrayList<>();
                        tmp.addAll(s.getWaiting_fire());
                        tmp.addAll(s.getTreatment_fire().values());
                        tmp.addAll(s.getQuestioning().keySet());
                        for(Fire old : tmp){
                            if(old.equals(f))
                                c.addNewFireResolved(old);
                        }

                        UpdateFire co = new UpdateFire(f, false);
                        ACLMessage message = new ACLMessage(ACLMessage.INFORM);
                        message.setContentObject(co);
                        for (AID ag : s.getWorld().getFireman().keySet()) {
                            message.addReceiver(ag);
                        }
                        this.myAgent.send(message);

                        //eliminar o fire do World
                        s.getWorld().getFire().remove(f);

                        //se o fogo que apagou eram um treating fire
                        List<AID> aids = s.getTreatment_fire().entrySet().stream().filter(v -> v.getValue().equals(f)).
                                map(k -> k.getKey()).collect(Collectors.toList());

                        if(aids.size() > 0) {
                            AID aid_fireman = aids.get(0);

                            if (aid_fireman != null) {
                                AgentData agentData = s.getWorld().getFireman().get(aid_fireman);

                                //eliminar o par agente&fire do treatment_fire
                                s.getTreatment_fire().remove(aid_fireman);
                                //alterar o estado do fireman para "a regressar"
                                agentData.setOcupation(Ocupation.RETURNING);
                                //eliminar treatment fire do fireman
                                agentData.setTreating_fire(null);

                                //se quem apagou não era o atribuído
                                if (!aid_fireman.equals(aid_sender)) {
                                    ACLMessage ms = new ACLMessage(ACLMessage.CANCEL);
                                    ms.addReceiver(aid_fireman);
                                    ms.setContentObject(new CancelFire(f));
                                    this.myAgent.send(ms);
                                    s.getWorld().getFireman().get(aid_sender).setException_fire(null);
                                }
                            }
                        } else {
                            //se está na list waiting_fire
                            s.getWaiting_fire().remove(f);
                            //se é uma key do map questioning
                            s.getQuestioning().remove(f);

                            s.getWorld().getFireman().get(aid_sender).setException_fire(null);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (UnreadableException e) {
            e.printStackTrace();
        }

    }
}
