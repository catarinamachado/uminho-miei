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

public class Map2 extends MapType {
    private JFrame frame;
    private JPanel panel;
    private JTable table;
    private JScrollPane tableContainer;
    private DefaultTableModel model_table;
    private String[][] data;
    private int[][] firemans;
    private HashMap<String,String> objects;
    private HashMap<Position,String> zones;


    public Map2(World world, Station s) {
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
        objects.put("water", "#1446e2");
        objects.put("fuel", "#b4a18f");
        objects.put("house", "#ffffff");
        objects.put("fire", "#f20505");
        objects.put("drone", "#f7e52b");
        objects.put("aircraft", "#1de8cc");
        objects.put("truck", "#a336f9");
        this.zones = new HashMap<>();

        this.frame = new JFrame("Fire Simulation");
        this.panel = new JPanel();
        this.panel.setLayout(new BorderLayout());
        this.data = new String[World.dimension][World.dimension];
        this.firemans = new int[World.dimension][World.dimension];
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

        CellColorRenderer2 renderer = new CellColorRenderer2(this.data);
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
                this.firemans[i][j] = -1;
            }
        }
        for(Position p : this.zones.keySet()){
            this.data[p.getY()][p.getX()] = this.zones.get(p);
        }
        //Populate Buildings
        for(Position p : world.getHouses()){
            this.data[p.getY()][p.getX()] = this.objects.get("house");
        }

        //Populate Firemans
        for(AgentData a : world.getFireman().values()){
            Position p = a.getActual_position();
            String[] s = a.getAid().getName().split("@");
            this.firemans[p.getY()][p.getX()] = Integer.parseInt(s[0].replaceAll("[^0-9]", ""));
            if(a.getFiremanType() == FiremanType.AIRCRAFT)
                this.data[p.getY()][p.getX()] = this.objects.get("aircraft");
            else if(a.getFiremanType() == FiremanType.DRONE)
                this.data[p.getY()][p.getX()] = this.objects.get("drone");
            else
                this.data[p.getY()][p.getX()] = this.objects.get("truck");
        }

        //Populate Water
        for(Position p : world.getWater()){
            this.data[p.getY()][p.getX()] = this.objects.get("water");
        }
        //Populate Gas
        for(Position p : world.getFuel()){
            this.data[p.getY()][p.getX()] = this.objects.get("fuel");
        }

        //Populate Fires
        ArrayList<Fire> all = new ArrayList<>();
        all.addAll(ss.getWaiting_fire());
        all.addAll(ss.getTreatment_fire().values());
        all.addAll(ss.getQuestioning().keySet());
        for(Fire f : all){
            for(Position p : f.getPositions())
                this.data[p.getY()][p.getX()] = this.objects.get("fire");
        }
    }

    @Override
    public void updateGUI(){
        CellColorRenderer2 renderer = new CellColorRenderer2(this.data);
        TableColumnModel columnModel = this.table.getColumnModel();
        for(int i = 0; i < World.dimension; i++){
            columnModel.getColumn(i).setCellRenderer(renderer);
            for(int j = 0; j < World.dimension; j++){
                if(this.firemans[j][i] != -1)
                    this.table.setValueAt(this.firemans[j][i],j,i);
                else
                    this.table.setValueAt("",j,i);
            }
        }
        DefaultTableModel tableModel = (DefaultTableModel) this.table.getModel();
        tableModel.fireTableDataChanged();
    }
}

class CellColorRenderer2 extends DefaultTableCellRenderer {
    private String[][] data;
    CellColorRenderer2(String[][] data) {
        super();
        this.data = data;
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,   boolean hasFocus, int row, int column) {
        Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        cell.setBackground(Color.decode(this.data[row][column]));
        return cell;
    }
}