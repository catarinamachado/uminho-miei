package GUI;

import Agents.Station;
import Logic.World;

public abstract class MapType {
    public final static int CELL_SIZE = 15;

    public abstract void update(World w, Station s);

    public abstract void updateGUI();
}
