package Util;

import Agents.AgentData;
import Logic.Fire;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.HashSet;

public class FindShortestPath {
    // Below arrays details all 4 possible movements from a cell
    private static int[] row = { -1, 0, 0, 1 };
    private static int[] col = { 0, -1, 1, 0 };
    private List<Position> path = new ArrayList<>();

    public void setPath(List<Position> path) {
        this.path = path;
    }

    // Find shortest route in the matrix from source cell to destination cell
    public static Node findPath(int velocity, List<Fire> fire, List<Position> fuel, List<Position> water,
                                List<Position> houses, List<AgentData> fireman, int x, int y, int x_dest, int y_dest) {
        // create a queue and enqueue first node
        Queue<Node> q = new ArrayDeque<>();
        Node src = new Node(x, y, null);
        q.add(src);

        // set to check if cell is visited before or not
        Set<String> visited = new HashSet<>();

        String key = src.getX() + "," + src.getY();
        visited.add(key);

        // run till queue is not empty
        while (!q.isEmpty())
        {
            // pop front node from queue and process it
            Node curr = q.poll();
            int i = curr.getX(), j = curr.getY();

            // return if destination is found
            if (i == x_dest && j == y_dest) {
                return curr;
            }

            // check all 4 movements from current cell
            // and recur for each valid movement
            for (int k = 0; k < 4; k++) {
                // value of velocity
                int n = velocity;
                //check for max velocity and to min velocity if necessary
                Position position = new Position(0,0);
                for (; n > 0; n--) {
                    // get next position coordinates using value of velocity
                    x = i + row[k] * n;
                    y = j + col[k] * n;
                    position.setX(x);
                    position.setY(y);

                    // check if it is possible to go to next position
                    // from current position
                    if (position.isValid(fire, fuel, water, houses, fireman)){
                        // construct next cell node
                        Node next = new Node(x, y, curr);

                        key = next.getX() + "," + next.getY();

                        // if it not visited yet
                        if (!visited.contains(key)) {
                            // push it into the queue and mark it as visited
                            q.add(next);
                            visited.add(key);
                        }
                    }
                }
            }
        }

        // return null if path is not possible
        return null;
    }

    // Utility function to print path from source to destination
    private int printPath(Node node) {
        if (node == null) {
            return 0;
        }
        int len = printPath(node.getParent());
        this.path.add(new Position(node.getX(),node.getY()));
        return len + 1;
    }

    public Pair<Integer, Position> findShortestPath(Position current, Position destination, int velocity,
                                                    List<Fire> fire, List<Position> fuel, List<Position> water,
                                                    List<Position> houses, List<AgentData> fireman) {
        this.setPath(new ArrayList<>());
        // Find a route from source cell to destination cell
        Node node = findPath(velocity, fire, fuel, water, houses, fireman, current.getX(), current.getY(),
                destination.getX(), destination.getY());

        int len = printPath(node) - 1;

        if (node != null) {
            if(this.path.size() >= 2) {
                //System.out.println("Caminho: " + path.toString());
                //System.out.println("Next: " + this.path.get(1).toString() + "------ se o destino é:" + destination.toString());
                return new Pair<>(len, this.path.get(1));
            } else { //quando ele está a abastecer
                return new Pair<>(len, this.path.get(0));
            }
        } else {
            //System.out.println("Destination not found");
            return new Pair<>(Integer.MAX_VALUE, current);
        }
    }
}