package Agents;

import Agents.Behaviours.*;
import Agents.Messages.UpdateFire;
import Logic.Fire;
import Logic.Metric;
import Logic.World;
import Logic.Zone;
import Util.BestFireManComparator;
import Util.Position;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Station extends Agent {
    private World world;
    private Map<AID, Fire> treatment_fire;
    private List<Fire> waiting_fire;
    private Map<Fire,List<AID>> questioning;
    private GUI.MapType map_gui;
    private GUI.AgentState agent_gui;
    private Metric metrics;

    public void setup() {
        super.setup();
        Object[] args = getArguments();
        this.world = (World) args[0];
        this.treatment_fire = new HashMap<>();
        this.waiting_fire = new ArrayList<>();
        this.metrics = new Metric();
        questioning = new HashMap<>();
        if((boolean) args[1])
            this.map_gui = new GUI.Map2(world, this);
        else
            this.map_gui = new GUI.Map(world, this);
        this.agent_gui = new GUI.AgentState(world, this);

        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setName(getLocalName());
        sd.setType("Station");
        dfd.addServices(sd);

        try {
            DFService.register(this, dfd);
        } catch (FIPAException e) {
            e.printStackTrace();
        }

        this.addBehaviour(new SendInitialInfo(this.world));
        this.addBehaviour(new HandleStationMessages());
        this.addBehaviour(new CheckWaitingFires());
        this.addBehaviour(new MetricController(this, 5000));
        this.addBehaviour(new CalculateRisk(this));
        this.addBehaviour(new UpdateOcupationRate(this, 1000));
        this.addBehaviour(new TickerBehaviour(this,500) {
            @Override
            protected void onTick() {
                map_gui.update(world, (Station) this.myAgent);
                map_gui.updateGUI();
                agent_gui.update(world, (Station)this.myAgent);
                agent_gui.updateGUI();
            }
        });
        this.addBehaviour(new TickerBehaviour(this,1000) {
            @Override
            protected void onTick() {
                expandFire(waiting_fire);
                expandFire(treatment_fire.values());
                expandFire(questioning.keySet());
                treatment_fire.values().forEach(Fire::increaseTime);
                treatment_fire.values().forEach(Fire::increaseTimeBeingResolved);
                waiting_fire.forEach(Fire::increaseTime);
                questioning.keySet().forEach(Fire::increaseTime);
                System.out.println("-------Fires being treated-------");
                treatment_fire.keySet().forEach(a -> System.out.println("Fire " + treatment_fire.get(a).getPositions()
                .get(0).toString() + " by " + a.getName()));
                System.out.println("-------Fires waiting to be treated-------");
                waiting_fire.forEach(f -> System.out.println(f.toString()));
                System.out.println("-------Fires being questioned-------");
                //for(Fire f : questioning.keySet()){
                //    System.out.println(f.toString() + " " + questioning.get(f).size());
                //}
                //System.out.println("-------Zone Status----------");
                //world.getZones().forEach(z -> System.out.println("Zone " + z.getId() + ": " + z.getOcupation_rate()));
            }
        });
    }

    public void takeDown(){

    }

    public World getWorld() {
        return world;
    }

    public Map<AID,Fire> getTreatment_fire() {
        return treatment_fire;
    }

    public List<Fire> getWaiting_fire() {
        return waiting_fire;
    }

    public Map<Fire, List<AID>> getQuestioning() {
        return questioning;
    }

    public Metric getMetrics() {
        return metrics;
    }

    public AID findBestFireman(Fire f, List<AID> unavailable){
        List<Position> p = f.getPositions();
        int size_of_fire = p.size();
        Zone z = this.world.findZoneOfFire(f);
        int fire_x = p.stream().map(Position::getX).reduce(0, Integer::sum) / size_of_fire;
        int fire_y = p.stream().map(Position::getY).reduce(0, Integer::sum) / size_of_fire;

        List<AgentData> firemans = this.world.getFireman().values().stream().filter(b -> b.getZone().getId() == z.getId()).collect(Collectors.toList());
        for(AID d : unavailable){
            firemans.removeIf(a -> a.getAid().equals(d));
        }
        List<AgentData> bad_firemans = firemans.stream().filter(c -> c.getCap_max_water() < size_of_fire).collect(Collectors.toList());
        firemans = firemans.stream().filter(c -> c.getCap_max_water() >= size_of_fire).collect(Collectors.toList());
        if(firemans.size() == 0){
            if(bad_firemans.size() == 0){
                ArrayList<Zone> t = new ArrayList<>(world.getZones());
                t.sort((c1,c2) -> (int)(c1.getOcupation_rate() - c2.getOcupation_rate()));
                Zone n = t.get(0);
                List<AgentData> remaining = this.world.getFireman().values().stream().filter(b -> b.getZone().getId() == n.getId()).collect(Collectors.toList());
                remaining.sort(new BestFireManComparator(fire_x,fire_y));
                return remaining.get(0).getAid();
            }
            else {
                bad_firemans.sort(new BestFireManComparator(fire_x,fire_y));
                return bad_firemans.get(0).getAid();
            }
        }
        else{
            firemans.sort(new BestFireManComparator(fire_x,fire_y));
            return firemans.get(0).getAid();
        }
    }

    private void expandFire(Collection<Fire> fires){
        for(Fire f: fires){
            if(((new Random()).nextInt(10)+1) > 7){
                int randx = (new Random().nextInt(3))-1; //gera int entre -1 e 1
                int randy = (new Random().nextInt(3))-1; // same
                if(randx!= 0 || randy!= 0){
                    int x = f.getPositions().get(f.getPositions().size()-1).getX()+randx;
                    int y = f.getPositions().get(f.getPositions().size()-1).getY()+randy;
                    Position p = new Position(x,y);
                    if(p.isValid(world.getFire(), world.getFuel(), world.getWater(), world.getHouses(),
                            new ArrayList<>(world.getFireman().values()))){
                        try {
                            for(Fire fire: world.getFire()){
                                if(fire.equals(f)){
                                    fire.getPositions().add(p);
                                }
                            }
                            f.getPositions().add(p);
                            UpdateFire co = new UpdateFire(f, true);
                            ACLMessage message = new ACLMessage(ACLMessage.INFORM);
                            message.setContentObject(co);
                            for (AID ag : world.getFireman().keySet()) {
                                message.addReceiver(ag);
                            }
                            send(message);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
}