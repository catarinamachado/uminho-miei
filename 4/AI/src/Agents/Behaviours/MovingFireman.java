package Agents.Behaviours;

import Agents.AgentData;
import Agents.Fireman;
import Agents.Messages.UpdateData;
import Util.*;
import Logic.Fire;

import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MovingFireman extends TickerBehaviour {
    private FindShortestPath fsp = new FindShortestPath();


    public MovingFireman(Agent myagent) {
        super(myagent,500);
    }

    @Override
    protected void onTick() {
        Fireman f = (Fireman) this.myAgent;
        Fire newFire = f.getActual_position().haveNeighborFire(f.getFires(), 2);
        Position next;

        //Se está na posição de um FUEL abastece
        if (f.getFuel().stream().anyMatch(p -> p.equals(f.getActual_position()))
                && (f.getCap_fuel() != f.getCap_max_fuel())) {
            //System.out.println("Abasteci fuel!\n");
            f.setCap_fuel(f.getCap_max_fuel());
        }

        //Se está na posição de um WATER abastece
        else if (f.getWater().stream().anyMatch(p -> p.equals(f.getActual_position()))
                && (f.getCap_water() != f.getCap_max_water())) {
            //System.out.println("Abasteci água!\n");
            f.setCap_water(f.getCap_max_water());
        }

        //Se está na posição standard repousa
        else if (f.getActual_position().equals(f.getDestiny()) && f.getOcupation().equals(Ocupation.RETURNING)) {
            System.out.println(f.getAID().toString() + "-> Estou em casa!\n");
            f.setOcupation(Ocupation.RESTING);
            f.setDestiny(null);
            saveData(f);
            return;
        }

        if(f.getDestiny() == null) {
            block();
            return;
        }

        System.out.println("Fireman: " + f.getAID().toString() + ", destiny = " + f.getDestiny());

        if (f.getCap_fuel() > 0) {
            //Se está sem água vai abastecer
            if(f.getCap_water() <= 0){
                next = decideNewPosition(f, f.getDestiny(), false, true);
                //System.out.println("\n---------------------------------A ir para " + next.toString() + "------------------");
                f.setActual_position(next);
                f.setCap_fuel(f.getCap_fuel() - 1);
            }

            //Se está na posição de um FIRE combate-o
            else if (f.getTreating_fire() != null && f.getTreating_fire().getPositions().size() > 0 &&
                    (f.getActual_position().equals(f.getTreating_fire().getPositions().get(0)) ||
                            f.getActual_position().equals(f.getDestiny()))) {
                //System.out.println("Vou mesmo agora apagar o fogo!\n");
                f.setOcupation(Ocupation.IN_ACTION);
                f.setDestiny(null);
                this.myAgent.addBehaviour(new HandleFire(this.myAgent, 1000));
                saveData(f);
                return;
            }

            else {
                //Proatividade
                if(newFire != null && (f.getCap_fuel() * 2 > f.getCap_max_fuel())) {
                    //System.out.println("Vou apagar um fogo só porque posso!");
                    f.setCap_fuel(f.getCap_fuel() - 1);
                    f.setActual_position(newFire.getPositions().get(0));
                    f.setException_fire(newFire);
                    this.myAgent.addBehaviour(new HandleFire(this.myAgent, 1000));
                    saveData(f);
                    return;
                }

                //Reabastecer ÁGUA antes de ir para a posição standard
                else if ((f.getOcupation().equals(Ocupation.RETURNING)) && (f.getCap_water() * 2 <= f.getCap_max_water())) {
                    next = decideNewPosition(f, f.getDestiny(), false, true);
                    //System.out.println("\n---------------------------------A ir para " + next.toString() + "------------------");
                    f.setActual_position(next);
                    f.setCap_fuel(f.getCap_fuel() - 1);
                    f.setOcupation(Ocupation.RETURNING);
                }

                //Mover para a posição standard
                else if (f.getOcupation().equals(Ocupation.RETURNING)) {
                    next = decideNewPosition(f, f.getDestiny(), false, false);
                    //System.out.println("\n---------------------------------A ir para " + next.toString() + "------------------");
                    f.setActual_position(next);
                    f.setCap_fuel(f.getCap_fuel() - 1);
                }

                //Mover para o fogo
                else {
                    //System.out.println("Quero apagar o fogo que está na posição " + f.getDestiny());
                    next = decideNewPosition(f, f.getDestiny(), true, false);
                    //System.out.println("\n---------------------------------A ir para " + next.toString() + "------------------");
                    f.setActual_position(next);
                    f.setCap_fuel(f.getCap_fuel() - 1);
                }
            }
        }

        saveData(f);
    }

    public Tuple<Integer, Position, Position> maxPairValue(Position actual_position, int velocity, List<Fire> fire,
                        List<Position> fuel, List<Position> water, List<Position> houses, List<AgentData> fireman,
                        boolean searching_water){
        List<Position> places;
        List<Position> places_without_destiny;
        Pair<Integer, Position> path;
        Tuple<Integer, Position, Position> tuple_min = new Tuple<>(0, actual_position, actual_position);
        int distance_fuel_min = 0;

        if(searching_water){
            places = water;
        } else {
            places = fuel;
        }

        for (Position p: places) {
            places_without_destiny = new ArrayList<>(places);
            places_without_destiny.remove(p);

            if (searching_water) {
                path = fsp.findShortestPath(actual_position, p, velocity, fire, fuel, places_without_destiny, houses, fireman);
            } else {
                path = fsp.findShortestPath(actual_position, p, velocity, fire, places_without_destiny, water, houses, fireman);
            }

            if (distance_fuel_min == 0 || path.getFirst() < distance_fuel_min) {
                tuple_min.setFirst(path.getFirst());
                tuple_min.setSecond(path.getSecond());
                tuple_min.setThird(p);
                distance_fuel_min = tuple_min.getFirst();
            }
        }

        return tuple_min;
    }

    public Position decideNewPosition(Fireman f, Position goal, boolean extinguish_fire, boolean refill_water){
        int actual_cap_fuel = f.getCap_fuel();
        int velocity = f.getVel();
        Position actual_position = f.getActual_position();

        List<Fire> fires_without_goal = new ArrayList<>(f.getFires());
        fires_without_goal.stream().filter(a -> a.getPositions().
                removeIf(b -> b.getX() == goal.getX() && b.getY() == goal.getY())).collect(Collectors.toList());

        // Descobrir bomba de gasolina mais próxima do bombeiro
        Tuple<Integer, Position, Position> destiny_fuel_path = maxPairValue(actual_position, velocity, fires_without_goal,
                f.getFuel(), f.getWater(), f.getHouses(), new ArrayList<>(), false);
        Position destiny_fuel = destiny_fuel_path.getSecond();
        //System.out.println("[FIREMAN] A bomba de gasolina " + destiny_fuel_path.getThird() + " mais próxima está a " + distance_fuel + " posições");

        if (extinguish_fire) {
            return goingToGoalPosition(f, goal, actual_position, velocity, actual_cap_fuel, destiny_fuel, fires_without_goal, false);
        } else if (refill_water) {
            return goingRefillWater(f, actual_position, velocity, actual_cap_fuel, destiny_fuel);
        } else {
            return goingToGoalPosition(f, goal, actual_position, velocity, actual_cap_fuel, destiny_fuel, fires_without_goal, true);
        }
    }

    public Position goingToGoalPosition(Fireman f, Position goal, Position actual_position, int velocity,
                                        int actual_cap_fuel, Position destiny_fuel, List<Fire> fires_without_goal, boolean home){
        // Informações bombeiro ir para goal (posição fogo ou standard position->home)
        Pair<Integer, Position> destiny_path = fsp.findShortestPath(actual_position, goal, velocity, fires_without_goal,
                f.getFuel(), f.getWater(), f.getHouses(), new ArrayList<>());
        int distance_destiny = destiny_path.getFirst();
        Position next_to_destiny = destiny_path.getSecond();
        //System.out.println("[FIREMAN] O goal está a " + distance_destiny + " posições");

        // Informações bombeiro abastecer na bomba de gasolina mais próxima do goal
        Tuple<Integer, Position, Position> information_destiny_fuel_path = maxPairValue(goal, velocity, fires_without_goal,
                f.getFuel(), f.getWater(), f.getHouses(), new ArrayList<>(), false);
        Position fuel_position = information_destiny_fuel_path.getThird();
        int distance_fuel_nearest_goal = information_destiny_fuel_path.getFirst();
        //System.out.println("[FIREMAN] A bomba de gasolina mais próxima do goal está a " + distance_fuel_nearest_goal + " posições do goal");

        // Informação caminho do bombeiro até à bomba de gasolina mais próxima do goal
        List<Position> fuel_copy = new ArrayList<>(f.getFuel());
        fuel_copy.remove(fuel_position);

        Pair<Integer, Position> distance_destiny_fuel_path = fsp.findShortestPath(actual_position, fuel_position, velocity,
                fires_without_goal, fuel_copy, f.getWater(), f.getHouses(), new ArrayList<>());
        Position destiny_fuel_goal = distance_destiny_fuel_path.getSecond();
        int distance_fuel_goal = distance_destiny_fuel_path.getFirst();
        //System.out.println("[FIREMAN] A bomba de gasolina mais próxima do goal está a " + distance_fuel_goal + " posições do bombeiro\n");

        // Informações bombeiro goal e abastecer no final
        int distance_fuel_after_goal = distance_destiny + distance_fuel_nearest_goal;

        // Se bombeiro tem fuel suficiente para ir ao goal e abastecer
        if (actual_cap_fuel >= distance_fuel_after_goal) {
            /*if(home){
                System.out.println("\nVOU PARA CASAAAAA (" + f.getDestiny().toString() + "): " + next_to_destiny.toString() + "\n");
            } else {
                System.out.println("\nVOU APAGAR O FOGOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO (pos " + f.getDestiny().toString() + "): " + next_to_destiny.toString() + "\n");
            }*/
            return next_to_destiny;
        }
        // Caso o bombeiro consiga ir abastecer à bomba mais próxima do goal
        else if (actual_cap_fuel >= distance_fuel_goal) {
            /*if(home){
                System.out.println("\nVOU METER GASOLINAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA (maix próx casa " + fuel_position.toString() + ") :" + destiny_fuel_goal.toString() + "\n");
            } else{
                System.out.println("\nVOU METER GASOLINAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA (maix próx fogo " + fuel_position.toString() + ") :" + destiny_fuel_goal.toString() + "\n");
            }*/
            return destiny_fuel_goal;
        } else {
            //System.out.println("\nVOU METER GASOLINAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA (mais próx bombeiro): " + destiny_fuel.toString() + "\n");
            return destiny_fuel;
        }
    }

    public Position goingRefillWater(Fireman f, Position actual_position, int velocity, int actual_cap_fuel, Position destiny_fuel){
        // Informações caminho para posto de água mais próximo do bombeiro
        Tuple<Integer, Position, Position> destiny_water_path = maxPairValue(actual_position, velocity, f.getFires(),
                f.getFuel(), f.getWater(), f.getHouses(), new ArrayList<>(), true);
        int distance_water = destiny_water_path.getFirst();
        Position destiny_water = destiny_water_path.getSecond();
        Position water_position = destiny_water_path.getThird();
        //System.out.println("[FIREMAN] O posto de água " + water_position + " mais próximo está a " + distance_water + " posições");

        // Informações bombeiro abastecer na bomba de gasolina mais próxima da água
        Tuple<Integer, Position, Position> information_destiny_fuel_path = maxPairValue(water_position, velocity, f.getFires(),
                f.getFuel(), f.getWater(), f.getHouses(), new ArrayList<>(), false);
        Position fuel_position = information_destiny_fuel_path.getThird();
        int distance_fuel_nearest_goal = information_destiny_fuel_path.getFirst();
        //System.out.println("[FIREMAN] A bomba de gasolina " + fuel_position + " mais próxima da água " + water_position + " está a " + distance_fuel_nearest_goal + " posições da água");

        // Informações caminho bomba de gasolina até à água mais próxima da água
        List<Position> fuel_copy = new ArrayList<>(f.getFuel());
        fuel_copy.remove(fuel_position);

        Pair<Integer, Position> information_destiny_water_path = fsp.findShortestPath(actual_position, fuel_position, velocity,
                f.getFires(), fuel_copy, f.getWater(), f.getHouses(), new ArrayList<>());
        Position destiny_fuel_nearest_water = information_destiny_water_path.getSecond();
        int distance_fuel_nearest_water = information_destiny_water_path.getFirst();
        //System.out.println("[FIREMAN] A bomba de gasolina mais próxima do posto de água (goal) está a " + distance_fuel_nearest_water + " posições do bombeiro");

        // Informações bombeiro reabaster água e abastecer combustível no final
        int distance_fuel_after_water = distance_water + distance_fuel_nearest_goal;

        // Se bombeiro tem fuel suficiente para reabastecer água e reabastecer combustível
        if (actual_cap_fuel >= distance_fuel_after_water) {
            //System.out.println("\nVOU REABASTECER ÁGUAAAAAAAA: " + destiny_water.toString() + "\n");
            return destiny_water;
        }
        // Caso o bombeiro consiga ir abastecer ao posto mais próximo da água
        else if (actual_cap_fuel >= distance_fuel_nearest_water) {
            //System.out.println("\nVOU METER GASOLINAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA (mais próx da água " + water_position.toString() + ") :" + destiny_fuel_nearest_water.toString() + "\n");
            return destiny_fuel_nearest_water;
        } else {
            //System.out.println("\nVOU METER GASOLINAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA (mais próx bombeiro): " + destiny_fuel.toString() + "\n");
            return destiny_fuel;
        }
    }

    public void saveData(Fireman f){
        try {
            ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
            msg.setContentObject(new UpdateData(f.getActual_position(), f.getDestiny(),
                    f.getCap_fuel(), f.getCap_water(), f.getOcupation()));
            msg.addReceiver(f.getStation());
            this.myAgent.send(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}