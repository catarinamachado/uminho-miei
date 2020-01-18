package Agents.Behaviours;

import Agents.Behaviours.Handlers.HandleCancelFire;
import Agents.Behaviours.Handlers.HandleExtinguishFireData;
import Agents.Behaviours.Handlers.HandleInitialData;
import Agents.Behaviours.Handlers.HandleUpdateFire;
import Agents.Messages.CancelFire;
import Agents.Messages.ExtinguishFireData;
import Agents.Messages.InitialData;
import Agents.Fireman;
import Agents.Messages.UpdateFire;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

public class HandleFiremanMessages extends CyclicBehaviour {

    @Override
    public void action() {
        Fireman f = (Fireman) myAgent;
        ACLMessage msg = myAgent.receive();
        if(msg == null){
            block();
            return;
        }
        try{
            Object content = msg.getContentObject();
            switch (msg.getPerformative()){
                case(ACLMessage.INFORM):
                    if(content instanceof InitialData) {
                        f.addBehaviour(new HandleInitialData(f, msg));
                    }
                    else if(content instanceof UpdateFire){
                        f.addBehaviour(new HandleUpdateFire(f,msg));
                    }

                    break;
                case(ACLMessage.REQUEST):
                    if(content instanceof ExtinguishFireData){
                        f.addBehaviour(new HandleExtinguishFireData(f,msg));
                    }
                    break;
                case(ACLMessage.CANCEL):
                    if(content instanceof CancelFire)
                        f.addBehaviour(new HandleCancelFire(f,msg));
                    break;
                default:
                    System.out.println("Wrong message content.");
                    break;
            }
        } catch (UnreadableException e) {
            System.out.println("Error Fireman on msg: " + msg.getPerformative() + " -> " + msg.getContent());
        }
    }
}
