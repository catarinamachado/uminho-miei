package Agents.Behaviours;

import Agents.Station;
import Logic.Metric;
import Logic.Zone;
import Util.FiremanType;

import jade.core.behaviours.TickerBehaviour;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.ui.ApplicationFrame;
import javax.swing.*;
import java.awt.*;

public class MetricController extends TickerBehaviour {
    private Station s;
    private DefaultCategoryDataset datasetFires;
    private DefaultPieDataset datasetFiresZones;
    private DefaultCategoryDataset datasetTimeFires;
    private DefaultPieDataset datasetTypeofFireman;
    private ApplicationFrame window;

    public MetricController(Station s, int i) {
        super(s, i);
        this.s = s;
        this.datasetFires = new DefaultCategoryDataset();
        this.datasetFiresZones = new DefaultPieDataset();
        this.datasetTimeFires = new DefaultCategoryDataset();
        this.datasetTypeofFireman = new DefaultPieDataset();
        this.window = new ApplicationFrame("Statistics");
    }

    @Override
    protected void onTick() {
        Metric metric = this.s.getMetrics();
        int treating_fires = s.getTreatment_fire().size();
        int waiting_fires = s.getQuestioning().size() + s.getWaiting_fire().size();
        String time = String.valueOf((this.getPeriod()*this.getTickCount()) / 1000);

        //Ratio Fogos em tratamento/em espera
        datasetFires.addValue(treating_fires, "fogos em tratamento", time);
        datasetFires.addValue(waiting_fires,"fogos em espera", time);
        if(datasetFires.getColumnCount() > 15){
            datasetFires.removeColumn(0);
        }
        JFreeChart fires_chart = ChartFactory.createLineChart(
                "Fogos no Mundo",
                "Tempo (s)",
                "Número de Fogos",
                this.datasetFires,
                PlotOrientation.VERTICAL,
                true,true,false);
        ChartPanel chartPanel_fires_chart = new ChartPanel( fires_chart );
        chartPanel_fires_chart.setPreferredSize( new java.awt.Dimension( 460 , 267 ) );
        window.getContentPane().add(chartPanel_fires_chart, BorderLayout.SOUTH);

        //Fogos por zona
        datasetFiresZones.clear();
        for(Zone z : this.s.getWorld().getZones()){
            datasetFiresZones.setValue("Zona " + z.getId(),
                    this.s.getWorld().getFire().stream().filter(f -> f.getZone_id() == z.getId()).count()
                    );
        }
        JFreeChart fires_zones_chart = ChartFactory.createPieChart(
                "Fogos por Zona",
                this.datasetFiresZones,
                true, true, false);
        ChartPanel chartPanel_firesZones_chart = new ChartPanel( fires_zones_chart );

        chartPanel_firesZones_chart.setPreferredSize( new java.awt.Dimension( 460 , 267 ) );
        window.getContentPane().add(chartPanel_firesZones_chart, BorderLayout.EAST);

        //Utilizaçao de cada tipo de veiculo
        datasetTypeofFireman.clear();
        datasetTypeofFireman.setValue(FiremanType.AIRCRAFT.toString(),metric.getAircrafts_usage());
        datasetTypeofFireman.setValue(FiremanType.DRONE.toString(),metric.getDrones_usage());
        datasetTypeofFireman.setValue(FiremanType.FIRETRUCK.toString(),metric.getTrucks_usage());

        JFreeChart fireman_types_chart = ChartFactory.createPieChart(
                "Tipos de Veículos em Uso",
                this.datasetTypeofFireman,
                true, true, false);
        ChartPanel chartPanel_fireman_types_chart = new ChartPanel( fireman_types_chart );

        chartPanel_fireman_types_chart.setPreferredSize( new java.awt.Dimension( 460 , 267 ) );
        window.getContentPane().add(chartPanel_fireman_types_chart, BorderLayout.WEST);

        //Quantidade de combustível usado
        System.out.println("{\n" +
                        "Tempo decorrido:" + time +
                        "\nCombustível: " + metric.getFuel_usage() +
                        "\n}"
        );

        //Tempo médio de espera para fogo ser atendido
        //Tempo médio de resolução de um fogo
        datasetTimeFires.addValue(metric.getTimeToAssignFire(),"atribuir um fogo",time);
        datasetTimeFires.addValue(metric.getTimeToHandleFire(),"resolver um fogo", time);
        if(datasetTimeFires.getColumnCount() > 15){
            datasetTimeFires.removeColumn(0);
        }
        JFreeChart fires_time_chart = ChartFactory.createLineChart(
                "Estado dos Fogos",
                "Tempo (s)",
                "Tempo médio do estado dos fogos",
                this.datasetTimeFires,
                PlotOrientation.VERTICAL,
                true,true,false);
        ChartPanel chartPanel_fires_time_chart = new ChartPanel( fires_time_chart );
        chartPanel_fires_time_chart.setPreferredSize( new java.awt.Dimension( 460 , 267 ) );
        window.getContentPane().add(chartPanel_fires_time_chart, BorderLayout.NORTH);


        this.window.setSize( 1024 , 768 );
        this.window.setVisible( true );
    }
}
