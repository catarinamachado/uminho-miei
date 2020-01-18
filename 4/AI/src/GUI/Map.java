package GUI;

import Agents.AgentData;
import Agents.Station;
import Logic.Fire;
import Logic.World;
import Logic.Zone;
import Util.FiremanType;
import Util.Position;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Map extends MapType {
    private JFrame frame;
    private JPanel panel;
    private JTable table;
    private JScrollPane tableContainer;
    private DefaultTableModel model_table;
    private String[][] data_colors;
    private ImageIcon[][] data_images;
    private HashMap<String, ImageIcon> objects;
    private HashMap<Position,String> zones;


    public Map(World world, Station s) {
        String[] colors = {
                "#f9ebea",
                "#ebdef0",
                "#e8daef",
                "#fdedec",
                "#f5b7b1",
                "#d7bde2",
                "#f5eef8",
                "#f4ecf7",
                "#f2d7d5",
                "#fadbd8",
                "#ebdef0",
                "#e8daef",
                "#e6b0aa",
                "#d2b4de",
        };
        objects = new HashMap<>();
        ImageIcon water = new ImageIcon(getClass().getResource("assets/water.png")); // load the image to a imageIcon
        Image image = water.getImage(); // transform it
        Image newimg = image.getScaledInstance(CELL_SIZE, CELL_SIZE,  java.awt.Image.SCALE_SMOOTH); // scale it the smooth way
        water = new ImageIcon(newimg);  // transform it back
        objects.put("water", water);

        ImageIcon fuel = new ImageIcon(getClass().getResource("assets/fuel.png")); // load the image to a imageIcon
        image = fuel.getImage();
        newimg = image.getScaledInstance(CELL_SIZE, CELL_SIZE, Image.SCALE_SMOOTH);
        fuel = new ImageIcon(newimg);
        objects.put("fuel", fuel);

        ImageIcon house = new ImageIcon(getClass().getResource("assets/house.png")); // load the image to a imageIcon
        image = house.getImage();
        newimg = image.getScaledInstance(CELL_SIZE, CELL_SIZE, Image.SCALE_SMOOTH);
        house = new ImageIcon(newimg);
        objects.put("house", house);

        ImageIcon fire = new ImageIcon(getClass().getResource("assets/fire.png")); // load the image to a imageIcon
        image = fire.getImage();
        newimg = image.getScaledInstance(CELL_SIZE, CELL_SIZE, Image.SCALE_SMOOTH);
        fire = new ImageIcon(newimg);
        objects.put("fire", fire);

        ImageIcon drone = new ImageIcon(getClass().getResource("assets/drone.png")); // load the image to a imageIcon
        image = drone.getImage();
        newimg = image.getScaledInstance(CELL_SIZE, CELL_SIZE, Image.SCALE_SMOOTH);
        drone = new ImageIcon(newimg);
        objects.put("drone", drone);

        ImageIcon aircraft = new ImageIcon(getClass().getResource("assets/aircraft.png")); // load the image to a imageIcon
        image = aircraft.getImage();
        newimg = image.getScaledInstance(CELL_SIZE, CELL_SIZE, Image.SCALE_SMOOTH);
        aircraft = new ImageIcon(newimg);
        objects.put("aircraft", aircraft);

        ImageIcon truck = new ImageIcon(getClass().getResource("assets/truck.png")); // load the image to a imageIcon
        image = truck.getImage();
        newimg = image.getScaledInstance(CELL_SIZE, CELL_SIZE, Image.SCALE_SMOOTH);
        truck = new ImageIcon(newimg);
        objects.put("truck", truck);

        this.zones = new HashMap<>();

        this.frame = new JFrame("Fire Simulation");
        this.panel = new JPanel();
        this.panel.setLayout(new BorderLayout());
        this.data_colors = new String[World.dimension][World.dimension];
        this.data_images = new ImageIcon[World.dimension][World.dimension];
        this.model_table = new DefaultTableModel(World.dimension,World.dimension);
        int j = 0;
        for(Zone z : world.getZones()){
            ArrayList<Position> pos = z.getAllPositions();
            for (Position p : pos) {
                this.zones.put(p, colors[j]);
            }
            j++;
        }
        update(world, s);
        this.table = new JTable(this.model_table);

        CellColorRenderer renderer = new CellColorRenderer(this.data_colors);
        this.table.setRowHeight(CELL_SIZE);
        TableColumnModel columnModel = this.table.getColumnModel();
        for(int i = 0; i < World.dimension; i++){
            columnModel.getColumn(i).setCellRenderer(renderer);
            columnModel.getColumn(i).setPreferredWidth(CELL_SIZE);
        }
        this.table.setPreferredScrollableViewportSize(table.getPreferredSize());

        this.table.setDefaultEditor(Object.class, null);
        this.table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        this.tableContainer = new JScrollPane(table);
        this.table.setTableHeader(null);

        this.panel.add(tableContainer, BorderLayout.CENTER);
        this.frame.getContentPane().add(panel);

        this.frame.pack();
        this.frame.setVisible(true);
        this.frame.setAlwaysOnTop(true);
    }

    @Override
    public void update(World world, Station ss) {
        for(int i = 0; i < World.dimension; i++){
            for(int j = 0; j < World.dimension; j++){
                this.data_images[i][j] = null;
                this.data_colors[i][j] = "#00ff00";
            }
        }
        for(Position p : this.zones.keySet()){
            this.data_colors[p.getY()][p.getX()] = this.zones.get(p);
        }
        //Populate Buildings
        for(Position p : world.getHouses()){
            this.data_images[p.getY()][p.getX()] = this.objects.get("house");
        }

        //Populate Firemans
        for(AgentData a : world.getFireman().values()){
            Position p = a.getActual_position();
            if(a.getFiremanType() == FiremanType.AIRCRAFT)
                this.data_images[p.getY()][p.getX()] = this.objects.get("aircraft");
            else if(a.getFiremanType() == FiremanType.DRONE)
                this.data_images[p.getY()][p.getX()] = this.objects.get("drone");
            else
                this.data_images[p.getY()][p.getX()] = this.objects.get("truck");
        }

        //Populate Water
        for(Position p : world.getWater()){
            this.data_images[p.getY()][p.getX()] = this.objects.get("water");
        }
        //Populate Gas
        for(Position p : world.getFuel()){
            this.data_images[p.getY()][p.getX()] = this.objects.get("fuel");
        }

        //Populate Fires
        ArrayList<Fire> all = new ArrayList<>();
        all.addAll(ss.getWaiting_fire());
        all.addAll(ss.getTreatment_fire().values());
        all.addAll(ss.getQuestioning().keySet());
        for(Fire f : all){
            for(Position p : f.getPositions())
                this.data_images[p.getY()][p.getX()] = this.objects.get("fire");
        }
    }

    @Override
    public void updateGUI(){
        CellColorRenderer renderer = new CellColorRenderer(this.data_colors);
        TableColumnModel columnModel = this.table.getColumnModel();
        for(int i = 0; i < World.dimension; i++){
            columnModel.getColumn(i).setCellRenderer(renderer);
            for(int j = 0; j < World.dimension; j++){
                    this.table.setValueAt(this.data_images[j][i],j,i);
            }
        }
        DefaultTableModel tableModel = (DefaultTableModel) this.table.getModel();
        tableModel.fireTableDataChanged();
    }
}

class CellColorRenderer extends DefaultTableCellRenderer {
    private String[][] data;
    CellColorRenderer(String[][] data) {
        super();
        this.data = data;
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,   boolean hasFocus, int row, int column) {
        Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        ((JLabel) cell).setIcon( (ImageIcon) value );
        System.out.println("(" + column + "," + row + ") -> " + this.data[row][column]);
        cell.setBackground(Color.decode(this.data[row][column]));
        return cell;
    }
}
