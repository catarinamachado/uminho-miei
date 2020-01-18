package GUI;

import Agents.AgentData;
import Agents.Station;
import Logic.Fire;
import Logic.World;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class AgentState {
    private JFrame frame;
    private JPanel panel;
    private JTable table;
    private JScrollPane tableContainer;
    private DefaultTableModel model_table;

    private JPanel panel2;
    private JTable table2;
    private JScrollPane tableContainer2;
    private DefaultTableModel model_table2;

    public AgentState(World world, Station s){
        this.frame = new JFrame("World State");
        this.panel = new JPanel();
        this.panel.setLayout(new BorderLayout());
        this.model_table = new DefaultTableModel(world.getFireman().size(),7);
        this.table = new JTable(this.model_table);
        this.panel2 = new JPanel();
        this.panel2.setLayout(new BorderLayout());
        this.model_table2 = new DefaultTableModel(world.getFireman().size(),1);
        this.table2 = new JTable(this.model_table2);

        update(world, s);

        this.table.setDefaultEditor(Object.class, null);
        this.table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        this.table2.setDefaultEditor(Object.class, null);
        
        this.tableContainer = new JScrollPane(table);
        this.tableContainer2 = new JScrollPane(table2);

        this.table.setTableHeader(null);
        this.table2.setTableHeader(null);


        this.frame.getContentPane().add(tableContainer, BorderLayout.SOUTH);
        this.frame.getContentPane().add(tableContainer2, BorderLayout.CENTER);
        this.frame.validate();

        this.frame.pack();
        this.frame.setBounds(100,100,800,500);
        this.frame.setVisible(true);
    }

    public void update(World world, Station s) {
        DefaultTableModel model = (DefaultTableModel) this.table.getModel();
        model.setNumRows(0);
        model.addRow(new String[]{"Agent AID", "Water", "Fuel", "Ocupation", "Standard Position", "Actual Position", "Treating Fire?"});

        for(AgentData ag : world.getFireman().values()){
            if(ag.getTreating_fire() != null)
                model.addRow(new String[]{ag.getAid().getName(), Integer.toString(ag.getCap_water()), Integer.toString(ag.getCap_fuel()), ag.getOcupation().toString(), ag.getStd_position().toString(), ag.getActual_position().toString(), ag.getTreating_fire().getPositions().get(0).toString()});
            else
                model.addRow(new String[]{ag.getAid().getName(), Integer.toString(ag.getCap_water()), Integer.toString(ag.getCap_fuel()), ag.getOcupation().toString(), ag.getStd_position().toString(), ag.getActual_position().toString(), "Null"});
        }

        DefaultTableModel model2 = (DefaultTableModel) this.table2.getModel();
        model2.setNumRows(0);
        model2.addRow(new String[]{"Fire"});

        for(Fire f : s.getWaiting_fire())
            model2.addRow(new String[]{f.toString()});
        for(Fire f : s.getTreatment_fire().values())
            model2.addRow(new String[]{f.toString()});
        for(Fire f : s.getQuestioning().keySet())
            model2.addRow(new String[]{f.toString()});
    }

    public void updateGUI(){
        DefaultTableModel model = (DefaultTableModel) this.table.getModel();
        model.fireTableDataChanged();
        DefaultTableModel model2 = (DefaultTableModel) this.table.getModel();
        model2.fireTableDataChanged();
    }

}
