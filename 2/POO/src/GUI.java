/**
 * Graphical User Interface
 *
 * @author A81047
 * @author A34900
 * @author A82339
 * @version 20180513
 */

import javafx.application.Application;
import static javafx.geometry.HPos.RIGHT;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Line;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.MenuButton;
import javafx.scene.control.CheckMenuItem;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.util.Callback;
import javafx.scene.control.Alert.AlertType;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GUI extends Application {
    private Stage screen;
    private Scene main, login, new_individual, new_coletivo, dashboard_individual,
            total_facturas, dashboard_coletivo, dashboard_factura, dashboard_admin, contribuintes10,
            dashboard_consultafacturas_individual, dashboard_consultafacturas_coletivo,
            dashboard_novoDependente, dashboard_CAEfacturas, dashboard_historicoCAES, dashboard_agregado,
            dashboard_novoagregado, dashboard_consultafacturas_coletivoORDValor, dashboard_consultafacturas_coletivoCIValor,
            dashboard_consultafacturas_coletivoORDData,dashboard_contribuintesMaisFaturas;
    private int logged_user_nif;
    private int tipo_user;  // 1 é individual 2 é coletivo
    private JavaFactura struct;



    /**
       * Classe EmpresasTop auxiliar à classe GUI, que serve para facilitar a demostração das
       * empresas com mais faturas do sistema
       * e respetivos valores de despesas e de deduções
       */
    public class EmpresasTop {
        private int NIF;
        private int nFaturas;
        private BigDecimal valorDespesa;
        private BigDecimal valorDeducao;

        public EmpresasTop(EmpresasTop a) {
            NIF = a.getNIF();
            nFaturas = a.getNFaturas();
            valorDespesa = a.getValorDespesa();
            valorDeducao = a.getValorDeducao();
        }

        public EmpresasTop (int NIF, int nFaturas, BigDecimal valorDespesa, BigDecimal valorDeducao) {
            this.NIF = NIF;
            this.nFaturas = nFaturas;
            this.valorDespesa = valorDespesa;
            this.valorDeducao = valorDeducao;
        }

        public int getNIF() {
            return this.NIF;
        }

        public int getNFaturas() {
            return this.nFaturas;
        }

        public BigDecimal getValorDespesa() {
            return this.valorDespesa;
        }

        public BigDecimal getValorDeducao() {
            return this.valorDeducao;
        }

        public EmpresasTop clone(){
            return new EmpresasTop(this);
        }

        public boolean equals(Object object) {
            if(this == object)
                return true;

        if(object == null || (this.getClass() != object.getClass()))
            return false;

        EmpresasTop empresa = (EmpresasTop) object;
        return ( NIF == empresa.getNIF() && nFaturas == empresa.getNFaturas() &&
                 valorDespesa.equals(empresa.getValorDespesa()) && valorDeducao.equals(empresa.getValorDeducao()));
        }
    }


    private void loadContribuintesMaisFaturas(int n) {

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(8);
        grid.setPadding(new Insets(40, 40, 40, 40));
        grid.setAlignment(Pos.TOP_CENTER);

        TableView<EmpresasTop> table;

        TableColumn<EmpresasTop, Integer> numero = new TableColumn<>("N.º do Contribuinte");
        numero.setCellValueFactory(
                    new PropertyValueFactory<>("NIF"));
        numero.setMinWidth(150);

        TableColumn<EmpresasTop, Integer> quantidade = new TableColumn<>("Quantidade de Faturas Emitidas");
        quantidade.setCellValueFactory(
                new PropertyValueFactory<>("nFaturas"));
        quantidade.setMinWidth(230);

        TableColumn<EmpresasTop, BigDecimal> valorDespesa = new TableColumn<>("Valor total das Faturas (€)");
        valorDespesa.setCellValueFactory(
                new PropertyValueFactory<>("valorDespesa"));
        valorDespesa.setMinWidth(190);

        TableColumn<EmpresasTop, BigDecimal> valorDeducoes = new TableColumn<>("Valor total das Deduções (€)");
        valorDeducoes.setCellValueFactory(
                new PropertyValueFactory<>("valorDeducao"));
        valorDeducoes.setMinWidth(190);

        List<ContribuinteColetivo> empresas = new ArrayList<>();
        ObservableList<EmpresasTop> data = FXCollections.observableArrayList();

        for(ContribuinteColetivo cc: struct.EmpresasComMaisFacturas(n)){
           data.add(new EmpresasTop(cc.getNIF(),
                                    cc.numFacturas(),
                    struct.totalFacturado(cc.getNIF()) , struct.totalDeducaoCC(cc.getNIF())));
           empresas.add(cc);
        }


        table = new TableView<>();
        table.setItems(data);
        table.getColumns().addAll(numero, quantidade, valorDespesa, valorDeducoes);

        grid.getChildren().addAll(table);

        TextField empresasField = new TextField();
        empresasField.setPromptText("Numero de Empresas");
        grid.add(empresasField, 4, 28);

        Label totalDeduzido = new Label("Total deduções: ");
        totalDeduzido.setTextFill(Color.web("#0076a3"));
        totalDeduzido.setMaxWidth(Double.MAX_VALUE);
        grid.add(totalDeduzido, 5, 4);

        Label totalDeduzidoField = new Label (struct.deducaoConjunta(empresas).toString());
        grid.add(totalDeduzidoField, 5, 5);


        Button numeroEmpresas = new Button("Submeter");
        HBox hbNumeroEmpresas = new HBox(20);
        hbNumeroEmpresas.setAlignment(Pos.BOTTOM_LEFT);
        hbNumeroEmpresas.getChildren().add(numeroEmpresas);
        grid.add(hbNumeroEmpresas, 5, 28);

        Button limpar = new Button("Limpar");
        HBox hbLimpar = new HBox(10);
        hbLimpar.setAlignment(Pos.BOTTOM_RIGHT);
        hbLimpar.getChildren().add(limpar);
        grid.add(hbLimpar, 6, 28);

        limpar.setOnAction(e -> {
           loadContribuintesMaisFaturas(0);
           screen.setScene(dashboard_contribuintesMaisFaturas);
        });

        Button voltar = new Button("Voltar");
        HBox hbVoltar = new HBox(10);
        hbVoltar.setAlignment(Pos.BOTTOM_RIGHT);
        hbVoltar.getChildren().add(voltar);
        grid.add(hbVoltar, 6, 30);

        voltar.setOnAction(e -> {
            screen.setScene(this.dashboard_admin);
        });

        numeroEmpresas.setOnAction(e -> {
            int num;
            try{
                num = Integer.parseInt(empresasField.getText());
            }
            catch (NumberFormatException a) {
                num = 0;
            }

            loadContribuintesMaisFaturas(num);
            screen.setScene(dashboard_contribuintesMaisFaturas);
        });


        this.dashboard_contribuintesMaisFaturas = new Scene(grid, 1300, 800);
    }

    private void loadContribuintes10() {
        GridPane grid = new GridPane();
        grid.setHgap(70);
        grid.setVgap(8);
        grid.setPadding(new Insets(40, 40, 40, 40));
        grid.setAlignment(Pos.TOP_CENTER);

        ArrayList<ContribuinteIndividual> users = new ArrayList<>();
        struct.getTop10Contribuintes(users);


        Label um = new Label("NIF: " + Integer.toString(users.get(0).getNIF()));
        grid.add(um, 0, 12);
        um.setMaxWidth(Double.MAX_VALUE);
        um.setAlignment(Pos.CENTER);

        Label valor_um = new Label(users.get(0).getValorTotal().toString() + "€");
        grid.add(valor_um, 0, 14);
        valor_um.setTextFill(Color.web("#0076a3"));
        valor_um.setMaxWidth(Double.MAX_VALUE);
        valor_um.setAlignment(Pos.CENTER);


        Label dois = new Label("NIF: " + Integer.toString(users.get(1).getNIF()));
        grid.add(dois, 1, 12);
        dois.setMaxWidth(Double.MAX_VALUE);
        dois.setAlignment(Pos.CENTER);

        Label valor_dois = new Label(users.get(1).getValorTotal().toString() + "€");
        grid.add(valor_dois, 1, 14);
        valor_dois.setTextFill(Color.web("#0076a3"));
        valor_dois.setMaxWidth(Double.MAX_VALUE);
        valor_dois.setAlignment(Pos.CENTER);



        Label terceiro = new Label("NIF: " + Integer.toString(users.get(2).getNIF()));
        grid.add(terceiro, 2, 12);
        terceiro.setMaxWidth(Double.MAX_VALUE);
        terceiro.setAlignment(Pos.CENTER);

        Label valor_terceiro = new Label(users.get(2).getValorTotal().toString() + "€");
        grid.add(valor_terceiro, 2, 14);
        valor_terceiro.setTextFill(Color.web("#0076a3"));
        valor_terceiro.setMaxWidth(Double.MAX_VALUE);
        valor_terceiro.setAlignment(Pos.CENTER);



        Label quarto = new Label("NIF: " + Integer.toString(users.get(3).getNIF()));
        grid.add(quarto, 0, 19);
        quarto.setMaxWidth(Double.MAX_VALUE);
        quarto.setAlignment(Pos.CENTER);

        Label valor_quarto = new Label(users.get(3).getValorTotal().toString() + "€");
        grid.add(valor_quarto, 0, 21);
        valor_quarto.setTextFill(Color.web("#0076a3"));
        valor_quarto.setMaxWidth(Double.MAX_VALUE);
        valor_quarto.setAlignment(Pos.CENTER);



        Label cinco = new Label("NIF: " + Integer.toString(users.get(4).getNIF()));
        grid.add(cinco, 1, 19);
        cinco.setMaxWidth(Double.MAX_VALUE);
        cinco.setAlignment(Pos.CENTER);

        Label valor_cinco = new Label(users.get(4).getValorTotal().toString() + "€");
        grid.add(valor_cinco, 1, 21);
        valor_cinco.setTextFill(Color.web("#0076a3"));
        valor_cinco.setMaxWidth(Double.MAX_VALUE);
        valor_cinco.setAlignment(Pos.CENTER);



        Label seis = new Label("NIF: " + Integer.toString(users.get(5).getNIF()));
        grid.add(seis, 2, 19);
        seis.setMaxWidth(Double.MAX_VALUE);
        seis.setAlignment(Pos.CENTER);

        Label valor_seis = new Label(users.get(5).getValorTotal().toString() + "€");
        grid.add(valor_seis, 2, 21);
        valor_seis.setTextFill(Color.web("#0076a3"));
        valor_seis.setMaxWidth(Double.MAX_VALUE);
        valor_seis.setAlignment(Pos.CENTER);



        Label sete = new Label("NIF: " + Integer.toString(users.get(6).getNIF()));
        grid.add(sete, 0, 26);
        sete.setMaxWidth(Double.MAX_VALUE);
        sete.setAlignment(Pos.CENTER);

        Label valor_sete = new Label(users.get(6).getValorTotal().toString() + "€");
        grid.add(valor_sete, 0, 28);
        valor_sete.setTextFill(Color.web("#0076a3"));
        valor_sete.setMaxWidth(Double.MAX_VALUE);
        valor_sete.setAlignment(Pos.CENTER);



        Label oito = new Label("NIF: " + Integer.toString(users.get(7).getNIF()));
        grid.add(oito, 1, 26);
        oito.setMaxWidth(Double.MAX_VALUE);
        oito.setAlignment(Pos.CENTER);

        Label valor_oito = new Label(users.get(7).getValorTotal().toString() + "€");
        grid.add(valor_oito, 1, 28);
        valor_oito.setTextFill(Color.web("#0076a3"));
        valor_oito.setMaxWidth(Double.MAX_VALUE);
        valor_oito.setAlignment(Pos.CENTER);



        Label nove = new Label("NIF: " + Integer.toString(users.get(8).getNIF()));
        grid.add(nove, 2, 26);
        nove.setMaxWidth(Double.MAX_VALUE);
        nove.setAlignment(Pos.CENTER);

        Label valor_nove = new Label(users.get(8).getValorTotal().toString() + "€");
        grid.add(valor_nove, 2, 28);
        valor_nove.setTextFill(Color.web("#0076a3"));
        valor_nove.setMaxWidth(Double.MAX_VALUE);
        valor_nove.setAlignment(Pos.CENTER);



        Label dez = new Label("NIF: " + Integer.toString(users.get(9).getNIF()));
        grid.add(dez, 0, 33);
        dez.setMaxWidth(Double.MAX_VALUE);
        dez.setAlignment(Pos.CENTER);

        Label valor_dez = new Label(users.get(9).getValorTotal().toString() + "€");
        grid.add(valor_dez, 0, 35);
        valor_dez.setTextFill(Color.web("#0076a3"));
        valor_dez.setMaxWidth(Double.MAX_VALUE);
        valor_dez.setAlignment(Pos.CENTER);



        Button voltar = new Button("Voltar");
        HBox hbVoltar = new HBox(10);
        hbVoltar.setAlignment(Pos.BOTTOM_LEFT);
        hbVoltar.getChildren().add(voltar);
        grid.add(hbVoltar, 2, 58);

        voltar.setOnAction(e -> {
            screen.setScene(this.dashboard_admin);
        });


        this.contribuintes10 = new Scene(grid, 400, 350);
    }

    private void loadDashboardAdmin() {
        GridPane grid = new GridPane();
        grid.setHgap(70);
        grid.setVgap(8);
        grid.setPadding(new Insets(40, 40, 40, 40));
        grid.setAlignment(Pos.CENTER);


        Button contribuintes = new Button("10 contribuintes que mais gastam");
        HBox hbContribuintes = new HBox(10.0D);
        hbContribuintes.setAlignment(Pos.BOTTOM_LEFT);
        hbContribuintes.getChildren().add(contribuintes);
        grid.add(hbContribuintes, 1, 0);

        contribuintes.setOnAction((e) -> {
            this.loadContribuintes10();
            this.screen.setScene(this.contribuintes10);
        });

        Button empresas = new Button("Empresas com mais facturas emitidas");
        HBox hbEmpresas = new HBox(10.0D);
        hbEmpresas.setAlignment(Pos.BOTTOM_CENTER);
        hbEmpresas.getChildren().add(empresas);
        grid.add(hbEmpresas, 0, 0);

        empresas.setOnAction((e) -> {
            this.loadContribuintesMaisFaturas(0);
            this.screen.setScene(this.dashboard_contribuintesMaisFaturas);
        });


        Button gravarAplicacao = new Button("Gravar Estado");
        HBox hbgravar = new HBox(10);
        hbgravar.setAlignment(Pos.CENTER);
        hbgravar.getChildren().add(gravarAplicacao);
        grid.add(hbgravar, 0, 2);

        gravarAplicacao.setOnAction(e -> {
            try{
                struct.guardaEstado("javafactura.ser", "javafacturaNextId.ser");
            }
            catch (FileNotFoundException exception) {

            }
            catch (IOException exception) {
                exception.printStackTrace();
            }
            screen.setScene(this.dashboard_admin);
        });

        Button recuperarAplicacao = new Button("Recuperar Estado");
        HBox hbRecuperar = new HBox(10);
        hbRecuperar.setAlignment(Pos.CENTER);
        hbRecuperar.getChildren().add(recuperarAplicacao);
        grid.add(hbRecuperar, 1, 2);

        recuperarAplicacao.setOnAction(e -> {
            try{
                struct = struct.carregaEstado("javafactura.ser", "javafacturaNextId.ser");
            }
            catch (FileNotFoundException exception) {
            }
            catch (IOException exception) {
                exception.printStackTrace();
            }
            catch (ClassNotFoundException exception) {
            }
            screen.setScene(this.dashboard_admin);
        });

        TextField nome = new TextField();
        nome.setPromptText("Nome Ficheiro");
        grid.add(nome, 0, 7);

        Button exportcsv = new Button("Exportar para CSV");
        HBox hbexport = new HBox(10);
        hbexport.setAlignment(Pos.CENTER);
        hbexport.getChildren().add(exportcsv);
        grid.add(hbexport, 0, 8);

        exportcsv.setOnAction(e -> {
            try {
                String name = nome.getText();
                if (name.equals("")) {
                    Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("Erro");
                    alert.setHeaderText(null);
                    alert.setContentText("Campo Nome de Ficheiro Vazio.");
                    alert.showAndWait();
                }
                else {
                    struct.exportarCSV(name);
                    Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("Sucesso");
                    alert.setHeaderText(null);
                    alert.setContentText("Já pode consultar o ficheiro CSV.");
                    alert.showAndWait();
                }
            }
            catch (Exception a) {
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Erro");
                alert.setHeaderText(null);
                alert.setContentText(a.getMessage());
                alert.showAndWait();
            }
        });

        TextField nome1 = new TextField();
        nome1.setPromptText("Nome Ficheiro");
        grid.add(nome1, 1, 7);

        Button importcsv = new Button("Importar CSV");
        HBox hbimportcsv = new HBox(10);
        hbimportcsv.setAlignment(Pos.CENTER);
        hbimportcsv.getChildren().add(importcsv);
        grid.add(hbimportcsv, 1, 8);

        importcsv.setOnAction(e -> {
            try {
                String name = nome1.getText();
                if (name.equals("")) {
                    Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("Erro");
                    alert.setHeaderText(null);
                    alert.setContentText("Campo Nome de Ficheiro Vazio.");
                    alert.showAndWait();
                }
                else {
                    struct.importarCSV(name);
                    Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("Sucesso");
                    alert.setHeaderText(null);
                    alert.setContentText("CSV importado com sucesso.");
                    alert.showAndWait();
                }
            }
            catch (Exception a) {
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Erro");
                alert.setHeaderText(null);
                alert.setContentText(a.getMessage());
                alert.showAndWait();
            }
        });

        Button voltar = new Button("Voltar");
        HBox hbvoltar = new HBox(10.0D);
        hbvoltar.setAlignment(Pos.BOTTOM_RIGHT);
        hbvoltar.getChildren().add(voltar);
        grid.add(hbvoltar, 1, 13);

        voltar.setOnAction((e) -> {
            this.screen.setScene(this.main);
        });

        this.dashboard_admin = new Scene(grid, 300.0D, 250.0D);
    }

    private void loadDashboardIndividual(){
        GridPane grid = new GridPane();
        grid.setHgap(70);
        grid.setVgap(8);
        grid.setPadding(new Insets(40, 40, 40, 40));
        grid.setAlignment(Pos.TOP_CENTER);

        try{
            ContribuinteIndividual contribuinte_individual = struct.getContribuinteIndividual(logged_user_nif);

            Button direction_invoices = new Button("Direção do Agregado");
            HBox hbDirectionInvoices = new HBox(10);
            hbDirectionInvoices.setAlignment(Pos.CENTER);
            hbDirectionInvoices.getChildren().add(direction_invoices);
            grid.add(hbDirectionInvoices, 0, 0);

            direction_invoices.setOnAction(e -> {
                this.loadAgregado();
                screen.setScene(this.dashboard_agregado);
            });

            Button dependents = new Button("Associar Dependentes");
            HBox hbDependents = new HBox(10);
            hbDependents.setAlignment(Pos.CENTER);
            hbDependents.getChildren().add(dependents);
            grid.add(hbDependents, 1, 0);

            dependents.setOnAction(e -> {
                this.loadNovoDependente();
                screen.setScene(this.dashboard_novoDependente);
            });


            Button consultarFacturas = new Button("Consultar Facturas");
            HBox hbConsultarFacturas = new HBox(10);
            hbConsultarFacturas.setAlignment(Pos.CENTER);
            hbConsultarFacturas.getChildren().add(consultarFacturas);
            grid.add(hbConsultarFacturas, 2, 0);


            consultarFacturas.setOnAction(e -> {
                loadConsultarFacturasIndividual();
                screen.setScene(this.dashboard_consultafacturas_individual);
            });


            Button connect_invoices = new Button("Associar CAE das Facturas");
            HBox hbConnectInvoices = new HBox(10);
            hbConnectInvoices.setAlignment(Pos.CENTER);
            hbConnectInvoices.getChildren().add(connect_invoices);
            grid.add(hbConnectInvoices, 3, 0);

            connect_invoices.setOnAction(e -> {
                loadCAEFacturas();
                screen.setScene(this.dashboard_CAEfacturas);
            });


            //Dedução Fiscal do Agregado
            Text deducao_agreg_title = new Text("Dedução Fiscal do Agregado");
            deducao_agreg_title.setFont(Font.font ("Arial", FontWeight.BOLD, 14));
            deducao_agreg_title.setFill(Color.GREEN);
            HBox deducao_agreg = new HBox(10);
            deducao_agreg.setAlignment(Pos.BOTTOM_LEFT);
            deducao_agreg.getChildren().add(deducao_agreg_title);
            grid.add(deducao_agreg, 0, 4);

            //Valor total das deduções do agregado familiar
            try{
                String deducoes_string = struct.getDeducoesAgregado(logged_user_nif).toString();

                Label deducao_agregado = new Label(deducoes_string);
                grid.add(deducao_agregado, 0, 5);
                deducao_agregado.setMaxWidth(Double.MAX_VALUE);
                deducao_agregado.setAlignment(Pos.CENTER);
            }
            catch(CINullException err){
                Label deducao_agregado = new Label("Não foi possível calcular");
                grid.add(deducao_agregado, 0, 5);
                deducao_agregado.setMaxWidth(Double.MAX_VALUE);
                deducao_agregado.setAlignment(Pos.CENTER);
            }

            //Despesa Total do Agregado Familiar
            Text despesa_total_agregado = new Text("Despesa Total do Agregado Familiar");
            despesa_total_agregado.setFont(Font.font ("Arial", FontWeight.BOLD, 14));
            despesa_total_agregado.setFill(Color.BLUE);
            HBox desp_totalAgregado = new HBox(10);
            desp_totalAgregado.setAlignment(Pos.BOTTOM_RIGHT);
            desp_totalAgregado.getChildren().add(despesa_total_agregado);
            grid.add(despesa_total_agregado, 1, 4);

            //Valor total das despesas do agregado
            try{
                String despesas_string = struct.getDespesaTotalAgregado(logged_user_nif).toString();

                Label despesa_agregado = new Label(despesas_string);
                grid.add(despesa_agregado, 1, 5);
                despesa_agregado.setMaxWidth(Double.MAX_VALUE);
                despesa_agregado.setAlignment(Pos.CENTER);
            }
            catch(CINullException err){
                Label despesa_agregado = new Label("Não foi possível calcular");
                grid.add(despesa_agregado, 1, 5);
                despesa_agregado.setMaxWidth(Double.MAX_VALUE);
                despesa_agregado.setAlignment(Pos.CENTER);
            }


            //Dedução Fiscal Individual
            Text deducao_title = new Text("Dedução Fiscal Individual");
            deducao_title.setFont(Font.font ("Arial", FontWeight.BOLD, 14));
            deducao_title.setFill(Color.PURPLE);
            HBox deducao = new HBox(10);
            deducao.setAlignment(Pos.CENTER);
            deducao.getChildren().add(deducao_title);
            grid.add(deducao, 2, 4);

            //Valor total das deduções do contribuinte
            try{
                String deducoes_string = struct.getDeducoesTotalIndividuo(logged_user_nif).toString();

                Label deducao_individual = new Label(deducoes_string);
                grid.add(deducao_individual, 2, 5);
                deducao_individual.setMaxWidth(Double.MAX_VALUE);
                deducao_individual.setAlignment(Pos.CENTER);
            }
            catch(CINullException err){
                Label deducao_individual = new Label("Não foi possível calcular");
                grid.add(deducao_individual, 2, 5);
                deducao_individual.setMaxWidth(Double.MAX_VALUE);
                deducao_individual.setAlignment(Pos.CENTER);
            }


            //Despesa Total Individual
            Text despesa_total_title = new Text("Despesa Total Individual");
            despesa_total_title.setFont(Font.font ("Arial", FontWeight.BOLD, 14));
            despesa_total_title.setFill(Color.ORANGE);
            HBox desp_total = new HBox(10);
            desp_total.setAlignment(Pos.BOTTOM_RIGHT);
            desp_total.getChildren().add(despesa_total_title);
            grid.add(desp_total, 3, 4);

            //Valor total das despesas do contribuinte
            Label despesa_total = new Label(contribuinte_individual.getDespesaTotalIndividuo().toString());
            grid.add(despesa_total, 3, 5);
            despesa_total.setMaxWidth(Double.MAX_VALUE);
            despesa_total.setAlignment(Pos.CENTER);


            //Despesas Gerais Familiares
            Line line = new Line();
            line.setStartX(100.0f);
            line.setStartY(50.0f);
            line.setEndX(300.0f);
            line.setEndY(50.0f);
            grid.add(line, 0, 10);

            Line line2 = new Line();
            line2.setStartX(100.0f);
            line2.setStartY(50.0f);
            line2.setEndX(300.0f);
            line2.setEndY(50.0f);
            grid.add(line2, 0, 16);

            Label despesas_gerais = new Label("Despesas Gerais Familiares");
            grid.add(despesas_gerais, 0, 12);
            despesas_gerais.setMaxWidth(Double.MAX_VALUE);
            despesas_gerais.setAlignment(Pos.CENTER);

            Image dgf = new Image("imgs/dgf.png");
            ImageView dgfView = new ImageView();
            dgfView.setImage(dgf);
            dgfView.setFitHeight(50);
            dgfView.setFitWidth(50);
            HBox image_dgf = new HBox(10);
            image_dgf.setAlignment(Pos.CENTER);
            image_dgf.getChildren().add(dgfView);
            grid.add(image_dgf, 0, 13);

            try{
                Label valor_ded_dgf = new Label("Deduções: " +
                    struct.getDeducaoTotalIndividuoSetor(logged_user_nif, Gerais.objeto));

                grid.add(valor_ded_dgf, 0, 14);
                valor_ded_dgf.setTextFill(Color.web("#0076a3"));
                valor_ded_dgf.setMaxWidth(Double.MAX_VALUE);
                valor_ded_dgf.setAlignment(Pos.CENTER);
            }
            catch(CINullException err){
                Label valor_ded_dgf = new Label("Deduções: " +
                    "Não foi possível calcular");

                grid.add(valor_ded_dgf, 0, 14);
                valor_ded_dgf.setTextFill(Color.web("#0076a3"));
                valor_ded_dgf.setMaxWidth(Double.MAX_VALUE);
                valor_ded_dgf.setAlignment(Pos.CENTER);
            }
                Label valor_desp_dgf = new Label("Despesa total: " +
                    contribuinte_individual.getDespesaTotalSetor(Gerais.objeto.getCodigo()));
                    grid.add(valor_desp_dgf, 0, 15);
                    valor_desp_dgf.setMaxWidth(Double.MAX_VALUE);
                    valor_desp_dgf.setAlignment(Pos.CENTER);



            //Saúde
            Line line3 = new Line();
            line3.setStartX(100.0f);
            line3.setStartY(50.0f);
            line3.setEndX(300.0f);
            line3.setEndY(50.0f);
            grid.add(line3, 1, 10);

            Line line4 = new Line();
            line4.setStartX(100.0f);
            line4.setStartY(50.0f);
            line4.setEndX(300.0f);
            line4.setEndY(50.0f);
            grid.add(line4, 1, 16);

            Label saude_title = new Label("Saúde");
            grid.add(saude_title, 1, 12);
            saude_title.setMaxWidth(Double.MAX_VALUE);
            saude_title.setAlignment(Pos.CENTER);

            Image saude = new Image("imgs/saude.png");
            ImageView saudeView = new ImageView();
            saudeView.setImage(saude);
            saudeView.setFitHeight(50);
            saudeView.setFitWidth(50);
            HBox image_saude = new HBox(10);
            image_saude.setAlignment(Pos.CENTER);
            image_saude.getChildren().add(saudeView);
            grid.add(image_saude, 1, 13);

            try{
                Label valor_saude = new Label("Deduções: " +
                    struct.getDeducaoTotalIndividuoSetor(logged_user_nif, Saude.objeto));
                grid.add(valor_saude, 1, 14);
                valor_saude.setTextFill(Color.web("#0076a3"));
                valor_saude.setMaxWidth(Double.MAX_VALUE);
                valor_saude.setAlignment(Pos.CENTER);
            }
            catch(CINullException err){
                Label valor_saude = new Label("Deduções: " +
                    "Não foi possível calcular");
                grid.add(valor_saude, 1, 14);
                valor_saude.setTextFill(Color.web("#0076a3"));
                valor_saude.setMaxWidth(Double.MAX_VALUE);
                valor_saude.setAlignment(Pos.CENTER);
            }


            Label valor_desp_saude = new Label("Despesa total: " +
                contribuinte_individual.getDespesaTotalSetor(Saude.objeto.getCodigo()));
            grid.add(valor_desp_saude, 1, 15);
            valor_desp_saude.setMaxWidth(Double.MAX_VALUE);
            valor_desp_saude.setAlignment(Pos.CENTER);

            //Educação
            Line line5 = new Line();
            line5.setStartX(100.0f);
            line5.setStartY(50.0f);
            line5.setEndX(300.0f);
            line5.setEndY(50.0f);
            grid.add(line5, 2, 10);

            Line line6 = new Line();
            line6.setStartX(100.0f);
            line6.setStartY(50.0f);
            line6.setEndX(300.0f);
            line6.setEndY(50.0f);
            grid.add(line6, 2, 16);

            Label educacao_title = new Label("Educação");
            grid.add(educacao_title, 2, 12);
            educacao_title.setMaxWidth(Double.MAX_VALUE);
            educacao_title.setAlignment(Pos.CENTER);

            Image educacao = new Image("imgs/educacao.png");
            ImageView educacaoView = new ImageView();
            educacaoView.setImage(educacao);
            educacaoView.setFitHeight(50);
            educacaoView.setFitWidth(50);
            HBox image_educacao = new HBox(10);
            image_educacao.setAlignment(Pos.CENTER);
            image_educacao.getChildren().add(educacaoView);
            grid.add(image_educacao, 2, 13);

            try{
                Label valor_educacao = new Label("Deduções: " +
                  struct.getDeducaoTotalIndividuoSetor(logged_user_nif, Educacao.objeto));
                grid.add(valor_educacao, 2, 14);
                valor_educacao.setTextFill(Color.web("#0076a3"));
                valor_educacao.setMaxWidth(Double.MAX_VALUE);
                valor_educacao.setAlignment(Pos.CENTER);
            }
            catch(CINullException err){
                Label valor_educacao = new Label("Deduções: " +
                  "Não foi possível calcular");
                grid.add(valor_educacao, 2, 14);
                valor_educacao.setTextFill(Color.web("#0076a3"));
                valor_educacao.setMaxWidth(Double.MAX_VALUE);
                valor_educacao.setAlignment(Pos.CENTER);
            }

            Label valor_desp_educacao = new Label("Despesa total: " +
                contribuinte_individual.getDespesaTotalSetor(Educacao.objeto.getCodigo()));
            grid.add(valor_desp_educacao, 2, 15);
            valor_desp_educacao.setMaxWidth(Double.MAX_VALUE);
            valor_desp_educacao.setAlignment(Pos.CENTER);

            //Habitação
            Line line7 = new Line();
            line7.setStartX(100.0f);
            line7.setStartY(50.0f);
            line7.setEndX(300.0f);
            line7.setEndY(50.0f);
            grid.add(line7, 3, 10);

            Line line8 = new Line();
            line8.setStartX(100.0f);
            line8.setStartY(50.0f);
            line8.setEndX(300.0f);
            line8.setEndY(50.0f);
            grid.add(line8, 3, 16);

            Label habitacao_title = new Label("Habitação");
            grid.add(habitacao_title, 3, 12);
            habitacao_title.setMaxWidth(Double.MAX_VALUE);
            habitacao_title.setAlignment(Pos.CENTER);

            Image habitacao = new Image("imgs/habitacao.png");
            ImageView habitacaoView = new ImageView();
            habitacaoView.setImage(habitacao);
            habitacaoView.setFitHeight(50);
            habitacaoView.setFitWidth(50);
            HBox image_habitacao = new HBox(10);
            image_habitacao.setAlignment(Pos.CENTER);
            image_habitacao.getChildren().add(habitacaoView);
            grid.add(image_habitacao, 3, 13);

            try{
                Label valor_habitacao = new Label("Deduções: " +
                    struct.getDeducaoTotalIndividuoSetor(logged_user_nif, Habitacao.objeto));
                grid.add(valor_habitacao, 3, 14);
                valor_habitacao.setTextFill(Color.web("#0076a3"));
                valor_habitacao.setMaxWidth(Double.MAX_VALUE);
                valor_habitacao.setAlignment(Pos.CENTER);
            }
            catch(CINullException err){
                Label valor_habitacao = new Label("Deduções: " +
                    "Não foi possível calcular");
                grid.add(valor_habitacao, 3, 14);
                valor_habitacao.setTextFill(Color.web("#0076a3"));
                valor_habitacao.setMaxWidth(Double.MAX_VALUE);
                valor_habitacao.setAlignment(Pos.CENTER);
            }

            Label valor_desp_habitacao = new Label("Despesa total: " +
                contribuinte_individual.getDespesaTotalSetor(Habitacao.objeto.getCodigo()));
            grid.add(valor_desp_habitacao, 3, 15);
            valor_desp_habitacao.setMaxWidth(Double.MAX_VALUE);
            valor_desp_habitacao.setAlignment(Pos.CENTER);


            //Lares
            Line line9 = new Line();
            line9.setStartX(100.0f);
            line9.setStartY(50.0f);
            line9.setEndX(300.0f);
            line9.setEndY(50.0f);
            grid.add(line9, 0, 17);

            Line line10 = new Line();
            line10.setStartX(100.0f);
            line10.setStartY(50.0f);
            line10.setEndX(300.0f);
            line10.setEndY(50.0f);
            grid.add(line10, 0, 23);

            Label lares_title = new Label("Lares");
            grid.add(lares_title, 0, 19);
            lares_title.setMaxWidth(Double.MAX_VALUE);
            lares_title.setAlignment(Pos.CENTER);

            Image lares = new Image("imgs/lares.png");
            ImageView laresView = new ImageView();
            laresView.setImage(lares);
            laresView.setFitHeight(50);
            laresView.setFitWidth(50);
            HBox image_lares = new HBox(10);
            image_lares.setAlignment(Pos.CENTER);
            image_lares.getChildren().add(laresView);
            grid.add(image_lares, 0, 20);

            try{
                Label valor_lares = new Label("Deduções: " +
                  struct.getDeducaoTotalIndividuoSetor(logged_user_nif, Lares.objeto));
                grid.add(valor_lares, 0, 21);
                valor_lares.setTextFill(Color.web("#0076a3"));
                valor_lares.setMaxWidth(Double.MAX_VALUE);
                valor_lares.setAlignment(Pos.CENTER);
            }
            catch(CINullException err){
                Label valor_lares = new Label("Deduções: " +
                  "Não foi possível calcular");
                grid.add(valor_lares, 0, 21);
                valor_lares.setTextFill(Color.web("#0076a3"));
                valor_lares.setMaxWidth(Double.MAX_VALUE);
                valor_lares.setAlignment(Pos.CENTER);
            }

            Label valor_desp_lares = new Label("Despesa total: " +
                contribuinte_individual.getDespesaTotalSetor(Lares.objeto.getCodigo()));
            grid.add(valor_desp_lares, 0, 22);
            valor_desp_lares.setMaxWidth(Double.MAX_VALUE);
            valor_desp_lares.setAlignment(Pos.CENTER);


            //Reparação de Automóveis
            Line line11 = new Line();
            line11.setStartX(100.0f);
            line11.setStartY(50.0f);
            line11.setEndX(300.0f);
            line11.setEndY(50.0f);
            grid.add(line11, 1, 17);

            Line line12 = new Line();
            line12.setStartX(100.0f);
            line12.setStartY(50.0f);
            line12.setEndX(300.0f);
            line12.setEndY(50.0f);
            grid.add(line12, 1, 23);

            Label automoveis_title = new Label("Reparação de Automóveis");
            grid.add(automoveis_title, 1, 19);
            automoveis_title.setMaxWidth(Double.MAX_VALUE);
            automoveis_title.setAlignment(Pos.CENTER);

            Image automoveis = new Image("imgs/automoveis.png");
            ImageView automoveisView = new ImageView();
            automoveisView.setImage(automoveis);
            automoveisView.setFitHeight(50);
            automoveisView.setFitWidth(50);
            HBox image_automoveis = new HBox(10);
            image_automoveis.setAlignment(Pos.CENTER);
            image_automoveis.getChildren().add(automoveisView);
            grid.add(image_automoveis, 1, 20);

            try{
                Label valor_automoveis = new Label("Deduções: " +
                  struct.getDeducaoTotalIndividuoSetor(logged_user_nif, Automoveis.objeto));
                grid.add(valor_automoveis, 1, 21);
                valor_automoveis.setTextFill(Color.web("#0076a3"));
                valor_automoveis.setMaxWidth(Double.MAX_VALUE);
                valor_automoveis.setAlignment(Pos.CENTER);
            }
            catch(CINullException err){
                Label valor_automoveis = new Label("Deduções: " +
                  "Não foi possível calcular");
                grid.add(valor_automoveis, 1, 21);
                valor_automoveis.setTextFill(Color.web("#0076a3"));
                valor_automoveis.setMaxWidth(Double.MAX_VALUE);
                valor_automoveis.setAlignment(Pos.CENTER);
            }

            Label valor_desp_autom = new Label("Despesa total: " +
                  contribuinte_individual.getDespesaTotalSetor(Automoveis.objeto.getCodigo()));
            grid.add(valor_desp_autom, 1, 22);
            valor_desp_autom.setMaxWidth(Double.MAX_VALUE);
            valor_desp_autom.setAlignment(Pos.CENTER);

            //Restauração e Alojamento
            Line line13 = new Line();
            line13.setStartX(100.0f);
            line13.setStartY(50.0f);
            line13.setEndX(300.0f);
            line13.setEndY(50.0f);
            grid.add(line13, 2, 17);

            Line line14 = new Line();
            line14.setStartX(100.0f);
            line14.setStartY(50.0f);
            line14.setEndX(300.0f);
            line14.setEndY(50.0f);
            grid.add(line14, 2, 23);

            Label rest_aloj_title = new Label("Restauração e Alojamento");
            grid.add(rest_aloj_title, 2, 19);
            rest_aloj_title.setMaxWidth(Double.MAX_VALUE);
            rest_aloj_title.setAlignment(Pos.CENTER);

            Image rest_aloj = new Image("imgs/rest_aloj.png");
            ImageView rest_alojView = new ImageView();
            rest_alojView.setImage(rest_aloj);
            rest_alojView.setFitHeight(50);
            rest_alojView.setFitWidth(50);
            HBox image_rest_aloj = new HBox(10);
            image_rest_aloj.setAlignment(Pos.CENTER);
            image_rest_aloj.getChildren().add(rest_alojView);
            grid.add(image_rest_aloj, 2, 20);

            try{
                Label valor_rest_aloj = new Label("Deduções: " +
                  struct.getDeducaoTotalIndividuoSetor(logged_user_nif, Restauracao.objeto));
                grid.add(valor_rest_aloj, 2, 21);
                valor_rest_aloj.setTextFill(Color.web("#0076a3"));
                valor_rest_aloj.setMaxWidth(Double.MAX_VALUE);
                valor_rest_aloj.setAlignment(Pos.CENTER);
            }
            catch(CINullException err){
                Label valor_rest_aloj = new Label("Deduções: " +
                  "Não foi possível calcular");
                grid.add(valor_rest_aloj, 2, 21);
                valor_rest_aloj.setTextFill(Color.web("#0076a3"));
                valor_rest_aloj.setMaxWidth(Double.MAX_VALUE);
                valor_rest_aloj.setAlignment(Pos.CENTER);
            }

            Label valor_desp_rest_aloj = new Label("Despesa total: " +
                    contribuinte_individual.getDespesaTotalSetor(Restauracao.objeto.getCodigo()));
            grid.add(valor_desp_rest_aloj, 2, 22);
            valor_desp_rest_aloj.setMaxWidth(Double.MAX_VALUE);
            valor_desp_rest_aloj.setAlignment(Pos.CENTER);


            //Cabeleireiros
            Line line15 = new Line();
            line15.setStartX(100.0f);
            line15.setStartY(50.0f);
            line15.setEndX(300.0f);
            line15.setEndY(50.0f);
            grid.add(line15, 3, 17);

            Line line16 = new Line();
            line16.setStartX(100.0f);
            line16.setStartY(50.0f);
            line16.setEndX(300.0f);
            line16.setEndY(50.0f);
            grid.add(line16, 3, 23);

            Label cabelei_title = new Label("Cabeleireiros");
            grid.add(cabelei_title, 3, 19);
            cabelei_title.setMaxWidth(Double.MAX_VALUE);
            cabelei_title.setAlignment(Pos.CENTER);

            Image cabeleireiros = new Image("imgs/cabeleireiros.png");
            ImageView cabeleireirosView = new ImageView();
            cabeleireirosView.setImage(cabeleireiros);
            cabeleireirosView.setFitHeight(50);
            cabeleireirosView.setFitWidth(50);
            HBox image_cabeleireiros = new HBox(10);
            image_cabeleireiros.setAlignment(Pos.CENTER);
            image_cabeleireiros.getChildren().add(cabeleireirosView);
            grid.add(image_cabeleireiros, 3, 20);

            try{
                Label valor_cabeleireiros = new Label("Deduções: " +
                  struct.getDeducaoTotalIndividuoSetor(logged_user_nif, Cabeleireiros.objeto));
                grid.add(valor_cabeleireiros, 3, 21);
                valor_cabeleireiros.setTextFill(Color.web("#0076a3"));
                valor_cabeleireiros.setMaxWidth(Double.MAX_VALUE);
                valor_cabeleireiros.setAlignment(Pos.CENTER);
            }
            catch(CINullException err){
                Label valor_cabeleireiros = new Label("Deduções: " +
                    "Não foi possível calcular");
                grid.add(valor_cabeleireiros, 3, 21);
                valor_cabeleireiros.setTextFill(Color.web("#0076a3"));
                valor_cabeleireiros.setMaxWidth(Double.MAX_VALUE);
                valor_cabeleireiros.setAlignment(Pos.CENTER);
            }


            Label valor_desp_cabeleireiros = new Label("Despesa total: " +
                    contribuinte_individual.getDespesaTotalSetor(Cabeleireiros.objeto.getCodigo()));
            grid.add(valor_desp_cabeleireiros, 3, 22);
            valor_desp_cabeleireiros.setMaxWidth(Double.MAX_VALUE);
            valor_desp_cabeleireiros.setAlignment(Pos.CENTER);


            //Reparação de Motociclos
            Line line17 = new Line();
            line17.setStartX(100.0f);
            line17.setStartY(50.0f);
            line17.setEndX(300.0f);
            line17.setEndY(50.0f);
            grid.add(line17, 1, 24);

            Line line18 = new Line();
            line18.setStartX(100.0f);
            line18.setStartY(50.0f);
            line18.setEndX(300.0f);
            line18.setEndY(50.0f);
            grid.add(line18, 1, 30);

            Label motociclos_title = new Label("Reparação de Motociclos");
            grid.add(motociclos_title, 1, 26);
            motociclos_title.setMaxWidth(Double.MAX_VALUE);
            motociclos_title.setAlignment(Pos.CENTER);

            Image motociclos = new Image("imgs/motociclos.png");
            ImageView motociclosView = new ImageView();
            motociclosView.setImage(motociclos);
            motociclosView.setFitHeight(50);
            motociclosView.setFitWidth(50);
            HBox image_motociclos = new HBox(10);
            image_motociclos.setAlignment(Pos.CENTER);
            image_motociclos.getChildren().add(motociclosView);
            grid.add(image_motociclos, 1, 27);

            try{
            Label valor_motociclos = new Label("Deduções: " +
                  struct.getDeducaoTotalIndividuoSetor(logged_user_nif, Motociclos.objeto));
            grid.add(valor_motociclos, 1, 28);
            valor_motociclos.setTextFill(Color.web("#0076a3"));
            valor_motociclos.setMaxWidth(Double.MAX_VALUE);
            valor_motociclos.setAlignment(Pos.CENTER);
            }
            catch(CINullException err){
                Label valor_motociclos = new Label("Deduções: " +
                  "Não foi possível calcular");
            grid.add(valor_motociclos, 1, 28);
            valor_motociclos.setTextFill(Color.web("#0076a3"));
            valor_motociclos.setMaxWidth(Double.MAX_VALUE);
            valor_motociclos.setAlignment(Pos.CENTER);
            }

            Label valor_desp_motociclos = new Label("Despesa total: " +
                    contribuinte_individual.getDespesaTotalSetor(Motociclos.objeto.getCodigo()));
            grid.add(valor_desp_motociclos, 1, 29);
            valor_desp_motociclos.setMaxWidth(Double.MAX_VALUE);
            valor_desp_motociclos.setAlignment(Pos.CENTER);

            //Atividades Veterinárias
            Line line19 = new Line();
            line19.setStartX(100.0f);
            line19.setStartY(50.0f);
            line19.setEndX(300.0f);
            line19.setEndY(50.0f);
            grid.add(line19, 2, 24);

            Line line20 = new Line();
            line20.setStartX(100.0f);
            line20.setStartY(50.0f);
            line20.setEndX(300.0f);
            line20.setEndY(50.0f);
            grid.add(line20, 2, 30);

            Label veterinarias_title = new Label("Atividades Veterinárias");
            grid.add(veterinarias_title, 2, 26);
            veterinarias_title.setMaxWidth(Double.MAX_VALUE);
            veterinarias_title.setAlignment(Pos.CENTER);

            Image veterinarias = new Image("imgs/ativ_veterinarias.png");
            ImageView veterinariasView = new ImageView();
            veterinariasView.setImage(veterinarias);
            veterinariasView.setFitHeight(50);
            veterinariasView.setFitWidth(50);
            HBox image_veterinarias = new HBox(10);
            image_veterinarias.setAlignment(Pos.CENTER);
            image_veterinarias.getChildren().add(veterinariasView);
            grid.add(image_veterinarias, 2, 27);

            try{
                Label valor_veterinarias = new Label("Deduções: " +
                    struct.getDeducaoTotalIndividuoSetor(logged_user_nif, Veterinaria.objeto));
                grid.add(valor_veterinarias, 2, 28);
                valor_veterinarias.setTextFill(Color.web("#0076a3"));
                valor_veterinarias.setMaxWidth(Double.MAX_VALUE);
                valor_veterinarias.setAlignment(Pos.CENTER);
            }
            catch(CINullException err){
                Label valor_veterinarias = new Label("Deduções: " +
                    "Não foi possível calcular");
                grid.add(valor_veterinarias, 2, 28);
                valor_veterinarias.setTextFill(Color.web("#0076a3"));
                valor_veterinarias.setMaxWidth(Double.MAX_VALUE);
                valor_veterinarias.setAlignment(Pos.CENTER);
            }

            Label valor_desp_veterinarias = new Label("Despesa total: " +
                    contribuinte_individual.getDespesaTotalSetor(Veterinaria.objeto.getCodigo()));
            grid.add(valor_desp_veterinarias, 2, 29);
            valor_desp_veterinarias.setMaxWidth(Double.MAX_VALUE);
            valor_desp_veterinarias.setAlignment(Pos.CENTER);


            //Passes Mensais
            Line line21 = new Line();
            line21.setStartX(100.0f);
            line21.setStartY(50.0f);
            line21.setEndX(300.0f);
            line21.setEndY(50.0f);
            grid.add(line21, 3, 24);

            Line line22 = new Line();
            line22.setStartX(100.0f);
            line22.setStartY(50.0f);
            line22.setEndX(300.0f);
            line22.setEndY(50.0f);
            grid.add(line22, 3, 30);

            Label passes_title = new Label("Passes Mensais");
            grid.add(passes_title, 3, 26);
            passes_title.setMaxWidth(Double.MAX_VALUE);
            passes_title.setAlignment(Pos.CENTER);

            Image passes = new Image("imgs/passes_mensais.png");
            ImageView passesView = new ImageView();
            passesView.setImage(passes);
            passesView.setFitHeight(50);
            passesView.setFitWidth(50);
            HBox image_passes = new HBox(10);
            image_passes.setAlignment(Pos.CENTER);
            image_passes.getChildren().add(passesView);
            grid.add(image_passes, 3, 27);

            Label valor_passes = new Label("Deduções: " +
                  struct.getDeducaoTotalIndividuoSetor(logged_user_nif, PassesTransportes.objeto));
            grid.add(valor_passes, 3, 28);
            valor_passes.setTextFill(Color.web("#0076a3"));
            valor_passes.setMaxWidth(Double.MAX_VALUE);
            valor_passes.setAlignment(Pos.CENTER);

            Label valor_desp_passes = new Label("Despesa total: " +
                   contribuinte_individual.getDespesaTotalSetor(PassesTransportes.objeto.getCodigo()));
            grid.add(valor_desp_passes, 3, 29);
            valor_desp_passes.setMaxWidth(Double.MAX_VALUE);
            valor_desp_passes.setAlignment(Pos.CENTER);

            Button term_sess = new Button("Terminar Sessão");
            HBox hbTermSess = new HBox(10);
            hbTermSess.setAlignment(Pos.CENTER);
            hbTermSess.getChildren().add(term_sess);
            grid.add(hbTermSess, 0, 29);

            term_sess.setOnAction(e -> {
                screen.setScene(this.main);
            });
        }
        catch(CINullException err){
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Aviso");
            alert.setHeaderText(null);
            alert.setContentText("Não é possível visualizar a informação contida neste separador.");
            alert.showAndWait();

            screen.setScene(this.main);
        }

        this.dashboard_individual = new Scene(grid, 400, 350);
    }

    private void loadTotalFacturas(String valor_total, String valor_datas) {
        GridPane grid = new GridPane();
        grid.setHgap(70);
        grid.setVgap(8);
        grid.setPadding(new Insets(40, 40, 40, 40));
        grid.setAlignment(Pos.TOP_CENTER);

        ArrayList<ContribuinteIndividual> users = new ArrayList<>();
        struct.getTop10Contribuintes(users);

        Label um = new Label("Valor Total de Facturas:");
        grid.add(um, 0, 12);
        um.setMaxWidth(Double.MAX_VALUE);
        um.setAlignment(Pos.CENTER);

        Label valor_um = new Label(valor_total);
        grid.add(valor_um, 1, 12);
        valor_um.setTextFill(Color.web("#0076a3"));
        valor_um.setMaxWidth(Double.MAX_VALUE);
        valor_um.setAlignment(Pos.CENTER);

        Label data1 = new Label("Data de Início:");
        grid.add(data1, 0, 16);

        final DatePicker dataField1 = new DatePicker();
        grid.add(dataField1, 1, 16);

        Label data2 = new Label("Data de Fim:");
        grid.add(data2, 0, 18);

        final DatePicker dataField2 = new DatePicker();
        grid.add(dataField2, 1, 18);

        Button facturas = new Button("Consultar entre datas");
        HBox hbfacturas = new HBox(10);
        hbfacturas.setAlignment(Pos.BOTTOM_CENTER);
        hbfacturas.getChildren().add(facturas);
        grid.add(hbfacturas, 0, 20);

        facturas.setOnAction(e -> {
            loadTotalFacturas(valor_total,
                    struct.totalFacturado(
                            logged_user_nif,
                            dataField1.getValue(),
                            dataField2.getValue()
                            )
                            .toString() + "€");
            screen.setScene(this.total_facturas);
        });

        if (!valor_datas.equals("0")){
            Label data = new Label("Valor entre o período selecionado:");
            grid.add(data, 0, 22);
            data.setMaxWidth(Double.MAX_VALUE);
            data.setAlignment(Pos.CENTER);

            Label valor_data = new Label(valor_datas);
            grid.add(valor_data, 1, 22);
            valor_data.setTextFill(Color.web("#0076a3"));
            valor_data.setMaxWidth(Double.MAX_VALUE);
            valor_data.setAlignment(Pos.CENTER);
        }


        Button voltar = new Button("Voltar");
        HBox hbVoltar = new HBox(10);
        hbVoltar.setAlignment(Pos.BOTTOM_LEFT);
        hbVoltar.getChildren().add(voltar);
        grid.add(hbVoltar, 2, 58);

        voltar.setOnAction(e -> {
            if(logged_user_nif == 0)
                screen.setScene(this.dashboard_admin);
            else if(tipo_user == 2)
                screen.setScene(this.dashboard_coletivo);
            else {
                this.loadDashboardIndividual();
                screen.setScene(this.dashboard_individual);
            }
        });


        this.total_facturas = new Scene(grid, 400, 350);
    }

    private void loadDashboardColetivo() {
        GridPane grid = new GridPane();
        grid.setHgap(70);
        grid.setVgap(8);
        grid.setPadding(new Insets(40, 40, 40, 40));
        grid.setAlignment(Pos.CENTER);

        Button registar = new Button("Registar Facturas");
        HBox hbRegistar = new HBox(10);
        hbRegistar.setAlignment(Pos.BOTTOM_CENTER);
        hbRegistar.getChildren().add(registar);
        grid.add(hbRegistar, 0, 0);

        registar.setOnAction(e -> {
            this.loadRegistarFactura();
            screen.setScene(this.dashboard_factura);
        });

        Button consultarFacturas = new Button("Consultar Facturas");
        HBox hbConsultar = new HBox(10);
        hbConsultar.setAlignment(Pos.BOTTOM_LEFT);
        hbConsultar.getChildren().add(consultarFacturas);
        grid.add(hbConsultar, 1, 0);

        consultarFacturas.setOnAction(e -> {
            loadConsultarFacturasColetivo(0, "", "");
            screen.setScene(this.dashboard_consultafacturas_coletivo);
        });

        Button facturas = new Button("Consultar Valores de Facturas");
        HBox hbFacturas = new HBox(10);
        hbFacturas.setAlignment(Pos.BOTTOM_CENTER);
        hbFacturas.getChildren().add(facturas);
        grid.add(hbFacturas, 2, 0);

        facturas.setOnAction(e -> {
            loadTotalFacturas(struct.totalFacturado(logged_user_nif).toString() + "€", "0");
            screen.setScene(this.total_facturas);
        });

        Button historico_caes = new Button("Consultar Histórico CAES");
        HBox hbHistorico_caes = new HBox(10);
        hbHistorico_caes.setAlignment(Pos.BOTTOM_CENTER);
        hbHistorico_caes.getChildren().add(historico_caes);
        grid.add(hbHistorico_caes, 3, 0);

        historico_caes.setOnAction(e -> {
            loadHistoricoCAES();
            screen.setScene(this.dashboard_historicoCAES);
        });


        Button voltar = new Button("Voltar");
        HBox hbvoltar = new HBox(10);
        hbvoltar.setAlignment(Pos.BOTTOM_RIGHT);
        hbvoltar.getChildren().add(voltar);
        grid.add(hbvoltar, 3, 5);

        voltar.setOnAction(e -> {
            screen.setScene(this.main);
        });


        this.dashboard_coletivo = new Scene(grid, 300, 250);
    }

    private void loadRegistarFactura() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

       try{
           ContribuinteColetivo contribuinte_coletivo = struct.getContribuinteColetivo(logged_user_nif);


           Text scenetitle1 = new Text("Identificação da Factura");
           grid.add(scenetitle1, 0, 0, 3, 1);
           scenetitle1.setFill(Color.web("#0076a3"));


           Label numeroFactura = new Label("Número da Factura:");
           grid.add(numeroFactura, 0, 1);


           Label numFacturaField = new Label(Integer.toString(Factura.getNextId()));
           grid.add(numFacturaField, 1, 1);


           Label nifCliente = new Label("NIF do Consumidor:");
           grid.add(nifCliente, 0, 2);

           TextField nifClienteField = new TextField();
           grid.add(nifClienteField, 1, 2);


           Text scenetitle2 = new Text("Dados da Factura");
           grid.add(scenetitle2, 0, 1, 3, 9);
           scenetitle2.setFill(Color.web("#0076a3"));


           Label data = new Label("Data da Despesa:");
           grid.add(data, 0, 8);

           final DatePicker datePicker = new DatePicker();
           grid.add(datePicker, 1, 8);


           Label descricao = new Label("Descrição da Despesa:");
           grid.add(descricao, 0, 9);

           TextField descricaoField = new TextField();
           grid.add(descricaoField, 1, 9);

           Label valor = new Label("Valor da Despesa:");
           grid.add(valor, 0, 10);

           TextField valorField = new TextField();
           grid.add(valorField, 1, 10);


           Button voltar = new Button("Cancelar  X");
           HBox hbVoltar = new HBox(10);
           hbVoltar.setAlignment(Pos.BOTTOM_LEFT);
           hbVoltar.getChildren().add(voltar);
           grid.add(hbVoltar, 0, 13);
           voltar.setStyle("-fx-background-color: #FF3933; ");


           voltar.setOnAction(e -> {
                Factura.setNextId(Integer.parseInt(numFacturaField.getText()));
                screen.setScene(this.dashboard_coletivo);
           });


           Button guardar = new Button("Guardar √");
           HBox hbGuardar = new HBox(10);
           hbGuardar.setAlignment(Pos.BOTTOM_RIGHT);
           hbGuardar.getChildren().add(guardar);
           grid.add(hbGuardar, 1, 13);
           guardar.setStyle("-fx-background-color: #39FF33; ");



           guardar.setOnAction(e -> {
             try{
               int nif_cliente = Integer.parseInt(nifClienteField.getText());
               ContribuinteIndividual ci = struct.getContribuinteIndividual(nif_cliente);

               if(datePicker.getValue() != null){
                   Factura f = new Factura(
                        Integer.parseInt(numFacturaField.getText()),
                        logged_user_nif,
                        contribuinte_coletivo.getNome(),
                        datePicker.getValue(),
                        nif_cliente,
                        descricaoField.getText(),
                        contribuinte_coletivo.inicializarAtividadeEconomica(),
                        new BigDecimal(valorField.getText().replaceAll(",", "")),
                        contribuinte_coletivo.inicializarHistoricoCAES()
                    );

                    Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("Informação");
                    alert.setHeaderText(null);
                    alert.setContentText("Fatura registada com sucesso.");
                    alert.showAndWait();

                    struct.addFactura(f);
                    screen.setScene(this.dashboard_coletivo);
                }
                else{
                    Alert alert = new Alert(AlertType.WARNING);
                    alert.setTitle("Aviso");
                    alert.setHeaderText(null);
                    alert.setContentText("Insere uma data válida.");
                    alert.showAndWait();

                }
            }
            catch(CINullException err){
                Alert alert = new Alert(AlertType.WARNING);
                alert.setTitle("Aviso");
                alert.setHeaderText(null);
                alert.setContentText("Esse nif não pertence a nenhum contribuinte individual.");
                alert.showAndWait();

            }
            catch(Exception error){
                error.printStackTrace();

                Alert alert = new Alert(AlertType.WARNING);
                alert.setTitle("Aviso");
                alert.setHeaderText(null);
                alert.setContentText("Todos os campos têm que estar corretamente preenchidos.");
                alert.showAndWait();
            }
          });
        }
        catch(CCNullException err){
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Aviso");
            alert.setHeaderText(null);
            alert.setContentText("Não é possível visualizar a informação contida neste separador.");
            alert.showAndWait();

            screen.setScene(this.dashboard_coletivo);
        }

        this.dashboard_factura = new Scene(grid, 400, 350);
    }

    private void loadMain() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(20);
        grid.setVgap(20);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Image img = new Image("imgs/logo.png");
        ImageView imgView = new ImageView(img);
        grid.add(imgView, 1, 0);

        Button login = new Button("Login");
        HBox hbLogin = new HBox(10);
        hbLogin.setAlignment(Pos.CENTER);
        hbLogin.getChildren().add(login);
        grid.add(hbLogin, 1, 4);

        login.setOnAction(e -> {
            loadLogin();
            screen.setScene(this.login);
        });

        Button new_individual = new Button("Novo Contribuinte Individual");
        HBox hbIndividual = new HBox(10);
        hbIndividual.setAlignment(Pos.CENTER);
        hbIndividual.getChildren().add(new_individual);
        grid.add(hbIndividual, 1, 6);

        new_individual.setOnAction(e -> {
            this.loadIndividual();
            screen.setScene(this.new_individual);
        });

        Button new_coletivo = new Button("Novo Contribuinte Coletivo");
        HBox hbColetivo = new HBox(10);
        hbColetivo.setAlignment(Pos.CENTER);
        hbColetivo.getChildren().add(new_coletivo);
        grid.add(hbColetivo, 1, 8);

        new_coletivo.setOnAction(e -> {
            this.loadColetivo();
            screen.setScene(this.new_coletivo);
        });


        Button sair = new Button("Sair");
        HBox hbSair = new HBox(10);
        hbSair.setAlignment(Pos.CENTER);
        hbSair.getChildren().add(sair);
        grid.add(hbSair, 1, 10);

        sair.setOnAction(e -> {
            Platform.exit();
        });


        this.main = new Scene(grid, 300, 250);
    }

    private void loadLogin() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));


        Text scenetitle = new Text("Início de Sessão");
        grid.add(scenetitle, 0, 0, 2, 1);


        Label nif = new Label("NIF:");
        grid.add(nif, 0, 2);

        TextField nifField = new TextField();
        grid.add(nifField, 1, 2);


        Label pw = new Label("Password:");
        grid.add(pw, 0, 4);

        PasswordField pwBox = new PasswordField();
        grid.add(pwBox, 1, 4);


        Button voltar = new Button("Voltar");
        HBox hbVoltar = new HBox(10);
        hbVoltar.setAlignment(Pos.BOTTOM_LEFT);
        hbVoltar.getChildren().add(voltar);
        grid.add(hbVoltar, 0, 6);

        voltar.setOnAction(e -> {
            screen.setScene(this.main);
        });


        Button btn = new Button("Iniciar sessão");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn);
        grid.add(hbBtn, 1, 6);

        final Text actiontarget = new Text();
        grid.add(actiontarget, 0, 7);
        grid.setColumnSpan(actiontarget, 2);
        grid.setHalignment(actiontarget, RIGHT);
        actiontarget.setId("actiontarget");

        btn.setOnAction(e -> {
            try{
            int login_nif = Integer.parseInt(nifField.getText());
            String login_pw = pwBox.getText();
            this.tipo_user = 0;
            boolean flag = false;

            if (login_nif == 0 && login_pw.equals("admin")){
                screen.setScene(this.dashboard_admin);
                flag = true;
            }

            try{
                boolean c = struct.containsIndividual(login_nif, login_pw);

                if(c){
                    logged_user_nif = login_nif;
                    tipo_user = 1;
                    flag = true;
                    loadDashboardIndividual();
                    screen.setScene(this.dashboard_individual);
                }
            }
            catch(CINullException err){
            }

            try{
                boolean c = struct.containsColetivo(login_nif, login_pw);

                if(c){
                    logged_user_nif = login_nif;
                    tipo_user = 2;
                    flag = true;
                    screen.setScene(this.dashboard_coletivo);
                }
            }
            catch(CCNullException err){
            }

            if(!flag && login_nif != 0){
                Alert alert = new Alert(AlertType.WARNING);
                alert.setTitle("Aviso");
                alert.setHeaderText(null);
                alert.setContentText("NIF ou password incorretos.");
                alert.showAndWait();

                loadLogin();
                screen.setScene(login);
            }
        }
        catch (NumberFormatException a) {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Aviso");
            alert.setHeaderText(null);
            alert.setContentText("NIF tem que ser um número.");
            alert.showAndWait();

            loadLogin();
            screen.setScene(login);
        }

        });

        this.login = new Scene(grid, 300, 250);
    }

    private void loadIndividual() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));


        Text scenetitle = new Text("Novo Contribuinte Individual");
        grid.add(scenetitle, 0, 0, 2, 1);

        Label nome = new Label("Nome:");
        grid.add(nome, 0, 1);

        TextField nomeField = new TextField();
        grid.add(nomeField, 1, 1);


        Label nif = new Label("NIF:");
        grid.add(nif, 0, 2);

        TextField nifField = new TextField();
        grid.add(nifField, 1, 2);


        Label email = new Label("Email:");
        grid.add(email, 0, 3);

        TextField emailField = new TextField();
        grid.add(emailField, 1, 3);


        Label pw = new Label("Password:");
        grid.add(pw, 0, 4);

        PasswordField pwField = new PasswordField();
        grid.add(pwField, 1, 4);


        Label morada = new Label("Morada:");
        grid.add(morada, 0, 5);

        TextField moradaField = new TextField();
        grid.add(moradaField, 1, 5);


        Button voltar = new Button("Voltar");
        HBox hbVoltar = new HBox(10);
        hbVoltar.setAlignment(Pos.BOTTOM_LEFT);
        hbVoltar.getChildren().add(voltar);
        grid.add(hbVoltar, 0, 6);

        voltar.setOnAction(e -> {
            screen.setScene(this.main);
        });


        Button btn = new Button("Registar");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn);
        grid.add(hbBtn, 1, 6);

        final Text actiontarget = new Text();
        grid.add(actiontarget, 0, 7);
        grid.setColumnSpan(actiontarget, 2);
        grid.setHalignment(actiontarget, RIGHT);
        actiontarget.setId("actiontarget");

        btn.setOnAction(e -> {
            try{
                int nifCI = Integer.parseInt(nifField.getText());
                String emailCI = emailField.getText();
                String nomeCI = nomeField.getText();
                String moradaCI = moradaField.getText();
                String pwCI = pwField.getText();

                try{
                    ContribuinteIndividual cic = struct.getContribuinteIndividual(nifCI);

                    Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("Informação");
                    alert.setHeaderText(null);
                    alert.setContentText("Esse nif já pertence a um contribuinte.");
                    alert.showAndWait();
                }
                catch(CINullException err){
                    try{
                        ContribuinteColetivo ccc = struct.getContribuinteColetivo(nifCI);

                        Alert alert = new Alert(AlertType.INFORMATION);
                        alert.setTitle("Informação");
                        alert.setHeaderText(null);
                        alert.setContentText("Esse nif já pertence a um contribuinte.");
                        alert.showAndWait();
                    }

                    catch(CCNullException err2){
                      if(!emailCI.isEmpty() && !nomeCI.isEmpty() && !moradaCI.isEmpty() && !pwCI.isEmpty()){

                         ContribuinteIndividual ci = new ContribuinteIndividual(
                            nifCI,
                            emailCI,
                            nomeCI,
                            moradaCI,
                            pwCI,
                            0, //n_direcao
                            0, //n_dependentes
                            new ArrayList<>(), //lista nifs dependentes
                            new ArrayList<>(), //lista nifs direcao
                            1, //coeficiente_fiscal
                            new HashMap<>() //grupo faturas
                        );

                        Alert alert = new Alert(AlertType.INFORMATION);
                        alert.setTitle("Informação");
                        alert.setHeaderText(null);
                        alert.setContentText("Contribuinte individual adicionado com sucesso.");
                        alert.showAndWait();

                        this.struct.add(ci);
                        screen.setScene(this.main);
                    }
                    else{
                        Alert alert = new Alert(AlertType.WARNING);
                        alert.setTitle("Aviso");
                        alert.setHeaderText(null);
                        alert.setContentText("Tens que preencher todos os campos.");
                        alert.showAndWait();

                    }
            }
        }
        }
            catch (NumberFormatException a) {
                Alert alert = new Alert(AlertType.WARNING);
                alert.setTitle("Aviso");
                alert.setHeaderText(null);
                alert.setContentText("O NIF tem que ser um número.");
                alert.showAndWait();
        }
    });


        this.new_individual = new Scene(grid, 300, 250);
    }

    private void loadColetivo() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));


        Text scenetitle = new Text("Novo Contribuinte Coletivo");
        grid.add(scenetitle, 0, 0, 2, 1);

        Label nome = new Label("Nome:");
        grid.add(nome, 0, 1);

        TextField nomeField = new TextField();
        grid.add(nomeField, 1, 1);


        Label nif = new Label("NIF:");
        grid.add(nif, 0, 2);

        TextField nifField = new TextField();
        grid.add(nifField, 1, 2);


        Label email = new Label("Email:");
        grid.add(email, 0, 3);

        TextField emailField = new TextField();
        grid.add(emailField, 1, 3);


        Label pw = new Label("Password:");
        grid.add(pw, 0, 4);

        PasswordField pwField = new PasswordField();
        grid.add(pwField, 1, 4);


        Label morada = new Label("Sede:");
        grid.add(morada, 0, 5);

        TextField moradaField = new TextField();
        grid.add(moradaField, 1, 5);

        Label concelho = new Label("Concelho:");
        grid.add(concelho, 0, 6);

        ObservableList<MenuItem> concelhos = FXCollections.observableArrayList();

        MenuButton concelhosButton = new MenuButton("Escolha o concelho");
        MenuItem aveiro = new MenuItem("Aveiro");
        MenuItem beja = new MenuItem("Beja");
        MenuItem braga = new MenuItem("Braga");
        MenuItem braganca = new MenuItem("Bragança");
        MenuItem castelo_branco = new MenuItem("Castelo Branco");
        MenuItem coimbra = new MenuItem("Coimbra");
        MenuItem evora = new MenuItem("Évora");
        MenuItem faro = new MenuItem("Faro");
        MenuItem guarda = new MenuItem("Guarda");
        MenuItem leiria = new MenuItem("Leiria");
        MenuItem lisboa = new MenuItem("Lisboa");
        MenuItem portalegre = new MenuItem("Portalegre");
        MenuItem porto = new MenuItem("Porto");
        MenuItem santarem = new MenuItem("Santarém");
        MenuItem setubal = new MenuItem("Setúbal");
        MenuItem viana_do_castelo = new MenuItem("Viana do Castelo");
        MenuItem vila_real = new MenuItem("Vila Real");
        MenuItem viseu = new MenuItem("Viseu");

        concelhos.addAll(aveiro, beja, braga, braganca, castelo_branco, coimbra, evora,
                            faro, guarda, leiria, lisboa, portalegre, porto, santarem, setubal,
                            viana_do_castelo, vila_real, viseu);
        concelhosButton.getItems().addAll(concelhos);

        aveiro.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent actionEvent) {
                concelhosButton.setText(aveiro.getText());
            }
        });

        beja.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent actionEvent) {
                concelhosButton.setText(beja.getText());
            }
        });

        braga.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent actionEvent) {
                concelhosButton.setText(braga.getText());
            }
        });

        braganca.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent actionEvent) {
                concelhosButton.setText(braganca.getText());
            }
        });

        castelo_branco.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent actionEvent) {
                concelhosButton.setText(castelo_branco.getText());
            }
        });

        coimbra.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent actionEvent) {
                concelhosButton.setText(coimbra.getText());
            }
        });

        evora.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent actionEvent) {
                concelhosButton.setText(evora.getText());
            }
        });

        faro.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent actionEvent) {
                concelhosButton.setText(faro.getText());
            }
        });

        guarda.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent actionEvent) {
                concelhosButton.setText(guarda.getText());
            }
        });

        leiria.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent actionEvent) {
                concelhosButton.setText(leiria.getText());
            }
        });

        lisboa.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent actionEvent) {
                concelhosButton.setText(lisboa.getText());
            }
        });

        portalegre.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent actionEvent) {
                concelhosButton.setText(portalegre.getText());
            }
        });

        porto.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent actionEvent) {
                concelhosButton.setText(porto.getText());
            }
        });

        santarem.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent actionEvent) {
                concelhosButton.setText(santarem.getText());
            }
        });

        setubal.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent actionEvent) {
                concelhosButton.setText(setubal.getText());
            }
        });

        viana_do_castelo.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent actionEvent) {
                concelhosButton.setText(viana_do_castelo.getText());
            }
        });

        vila_real.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent actionEvent) {
                concelhosButton.setText(vila_real.getText());
            }
        });

        viseu.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent actionEvent) {
                concelhosButton.setText(viseu.getText());
            }
        });

        grid.add(concelhosButton, 1, 6);

        Label natureza = new Label("Atividades Económicas:");
        grid.add(natureza, 0, 7);

        ObservableList<MenuItem> setores = FXCollections.observableArrayList();

        ArrayList<Integer> atividades_economicas = new ArrayList<>();

        MenuButton naturezaButton = new MenuButton("Escolha os setores de atividade");
        CheckMenuItem despesasGerais = new CheckMenuItem("Despesas Gerais e Familiares");
        CheckMenuItem saude = new CheckMenuItem("Saúde");
        CheckMenuItem educacao = new CheckMenuItem("Educação");
        CheckMenuItem habitacao = new CheckMenuItem("Habitação");
        CheckMenuItem lares = new CheckMenuItem("Lares");
        CheckMenuItem repAutomoveis = new CheckMenuItem("Reparação de Automóveis");
        CheckMenuItem restAlojam = new CheckMenuItem("Restauração e Alojamento");
        CheckMenuItem cabeleireiros = new CheckMenuItem("Cabeleireiros");
        CheckMenuItem repMotociclos = new CheckMenuItem("Reparação de Motociclos");
        CheckMenuItem atiVeterinarias = new CheckMenuItem("Atividades Veterinárias");
        CheckMenuItem passesMensais = new CheckMenuItem("Passes Mensais");

        setores.addAll(despesasGerais, saude, educacao,
                habitacao, lares, repAutomoveis, restAlojam, cabeleireiros,
                repMotociclos, atiVeterinarias, passesMensais);
        naturezaButton.getItems().addAll(setores);

        grid.add(naturezaButton, 1, 7);


        Button voltar = new Button("Voltar");
        HBox hbVoltar = new HBox(10);
        hbVoltar.setAlignment(Pos.BOTTOM_LEFT);
        hbVoltar.getChildren().add(voltar);
        grid.add(hbVoltar, 0, 8);

        voltar.setOnAction(e -> {
            screen.setScene(this.main);
        });


        Button btn = new Button("Registar");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn);
        grid.add(hbBtn, 1, 8);

        final Text actiontarget = new Text();
        grid.add(actiontarget, 0, 8);
        grid.setColumnSpan(actiontarget, 2);
        grid.setHalignment(actiontarget, RIGHT);
        actiontarget.setId("actiontarget");

        btn.setOnAction(e -> {
            if(despesasGerais.isSelected())
                atividades_economicas.add(1);
            if(saude.isSelected())
                atividades_economicas.add(2);
            if(educacao.isSelected())
                atividades_economicas.add(3);
            if(habitacao.isSelected())
                atividades_economicas.add(4);
            if(lares.isSelected())
                atividades_economicas.add(5);
            if(repAutomoveis.isSelected())
                atividades_economicas.add(6);
            if(restAlojam.isSelected())
                atividades_economicas.add(7);
            if(cabeleireiros.isSelected())
                atividades_economicas.add(8);
            if(repMotociclos.isSelected())
                atividades_economicas.add(9);
            if(atiVeterinarias.isSelected())
                atividades_economicas.add(10);
            if(passesMensais.isSelected())
                atividades_economicas.add(11);

            try{
                int nifCC = Integer.parseInt(nifField.getText());
                String emailCC = emailField.getText();
                String nomeCC = nomeField.getText();
                String moradaCC = moradaField.getText();
                String pwCC = pwField.getText();

                try{
                    ContribuinteColetivo ccc = struct.getContribuinteColetivo(Integer.parseInt(nifField.getText()));

                    Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("Informação");
                    alert.setHeaderText(null);
                    alert.setContentText("Esse NIF já pertence a um contribuinte.");
                    alert.showAndWait();

                    loadColetivo();
                    screen.setScene(new_coletivo);
                }
                catch(CCNullException err){
                    try{
                        ContribuinteIndividual cic = struct.getContribuinteIndividual(nifCC);

                        Alert alert = new Alert(AlertType.INFORMATION);
                        alert.setTitle("Informação");
                        alert.setHeaderText(null);
                        alert.setContentText("Esse NIF já pertence a um contribuinte.");
                        alert.showAndWait();

                        loadColetivo();
                        screen.setScene(new_coletivo);
                    }
                    catch(CINullException err2){
                        if(!concelhosButton.getText().equals("Escolha o concelho") &&
                            atividades_economicas.size() != 0 &&
                            !emailCC.isEmpty() && !nomeCC.isEmpty() && !moradaCC.isEmpty() && !pwCC.isEmpty()){

                            Concelho umConcelho = struct.getUmConcelho(concelhosButton.getText());
                            double fator_deducao_fiscal = 0.0;

                            if(umConcelho != null) {
                                fator_deducao_fiscal = umConcelho.getBeneficio();
                            }

                            ContribuinteColetivo cc = new ContribuinteColetivo(
                                nifCC,
                                emailCC,
                                nomeCC,
                                moradaCC,
                                pwCC,
                                atividades_economicas,
                                fator_deducao_fiscal,
                                concelhosButton.getText(),
                                new ArrayList<>() //faturas
                            );

                            Alert alert = new Alert(AlertType.INFORMATION);
                            alert.setTitle("Informação");
                            alert.setHeaderText(null);
                            alert.setContentText("Contribuinte coletivo adicionado com sucesso.");
                            alert.showAndWait();

                            this.struct.add(cc);
                            screen.setScene(this.main);
                        }
                        else if(concelhosButton.getText().equals("Escolha o concelho")){
                            Alert alert = new Alert(AlertType.WARNING);
                            alert.setTitle("Aviso");
                            alert.setHeaderText(null);
                            alert.setContentText("Tens que inserir a que concelho pertence a sede da empresa.");
                            alert.showAndWait();
                        }
                        else if(atividades_economicas.size() == 0){
                            Alert alert = new Alert(AlertType.WARNING);
                            alert.setTitle("Aviso");
                            alert.setHeaderText(null);
                            alert.setContentText("Tens que inserir as atividades económicas onde a empresa se insere.");
                            alert.showAndWait();
                        }
                        else if(emailCC.isEmpty() || nomeCC.isEmpty() || moradaCC.isEmpty() || pwCC.isEmpty()){
                            Alert alert = new Alert(AlertType.WARNING);
                            alert.setTitle("Aviso");
                            alert.setHeaderText(null);
                            alert.setContentText("Tens que preencher todos os campos.");
                            alert.showAndWait();
                        }
                    }
                }
            }
            catch (NumberFormatException a) {
                Alert alert = new Alert(AlertType.WARNING);
                alert.setTitle("Aviso");
                alert.setHeaderText(null);
                alert.setContentText("O NIF tem que ser um número.");
                alert.showAndWait();
            }
        });

        this.new_coletivo = new Scene(grid, 300, 350);
    }

    private void loadNovoDependente() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));


        Text scenetitle = new Text("Associar Dependente");
        grid.add(scenetitle, 0, 0, 2, 1);

        Label nif = new Label("NIF do Dependente:");
        grid.add(nif, 0, 1);

        TextField nifField = new TextField();
        grid.add(nifField, 1, 1);


        Button voltar = new Button("Voltar");
        HBox hbVoltar = new HBox(10);
        hbVoltar.setAlignment(Pos.BOTTOM_LEFT);
        hbVoltar.getChildren().add(voltar);
        grid.add(hbVoltar, 0, 6);

        voltar.setOnAction(e -> {
            loadDashboardIndividual();
            screen.setScene(this.dashboard_individual);
        });


        Button btn = new Button("Registar");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn);
        grid.add(hbBtn, 1, 6);

        final Text actiontarget = new Text();
        grid.add(actiontarget, 0, 7);
        grid.setColumnSpan(actiontarget, 2);
        grid.setHalignment(actiontarget, RIGHT);
        actiontarget.setId("actiontarget");


        btn.setOnAction(e -> {
            try{
                int nif_dependente = Integer.parseInt(nifField.getText());
                ContribuinteIndividual ci_pai = struct.getContribuinteIndividual(logged_user_nif);
                ContribuinteIndividual ci_filho = struct.getContribuinteIndividual(nif_dependente);

                if (struct.existIndividual(nif_dependente)){
                    if ((ci_pai.pertenceDirecaoAgregado(logged_user_nif)) &&
                        (ci_filho.possoSerDependente()) ){
                        struct.addNovoDependenteAgregado(logged_user_nif, nif_dependente);


                        Alert alert = new Alert(AlertType.INFORMATION);
                        alert.setTitle("Informação");
                        alert.setHeaderText(null);
                        alert.setContentText("Dependente adicionado com sucesso.");
                        alert.showAndWait();
                    }
                    else if((!ci_pai.pertenceDirecaoAgregado(logged_user_nif))){
                        Alert alert = new Alert(AlertType.INFORMATION);
                        alert.setTitle("Informação");
                        alert.setHeaderText(null);
                        alert.setContentText("Não estás elegível para adicionar dependentes.");
                        alert.showAndWait();
                    }
                    else if(nif_dependente == logged_user_nif || (ci_pai.pertenceDependentesAgregado(nif_dependente))){
                        Alert alert = new Alert(AlertType.INFORMATION);
                        alert.setTitle("Informação");
                        alert.setHeaderText(null);
                        alert.setContentText("O contribuinte que estás a tentar adicionar já faz parte do agregado familiar.");
                        alert.showAndWait();
                    }
                    else{
                        Alert alert = new Alert(AlertType.INFORMATION);
                        alert.setTitle("Informação");
                        alert.setHeaderText(null);
                        alert.setContentText("O dependente que estás a tentar adicionar não é elegível para ser um dependente.");
                        alert.showAndWait();
                    }
                }
                else {
                    Alert alert = new Alert(AlertType.WARNING);
                    alert.setTitle("Aviso");
                    alert.setHeaderText(null);
                    alert.setContentText("NIF incorreto.");
                    alert.showAndWait();
                }

                loadNovoDependente();
                screen.setScene(dashboard_novoDependente);
            }

            catch(CINullException | NumberFormatException err){
                Alert alert = new Alert(AlertType.WARNING);
                alert.setTitle("Aviso");
                alert.setHeaderText(null);
                alert.setContentText("Não é possível efetuar essa ação.");
                alert.showAndWait();

                loadNovoDependente();
                screen.setScene(dashboard_novoDependente);
            }
        });

        this.dashboard_novoDependente = new Scene(grid, 300, 250);
    }

    private void loadConsultarFacturasIndividual() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(30, 30, 30, 30));

        TableView<Factura> table;

        TableColumn<Factura, Integer> numero = new TableColumn<>("Número da Factura");
        numero.setCellValueFactory(
                new PropertyValueFactory<>("id"));
        numero.setMinWidth(130);

        TableColumn<Factura, LocalDate> dataEmissao = new TableColumn<>("Data de Emissão");
        dataEmissao.setCellValueFactory(
                new PropertyValueFactory<>("data"));
        dataEmissao.setMinWidth(120);

        TableColumn<Factura, Integer> nifEmitente = new TableColumn<>("Nif do Emitente");
        nifEmitente.setCellValueFactory(
                new PropertyValueFactory<>("nif_emitente"));
        nifEmitente.setMinWidth(120);

        TableColumn<Factura, Integer> natureza = new TableColumn<>("Natureza");
        natureza.setCellValueFactory(
                new PropertyValueFactory<>("natureza"));
        natureza.setMinWidth(40);

        TableColumn<Factura, BigDecimal> valor = new TableColumn<>("Valor (€)");
        valor.setCellValueFactory(
                new PropertyValueFactory<>("valor"));
        valor.setMinWidth(30);

        TableColumn altera_cae = new TableColumn<>("Alterar CAE?");
        altera_cae.setMinWidth(100);


        Callback<TableColumn<Factura, String>, TableCell<Factura, String>> cellFactory
                = new Callback<TableColumn<Factura, String>, TableCell<Factura, String>>() {

            public TableCell call(final TableColumn<Factura, String> param) {
                final TableCell<Factura, String> cell = new TableCell<Factura, String>() {

                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);

                        if(!empty) {
                            Factura factura = getTableView().getItems().get(getIndex());
                            try{
                                boolean flag = struct.getContribuinteColetivo(factura.getNif_emitente()).podemAlterarCAEfaturas();
                                boolean cae0 = factura.getNatureza() == 0;

                                if(flag && !cae0){
                                    CheckBox cb_cae = new CheckBox();
                                    cb_cae.setIndeterminate(false);
                                    cb_cae.setAlignment(Pos.CENTER);

                                    cb_cae.setOnAction(new EventHandler<ActionEvent>() {
                                        public void handle(ActionEvent actionEvent) {

                                        if (cb_cae.isSelected()) {
                                            try{
                                                struct.eliminarCaeFatura(factura, logged_user_nif);
                                            }
                                            catch(CINullException err){
                                                Alert alert = new Alert(AlertType.WARNING);
                                                alert.setTitle("Aviso");
                                                alert.setHeaderText(null);
                                                alert.setContentText("Algo correu mal.");
                                                alert.showAndWait();

                                                loadConsultarFacturasIndividual();
                                                screen.setScene(dashboard_consultafacturas_individual);
                                            }
                                            catch(CCNullException err){
                                                Alert alert = new Alert(AlertType.WARNING);
                                                alert.setTitle("Aviso");
                                                alert.setHeaderText(null);
                                                alert.setContentText("Algo correu mal.");
                                                alert.showAndWait();

                                                loadConsultarFacturasIndividual();
                                                screen.setScene(dashboard_consultafacturas_individual);
                                            }
                                        }

                                        loadConsultarFacturasIndividual();
                                        screen.setScene(dashboard_consultafacturas_individual);
                                        }
                                    });
                                    setGraphic(cb_cae);
                                    setText(null);
                                }
                                else {
                                    setGraphic(null);
                                    setText(null);
                                }
                            }
                            catch(CCNullException err){
                                Alert alert = new Alert(AlertType.WARNING);
                                alert.setTitle("Aviso");
                                alert.setHeaderText(null);
                                alert.setContentText("Algo correu mal.");
                                alert.showAndWait();

                                loadDashboardIndividual();
                                screen.setScene(dashboard_individual);
                            }
                        }
                        else {
                            setGraphic(null);
                            setText(null);
                        }
                    }
                };
                return cell;
            }
        };

        altera_cae.setCellFactory(cellFactory);

        ObservableList<Factura> data = FXCollections.observableArrayList();

        for(Factura factura: struct.getFacturasColetivosOuIndividual(logged_user_nif))
            data.add(factura);


        table = new TableView<>();
        table.setItems(data);
        table.getColumns().addAll(numero, dataEmissao, nifEmitente, natureza, valor, altera_cae);

        grid.getChildren().addAll(table);

        table = new TableView<>();
        table.setItems(data);
        table.getColumns().addAll(numero, dataEmissao, nifEmitente, natureza, valor, altera_cae);

        grid.getChildren().addAll(table);

        TextField nome = new TextField();
        nome.setPromptText("Nome Ficheiro");
        grid.add(nome, 2, 7);

        TextField id = new TextField();
        id.setPromptText("Id da Factura");
        grid.add(id, 3, 7);

        Button imprime = new Button("Imprime Factura");
        HBox hbimprime = new HBox(10);
        hbimprime.setAlignment(Pos.BOTTOM_RIGHT);
        hbimprime.getChildren().add(imprime);
        grid.add(hbimprime, 4, 7);

        imprime.setOnAction(e -> {
            try{
                int fid = Integer.parseInt(id.getText());
                String name = nome.getText();
                if (name.equals(""))
                    name = "factura" + fid;
                struct.imprimeFacturaIndividual(name, fid, this.logged_user_nif);

                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Sucesso");
                alert.setHeaderText(null);
                alert.setContentText("A sua fatura está diponível em " + name + ".html");
                alert.showAndWait();
            }
            catch (FacturaNaoExisteException a) {
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Erro");
                alert.setHeaderText(null);
                alert.setContentText(a.getMessage());
                alert.showAndWait();
            }
            catch (Exception a) {
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Erro");
                alert.setHeaderText(null);
                alert.setContentText("Erro a guardar ficheiro.");
                alert.showAndWait();
            }
        });

        Button voltar = new Button("Voltar");
        HBox hbVoltar = new HBox(10);
        hbVoltar.setAlignment(Pos.BOTTOM_LEFT);
        hbVoltar.getChildren().add(voltar);
        grid.add(hbVoltar, 6, 9);

        voltar.setOnAction(e -> {
            loadDashboardIndividual();
            screen.setScene(this.dashboard_individual);
        });


        this.dashboard_consultafacturas_individual = new Scene(grid, 1150, 800);
    }

    private void loadConsultarFacturasColetivoORDDat(int nif, String data_inicial, String data_final) {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.TOP_CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(30, 30, 30, 30));

        TableView<Factura> table;

        TableColumn<Factura, Integer> numero = new TableColumn<>("Número da Factura");
        numero.setCellValueFactory(
                new PropertyValueFactory<>("id"));
        numero.setMinWidth(130);

        TableColumn<Factura, LocalDate> dataEmissao = new TableColumn<>("Data de Emissão");
        dataEmissao.setCellValueFactory(
                new PropertyValueFactory<>("data"));
        dataEmissao.setMinWidth(120);

        TableColumn<Factura, Integer> nifConsumidor = new TableColumn<>("Nif do Consumidor");
        nifConsumidor.setCellValueFactory(
                new PropertyValueFactory<>("nif_cliente"));
        nifConsumidor.setMinWidth(130);

        TableColumn<Factura, BigDecimal> natureza = new TableColumn<>("Natureza");
        natureza.setCellValueFactory(
                new PropertyValueFactory<>("natureza"));
        natureza.setMinWidth(40);

        TableColumn<Factura, BigDecimal> valor = new TableColumn<>("Valor (€)");
        valor.setCellValueFactory(
                new PropertyValueFactory<>("valor"));
        valor.setMinWidth(30);

        ObservableList<Factura> data = FXCollections.observableArrayList();

        LocalDate de = LocalDate.MIN;
        if(!data_inicial.equals(""))
            de = LocalDate.parse(data_inicial);

        LocalDate ate = LocalDate.MAX;
        if(!data_final.equals(""))
            ate = LocalDate.parse(data_final);


        Button ordvalor = new Button("Ordenar todas por Valor");
        ordvalor.setTextFill(Color.web("#0076a3"));
        HBox hbOrdvalor = new HBox(10);
        hbOrdvalor.setAlignment(Pos.BOTTOM_RIGHT);
        hbOrdvalor.getChildren().add(ordvalor);
        grid.add(hbOrdvalor, 2, 6);

        ordvalor.setOnAction(e -> {
            loadConsultarFacturasColetivoORDVAL(0, "", "");
            screen.setScene(dashboard_consultafacturas_coletivoORDValor);
        });

        Button orddata = new Button("Ordenar todas por Data");
        orddata.setTextFill(Color.web("#0076a3"));
        HBox hbOrddata= new HBox(10);
        hbOrddata.setAlignment(Pos.BOTTOM_RIGHT);
        hbOrddata.getChildren().add(orddata);
        grid.add(hbOrddata, 3, 6);

        orddata.setOnAction(e -> {
            loadConsultarFacturasColetivoORDDat(0, "", "");
            screen.setScene(dashboard_consultafacturas_coletivoORDData);
        });

        for(Factura factura: struct.ordenaFaturasData(logged_user_nif))
            data.add(factura);

        table = new TableView<>();
        table.setItems(data);
        table.getColumns().addAll(numero, dataEmissao, nifConsumidor, natureza, valor);

        grid.getChildren().addAll(table);

        final DatePicker dataField1 = new DatePicker();
        dataField1.setPromptText("Data inicial");
        grid.add(dataField1, 1, 7);

        final DatePicker dataField2 = new DatePicker();
        dataField2.setPromptText("Data final");
        grid.add(dataField2, 2, 7);

        TextField nifField = new TextField();
        nifField.setPromptText("NIF");
        grid.add(nifField, 3, 7);

        Button filtrarD = new Button("Filtrar por data");
        HBox hbFiltrarD = new HBox(10);
        hbFiltrarD.setAlignment(Pos.BOTTOM_RIGHT);
        hbFiltrarD.getChildren().add(filtrarD);
        grid.add(hbFiltrarD, 4, 7);

        filtrarD.setOnAction(e -> {
            int n;
            String i, f;
            try{
                n = Integer.parseInt(nifField.getText());
            }
            catch(NumberFormatException a) {
                n = 0;
            }
            try{
                i = dataField1.getValue().toString();
            }
            catch(NullPointerException b ) {
                i = "";
            }
            try{
                f = dataField2.getValue().toString();
            }
            catch(NullPointerException c) {
                f = "";
            }
            loadConsultarFacturasColetivo(n, i, f);
            screen.setScene(dashboard_consultafacturas_coletivo);
        });

        TextField nifField2 = new TextField();
        nifField2.setPromptText("NIF");
        grid.add(nifField2, 3, 8);

        Button filtrarV = new Button("Filtrar por valor");
        HBox hbFiltrarV = new HBox(10);
        hbFiltrarV.setAlignment(Pos.BOTTOM_RIGHT);
        hbFiltrarV.getChildren().add(filtrarV);
        grid.add(hbFiltrarV, 4, 8);

        filtrarV.setOnAction(e -> {
            try{
                int n = Integer.parseInt(nifField2.getText());
                loadConsultarFacturasColetivoCIValor(n, "", "");
                screen.setScene(dashboard_consultafacturas_coletivoCIValor);
            }
            catch (NumberFormatException a) {
                int n = 0;
                loadConsultarFacturasColetivoCIValor(n, "", "");
                screen.setScene(dashboard_consultafacturas_coletivoCIValor);
            }            try{
                int n = Integer.parseInt(nifField2.getText());
                loadConsultarFacturasColetivoCIValor(n, "", "");
                screen.setScene(dashboard_consultafacturas_coletivoCIValor);
            }
            catch (NumberFormatException a) {
                int n = 0;
                loadConsultarFacturasColetivoCIValor(n, "", "");
                screen.setScene(dashboard_consultafacturas_coletivoCIValor);
            }
        });

        Button limpar = new Button("Limpar");
        HBox hbLimpar = new HBox(10);
        hbLimpar.setAlignment(Pos.BOTTOM_RIGHT);
        hbLimpar.getChildren().add(limpar);
        grid.add(hbLimpar, 4, 9);

        limpar.setOnAction(e -> {
            loadConsultarFacturasColetivo(0, "", "");
            screen.setScene(dashboard_consultafacturas_coletivo);
        });

        TextField nome = new TextField();
        nome.setPromptText("Nome Ficheiro");
        grid.add(nome, 2, 11);

        TextField id = new TextField();
        id.setPromptText("Id da Factura");
        grid.add(id, 3, 11);

        Button imprime = new Button("Imprime Factura");
        HBox hbimprime = new HBox(10);
        hbimprime.setAlignment(Pos.BOTTOM_RIGHT);
        hbimprime.getChildren().add(imprime);
        grid.add(hbimprime, 4, 11);

        imprime.setOnAction(e -> {
            try{
                int fid = Integer.parseInt(id.getText());
                String name = nome.getText();
                if (name.equals(""))
                    name = "factura" + fid;
                struct.imprimeFacturaColetivo(name, fid, this.logged_user_nif);

                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Sucesso");
                alert.setHeaderText(null);
                alert.setContentText("A sua fatura está diponível em " + name + ".html");
                alert.showAndWait();
            }
            catch (FacturaNaoExisteException a) {
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Erro");
                alert.setHeaderText(null);
                alert.setContentText(a.getMessage());
                alert.showAndWait();
            }
            catch (Exception a){
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Erro");
                alert.setHeaderText(null);
                alert.setContentText("Erro a guardar ficheiro.");
                alert.showAndWait();
            }
        });

        Button voltar = new Button("Voltar");
        HBox hbVoltar = new HBox(10);
        hbVoltar.setAlignment(Pos.BOTTOM_RIGHT);
        hbVoltar.getChildren().add(voltar);
        grid.add(hbVoltar, 4, 15);

        voltar.setOnAction(e -> {
            screen.setScene(this.dashboard_coletivo);
        });

        this.dashboard_consultafacturas_coletivoORDData = new Scene(grid, 1300, 800);
    }

    private void loadConsultarFacturasColetivoORDVAL(int nif, String data_inicial, String data_final) {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.TOP_CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(30, 30, 30, 30));

        TableView<Factura> table;

        TableColumn<Factura, Integer> numero = new TableColumn<>("Número da Factura");
        numero.setCellValueFactory(
                new PropertyValueFactory<>("id"));
        numero.setMinWidth(130);

        TableColumn<Factura, LocalDate> dataEmissao = new TableColumn<>("Data de Emissão");
        dataEmissao.setCellValueFactory(
                new PropertyValueFactory<>("data"));
        dataEmissao.setMinWidth(120);

        TableColumn<Factura, Integer> nifConsumidor = new TableColumn<>("Nif do Consumidor");
        nifConsumidor.setCellValueFactory(
                new PropertyValueFactory<>("nif_cliente"));
        nifConsumidor.setMinWidth(130);

        TableColumn<Factura, BigDecimal> natureza = new TableColumn<>("Natureza");
        natureza.setCellValueFactory(
                new PropertyValueFactory<>("natureza"));
        natureza.setMinWidth(40);

        TableColumn<Factura, BigDecimal> valor = new TableColumn<>("Valor (€)");
        valor.setCellValueFactory(
                new PropertyValueFactory<>("valor"));
        valor.setMinWidth(30);

        ObservableList<Factura> data = FXCollections.observableArrayList();

        LocalDate de = LocalDate.MIN;
        if(!data_inicial.equals(""))
            de = LocalDate.parse(data_inicial);

        LocalDate ate = LocalDate.MAX;
        if(!data_final.equals(""))
            ate = LocalDate.parse(data_final);


        Button ordvalor = new Button("Ordenar todas por Valor");
        ordvalor.setTextFill(Color.web("#0076a3"));
        HBox hbOrdvalor = new HBox(10);
        hbOrdvalor.setAlignment(Pos.BOTTOM_RIGHT);
        hbOrdvalor.getChildren().add(ordvalor);
        grid.add(hbOrdvalor, 2, 6);

        ordvalor.setOnAction(e -> {
            loadConsultarFacturasColetivoORDVAL(0, "", "");
            screen.setScene(dashboard_consultafacturas_coletivoORDValor);
        });

        Button orddata = new Button("Ordenar todas por Data");
        orddata.setTextFill(Color.web("#0076a3"));
        HBox hbOrddata= new HBox(10);
        hbOrddata.setAlignment(Pos.BOTTOM_RIGHT);
        hbOrddata.getChildren().add(orddata);
        grid.add(hbOrddata, 3, 6);

        orddata.setOnAction(e -> {
            loadConsultarFacturasColetivoORDDat(0, "", "");
            screen.setScene(dashboard_consultafacturas_coletivoORDData);
        });

        for(Factura factura: struct.ordenaFaturasValor(logged_user_nif))
            data.add(factura);

        table = new TableView<>();
        table.setItems(data);
        table.getColumns().addAll(numero, dataEmissao, nifConsumidor, natureza, valor);

        grid.getChildren().addAll(table);

        final DatePicker dataField1 = new DatePicker();
        dataField1.setPromptText("Data inicial");
        grid.add(dataField1, 1, 7);

        final DatePicker dataField2 = new DatePicker();
        dataField2.setPromptText("Data final");
        grid.add(dataField2, 2, 7);

        TextField nifField = new TextField();
        nifField.setPromptText("NIF");
        grid.add(nifField, 3, 7);

        Button filtrarD = new Button("Filtrar por data");
        HBox hbFiltrarD = new HBox(10);
        hbFiltrarD.setAlignment(Pos.BOTTOM_RIGHT);
        hbFiltrarD.getChildren().add(filtrarD);
        grid.add(hbFiltrarD, 4, 7);

        filtrarD.setOnAction(e -> {
            int n;
            String i, f;
            try{
                n = Integer.parseInt(nifField.getText());
            }
            catch (NumberFormatException a) {
                n = 0;
            }
            try{
                i = dataField1.getValue().toString();
            }
            catch (NullPointerException b ) {
                i = "";
            }
            try{
                f = dataField2.getValue().toString();
            }
            catch (NullPointerException c) {
                f = "";
            }
            loadConsultarFacturasColetivo(n, i, f);
            screen.setScene(dashboard_consultafacturas_coletivo);
        });

        TextField nifField2 = new TextField();
        nifField2.setPromptText("NIF");
        grid.add(nifField2, 3, 8);

        Button filtrarV = new Button("Filtrar por valor");
        HBox hbFiltrarV = new HBox(10);
        hbFiltrarV.setAlignment(Pos.BOTTOM_RIGHT);
        hbFiltrarV.getChildren().add(filtrarV);
        grid.add(hbFiltrarV, 4, 8);

        filtrarV.setOnAction(e -> {
            try{
                int n = Integer.parseInt(nifField2.getText());
                loadConsultarFacturasColetivoCIValor(n, "", "");
                screen.setScene(dashboard_consultafacturas_coletivoCIValor);
            }
            catch (NumberFormatException a) {
                int n = 0;
                loadConsultarFacturasColetivoCIValor(n, "", "");
                screen.setScene(dashboard_consultafacturas_coletivoCIValor);
            }
        });

        Button limpar = new Button("Limpar");
        HBox hbLimpar = new HBox(10);
        hbLimpar.setAlignment(Pos.BOTTOM_RIGHT);
        hbLimpar.getChildren().add(limpar);
        grid.add(hbLimpar, 4, 9);

        limpar.setOnAction(e -> {
            loadConsultarFacturasColetivo(0, "", "");
            screen.setScene(dashboard_consultafacturas_coletivo);
        });

        TextField nome = new TextField();
        nome.setPromptText("Nome Ficheiro");
        grid.add(nome, 2, 11);

        TextField id = new TextField();
        id.setPromptText("Id da Factura");
        grid.add(id, 3, 11);

        Button imprime = new Button("Imprime Factura");
        HBox hbimprime = new HBox(10);
        hbimprime.setAlignment(Pos.BOTTOM_RIGHT);
        hbimprime.getChildren().add(imprime);
        grid.add(hbimprime, 4, 11);

        imprime.setOnAction(e -> {
            try{
                int fid = Integer.parseInt(id.getText());
                String name = nome.getText();
                if (name.equals(""))
                    name = "factura" + fid;
                struct.imprimeFacturaColetivo(name, fid, this.logged_user_nif);

                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Sucesso");
                alert.setHeaderText(null);
                alert.setContentText("A sua fatura está diponível em " + name + ".html");
                alert.showAndWait();
            }
            catch (FacturaNaoExisteException a) {
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Erro");
                alert.setHeaderText(null);
                alert.setContentText(a.getMessage());
                alert.showAndWait();
            }
            catch (Exception a) {
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Erro");
                alert.setHeaderText(null);
                alert.setContentText("Erro a guardar ficheiro.");
                alert.showAndWait();
            }
        });

        Button voltar = new Button("Voltar");
        HBox hbVoltar = new HBox(10);
        hbVoltar.setAlignment(Pos.BOTTOM_RIGHT);
        hbVoltar.getChildren().add(voltar);
        grid.add(hbVoltar, 4, 15);

        voltar.setOnAction(e -> {
            screen.setScene(this.dashboard_coletivo);
        });

        this.dashboard_consultafacturas_coletivoORDValor = new Scene(grid, 1300, 800);
    }

    private void loadConsultarFacturasColetivoCIValor(int nif, String data_inicial, String data_final) {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.TOP_CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(30, 30, 30, 30));

        TableView<Factura> table;

        TableColumn<Factura, Integer> numero = new TableColumn<>("Número da Factura");
        numero.setCellValueFactory(
                new PropertyValueFactory<>("id"));
        numero.setMinWidth(130);

        TableColumn<Factura, LocalDate> dataEmissao = new TableColumn<>("Data de Emissão");
        dataEmissao.setCellValueFactory(
                new PropertyValueFactory<>("data"));
        dataEmissao.setMinWidth(120);

        TableColumn<Factura, Integer> nifConsumidor = new TableColumn<>("Nif do Consumidor");
        nifConsumidor.setCellValueFactory(
                new PropertyValueFactory<>("nif_cliente"));
        nifConsumidor.setMinWidth(130);

        TableColumn<Factura, BigDecimal> natureza = new TableColumn<>("Natureza");
        natureza.setCellValueFactory(
                new PropertyValueFactory<>("natureza"));
        natureza.setMinWidth(40);

        TableColumn<Factura, BigDecimal> valor = new TableColumn<>("Valor (€)");
        valor.setCellValueFactory(
                new PropertyValueFactory<>("valor"));
        valor.setMinWidth(30);

        ObservableList<Factura> data = FXCollections.observableArrayList();

        LocalDate de = LocalDate.MIN;
        if(!data_inicial.equals(""))
            de = LocalDate.parse(data_inicial);

        LocalDate ate = LocalDate.MAX;
        if(!data_final.equals(""))
            ate = LocalDate.parse(data_final);


        Button ordvalor = new Button("Ordenar todas por Valor");
        ordvalor.setTextFill(Color.web("#0076a3"));
        HBox hbOrdvalor = new HBox(10);
        hbOrdvalor.setAlignment(Pos.BOTTOM_RIGHT);
        hbOrdvalor.getChildren().add(ordvalor);
        grid.add(hbOrdvalor, 2, 6);

        ordvalor.setOnAction(e -> {
            loadConsultarFacturasColetivoORDVAL(0, "", "");
            screen.setScene(dashboard_consultafacturas_coletivoORDValor);
        });

        Button orddata = new Button("Ordenar todas por Data");
        orddata.setTextFill(Color.web("#0076a3"));
        HBox hbOrddata= new HBox(10);
        hbOrddata.setAlignment(Pos.BOTTOM_RIGHT);
        hbOrddata.getChildren().add(orddata);
        grid.add(hbOrddata, 3, 6);

        orddata.setOnAction(e -> {
            loadConsultarFacturasColetivoORDDat(0, "", "");
            screen.setScene(dashboard_consultafacturas_coletivoORDData);
        });


        for(Factura factura: struct.faturasValorContribuinte(logged_user_nif, nif)){
          data.add(factura);
        }


        table = new TableView<>();
        table.setItems(data);
        table.getColumns().addAll(numero, dataEmissao, nifConsumidor, natureza, valor);

        grid.getChildren().addAll(table);

        final DatePicker dataField1 = new DatePicker();
        dataField1.setPromptText("Data inicial");
        grid.add(dataField1, 1, 7);

        final DatePicker dataField2 = new DatePicker();
        dataField2.setPromptText("Data final");
        grid.add(dataField2, 2, 7);

        TextField nifField = new TextField();
        nifField.setPromptText("NIF");
        grid.add(nifField, 3, 7);

        Button filtrarD = new Button("Filtrar por data");
        HBox hbFiltrarD = new HBox(10);
        hbFiltrarD.setAlignment(Pos.BOTTOM_RIGHT);
        hbFiltrarD.getChildren().add(filtrarD);
        grid.add(hbFiltrarD, 4, 7);

        filtrarD.setOnAction(e -> {
            int n;
            String i, f;
            try{
                n = Integer.parseInt(nifField.getText());
            }
            catch (NumberFormatException a) {
                n = 0;
            }
            try{
                i = dataField1.getValue().toString();
            }
            catch (NullPointerException b ) {
                i = "";
            }
            try{
                f = dataField2.getValue().toString();
            }
            catch (NullPointerException c) {
                f = "";
            }
            loadConsultarFacturasColetivo(n, i, f);
            screen.setScene(dashboard_consultafacturas_coletivo);
        });

        TextField nifField2 = new TextField();
        nifField2.setPromptText("NIF");
        grid.add(nifField2, 3, 8);

        Button filtrarV = new Button("Filtrar por valor");
        HBox hbFiltrarV = new HBox(10);
        hbFiltrarV.setAlignment(Pos.BOTTOM_RIGHT);
        hbFiltrarV.getChildren().add(filtrarV);
        grid.add(hbFiltrarV, 4, 8);

        filtrarV.setOnAction(e -> {
            try{
                int n = Integer.parseInt(nifField2.getText());
                loadConsultarFacturasColetivoCIValor(n, "", "");
                screen.setScene(dashboard_consultafacturas_coletivoCIValor);
            }
            catch (NumberFormatException a) {
                int n = 0;
                loadConsultarFacturasColetivoCIValor(n, "", "");
                screen.setScene(dashboard_consultafacturas_coletivoCIValor);
            }
        });

        Button limpar = new Button("Limpar");
        HBox hbLimpar = new HBox(10);
        hbLimpar.setAlignment(Pos.BOTTOM_RIGHT);
        hbLimpar.getChildren().add(limpar);
        grid.add(hbLimpar, 4, 9);

        limpar.setOnAction(e -> {
            loadConsultarFacturasColetivo(0, "", "");
            screen.setScene(dashboard_consultafacturas_coletivo);
        });

        TextField nome = new TextField();
        nome.setPromptText("Nome Ficheiro");
        grid.add(nome, 2, 11);

        TextField id = new TextField();
        id.setPromptText("Id da Factura");
        grid.add(id, 3, 11);

        Button imprime = new Button("Imprime Factura");
        HBox hbimprime = new HBox(10);
        hbimprime.setAlignment(Pos.BOTTOM_RIGHT);
        hbimprime.getChildren().add(imprime);
        grid.add(hbimprime, 4, 11);

        imprime.setOnAction(e -> {
            try{
                int fid = Integer.parseInt(id.getText());
                String name = nome.getText();
                if (name.equals(""))
                    name = "factura" + fid;
                struct.imprimeFacturaColetivo(name, fid, this.logged_user_nif);

                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Sucesso");
                alert.setHeaderText(null);
                alert.setContentText("A sua fatura está diponível em " + name + ".html");
                alert.showAndWait();
            }
            catch (FacturaNaoExisteException a) {
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Erro");
                alert.setHeaderText(null);
                alert.setContentText(a.getMessage());
                alert.showAndWait();
            }
            catch (Exception a) {
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Erro");
                alert.setHeaderText(null);
                alert.setContentText("Erro a guardar ficheiro.");
                alert.showAndWait();
            }
        });

        Button voltar = new Button("Voltar");
        HBox hbVoltar = new HBox(10);
        hbVoltar.setAlignment(Pos.BOTTOM_RIGHT);
        hbVoltar.getChildren().add(voltar);
        grid.add(hbVoltar, 4, 15);

        voltar.setOnAction(e -> {
            screen.setScene(this.dashboard_coletivo);
        });


        this.dashboard_consultafacturas_coletivoCIValor = new Scene(grid, 1300, 800);
    }

    private void loadConsultarFacturasColetivo(int nif, String data_inicial, String data_final) {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.TOP_CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(30, 30, 30, 30));

        TableView<Factura> table;

        TableColumn<Factura, Integer> numero = new TableColumn<>("Número da Factura");
        numero.setCellValueFactory(
                new PropertyValueFactory<>("id"));
        numero.setMinWidth(130);

        TableColumn<Factura, LocalDate> dataEmissao = new TableColumn<>("Data de Emissão");
        dataEmissao.setCellValueFactory(
                new PropertyValueFactory<>("data"));
        dataEmissao.setMinWidth(120);

        TableColumn<Factura, Integer> nifConsumidor = new TableColumn<>("Nif do Consumidor");
        nifConsumidor.setCellValueFactory(
                new PropertyValueFactory<>("nif_cliente"));
        nifConsumidor.setMinWidth(130);

        TableColumn<Factura, BigDecimal> natureza = new TableColumn<>("Natureza");
        natureza.setCellValueFactory(
                new PropertyValueFactory<>("natureza"));
        natureza.setMinWidth(40);

        TableColumn<Factura, BigDecimal> valor = new TableColumn<>("Valor (€)");
        valor.setCellValueFactory(
                new PropertyValueFactory<>("valor"));
        valor.setMinWidth(30);

        ObservableList<Factura> data = FXCollections.observableArrayList();

        LocalDate de = LocalDate.MIN;
        if(!data_inicial.equals(""))
            de = LocalDate.parse(data_inicial);

        LocalDate ate = LocalDate.MAX;
        if(!data_final.equals(""))
            ate = LocalDate.parse(data_final);


        Button ordvalor = new Button("Ordenar todas por Valor");
        ordvalor.setTextFill(Color.web("#0076a3"));
        HBox hbOrdvalor = new HBox(10);
        hbOrdvalor.setAlignment(Pos.BOTTOM_RIGHT);
        hbOrdvalor.getChildren().add(ordvalor);
        grid.add(hbOrdvalor, 2, 6);

        ordvalor.setOnAction(e -> {
            loadConsultarFacturasColetivoORDVAL(0, "", "");
            screen.setScene(dashboard_consultafacturas_coletivoORDValor);
        });

        Button orddata = new Button("Ordenar todas por Data");
        orddata.setTextFill(Color.web("#0076a3"));
        HBox hbOrddata= new HBox(10);
        hbOrddata.setAlignment(Pos.BOTTOM_RIGHT);
        hbOrddata.getChildren().add(orddata);
        grid.add(hbOrddata, 3, 6);

        orddata.setOnAction(e -> {
            loadConsultarFacturasColetivoORDDat(0, "", "");
            screen.setScene(dashboard_consultafacturas_coletivoORDData);
        });


        for(Factura factura: struct.faturasPeriodoContribuinte(logged_user_nif, nif, de, ate)){
          data.add(factura);
        }


        table = new TableView<>();
        table.setItems(data);
        table.getColumns().addAll(numero, dataEmissao, nifConsumidor, natureza, valor);

        grid.getChildren().addAll(table);

        final DatePicker dataField1 = new DatePicker();
        dataField1.setPromptText("Data inicial");
        grid.add(dataField1, 1, 7);

        final DatePicker dataField2 = new DatePicker();
        dataField2.setPromptText("Data final");
        grid.add(dataField2, 2, 7);

        TextField nifField = new TextField();
        nifField.setPromptText("NIF");
        grid.add(nifField, 3, 7);

        Button filtrarD = new Button("Filtrar por data");
        HBox hbFiltrarD = new HBox(10);
        hbFiltrarD.setAlignment(Pos.BOTTOM_RIGHT);
        hbFiltrarD.getChildren().add(filtrarD);
        grid.add(hbFiltrarD, 4, 7);

        filtrarD.setOnAction(e -> {
            int n;
            String i, f;
            try{
                n = Integer.parseInt(nifField.getText());
            }
            catch (NumberFormatException a) {
                n = 0;
            }
            try{
                i = dataField1.getValue().toString();
            }
            catch (NullPointerException b ) {
                i = "";
            }
            try{
                f = dataField2.getValue().toString();
            }
            catch (NullPointerException c) {
                f = "";
            }
            loadConsultarFacturasColetivo(n, i, f);
            screen.setScene(dashboard_consultafacturas_coletivo);
        });

        TextField nifField2 = new TextField();
        nifField2.setPromptText("NIF");
        grid.add(nifField2, 3, 8);

        Button filtrarV = new Button("Filtrar por valor");
        HBox hbFiltrarV = new HBox(10);
        hbFiltrarV.setAlignment(Pos.BOTTOM_RIGHT);
        hbFiltrarV.getChildren().add(filtrarV);
        grid.add(hbFiltrarV, 4, 8);

        filtrarV.setOnAction(e -> {
            try{
                int n = Integer.parseInt(nifField2.getText());
                loadConsultarFacturasColetivoCIValor(n, "", "");
                screen.setScene(dashboard_consultafacturas_coletivoCIValor);
            }
            catch (NumberFormatException a) {
                int n = 0;
                loadConsultarFacturasColetivoCIValor(n, "", "");
                screen.setScene(dashboard_consultafacturas_coletivoCIValor);
            }
        });

        Button limpar = new Button("Limpar");
        HBox hbLimpar = new HBox(10);
        hbLimpar.setAlignment(Pos.BOTTOM_RIGHT);
        hbLimpar.getChildren().add(limpar);
        grid.add(hbLimpar, 4, 9);

        limpar.setOnAction(e -> {
            loadConsultarFacturasColetivo(0, "", "");
            screen.setScene(dashboard_consultafacturas_coletivo);
        });

        TextField nome = new TextField();
        nome.setPromptText("Nome Ficheiro");
        grid.add(nome, 2, 11);

        TextField id = new TextField();
        id.setPromptText("Id da Factura");
        grid.add(id, 3, 11);

        Button imprime = new Button("Imprime Factura");
        HBox hbimprime = new HBox(10);
        hbimprime.setAlignment(Pos.BOTTOM_RIGHT);
        hbimprime.getChildren().add(imprime);
        grid.add(hbimprime, 4, 11);

        imprime.setOnAction(e -> {
            try{
                 int fid = Integer.parseInt(id.getText());
                 String name = nome.getText();
                 if (name.equals(""))
                     name = "factura" + fid;
                 struct.imprimeFacturaColetivo(name, fid, this.logged_user_nif);

                 Alert alert = new Alert(AlertType.INFORMATION);
                 alert.setTitle("Sucesso");
                 alert.setHeaderText(null);
                 alert.setContentText("A sua fatura está diponível em " + name + ".html");
                 alert.showAndWait();
            }
            catch (FacturaNaoExisteException a) {
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Erro");
                alert.setHeaderText(null);
                alert.setContentText(a.getMessage());
                alert.showAndWait();
            }
            catch (Exception a) {
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Erro");
                alert.setHeaderText(null);
                alert.setContentText("Erro a guardar ficheiro.");
                alert.showAndWait();
            }
        });

        Button voltar = new Button("Voltar");
        HBox hbVoltar = new HBox(10);
        hbVoltar.setAlignment(Pos.BOTTOM_RIGHT);
        hbVoltar.getChildren().add(voltar);
        grid.add(hbVoltar, 4, 15);

        voltar.setOnAction(e -> {
            screen.setScene(this.dashboard_coletivo);
        });


        this.dashboard_consultafacturas_coletivo = new Scene(grid, 1300, 800);
    }

    private void loadCAEFacturas() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.TOP_CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(30, 30, 30, 30));

        TableView<Factura> table;

        TableColumn<Factura, Integer> numero = new TableColumn<>("Número da Factura");
        numero.setCellValueFactory(
                new PropertyValueFactory<>("id"));
        numero.setMinWidth(140);

        TableColumn<Factura, LocalDate> dataEmissao = new TableColumn<>("Data de Emissão");
        dataEmissao.setCellValueFactory(
                new PropertyValueFactory<>("data"));
        dataEmissao.setMinWidth(130);

        TableColumn<Factura, BigDecimal> valor = new TableColumn<>("Valor");
        valor.setCellValueFactory(
                new PropertyValueFactory<>("valor"));
        valor.setMinWidth(40);

        TableColumn natureza = new TableColumn("Natureza");
        natureza.setMinWidth(330);

        Callback<TableColumn<Factura, String>, TableCell<Factura, String>> cellFactory
                = new Callback<TableColumn<Factura, String>, TableCell<Factura, String>>() {

            public TableCell call(final TableColumn<Factura, String> param) {
                final TableCell<Factura, String> cell = new TableCell<Factura, String>() {

                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            ObservableList<MenuItem> setores = FXCollections.observableArrayList();

                            HBox hbox = new HBox();

                            Factura factura = getTableView().getItems().get(getIndex());

                            MenuButton naturezaButton = new MenuButton("Desconhecido");

                            MenuItem despesasGerais = new MenuItem("Despesas Gerais e Familiares");
                            MenuItem saude = new MenuItem("Saúde");
                            MenuItem educacao = new MenuItem("Educação");
                            MenuItem habitacao = new MenuItem("Habitação");
                            MenuItem lares = new MenuItem("Lares");
                            MenuItem repAutomoveis = new MenuItem("Reparação de Automóveis");
                            MenuItem restAlojam = new MenuItem("Restauração e Alojamento");
                            MenuItem cabeleireiros = new MenuItem("Cabeleireiros");
                            MenuItem repMotociclos = new MenuItem("Reparação de Motociclos");
                            MenuItem atiVeterinarias = new MenuItem("Atividades Veterinárias");
                            MenuItem passesMensais = new MenuItem("Passes Mensais");

                            try{
                              List<Integer> atividades_economicas = struct.getContribuinteColetivo(factura.getNif_emitente()).getAtividades_economicas();

                              if(atividades_economicas.contains(1))
                                setores.addAll(despesasGerais);

                              if(atividades_economicas.contains(2))
                                setores.addAll(saude);

                              if(atividades_economicas.contains(3))
                                setores.addAll(educacao);

                              if(atividades_economicas.contains(4))
                                setores.addAll(habitacao);

                              if(atividades_economicas.contains(5))
                                setores.addAll(lares);

                              if(atividades_economicas.contains(6))
                                setores.addAll(repAutomoveis);

                              if(atividades_economicas.contains(7))
                                setores.addAll(restAlojam);

                              if(atividades_economicas.contains(8))
                                setores.addAll(cabeleireiros);

                              if(atividades_economicas.contains(9))
                                setores.addAll(repMotociclos);

                              if(atividades_economicas.contains(10))
                                setores.addAll(atiVeterinarias);

                              if(atividades_economicas.contains(11))
                                setores.addAll(passesMensais);

                              naturezaButton.getItems().addAll(setores);


                              Button submeter = new Button("Submeter");

                              hbox.getChildren().addAll(naturezaButton, submeter);


                              despesasGerais.setOnAction(new EventHandler<ActionEvent>() {
                                public void handle(ActionEvent actionEvent) {
                                  naturezaButton.setText(despesasGerais.getText());
                                }
                              });

                              saude.setOnAction(new EventHandler<ActionEvent>() {
                                public void handle(ActionEvent actionEvent) {
                                  naturezaButton.setText(saude.getText());
                                }
                              });

                              educacao.setOnAction(new EventHandler<ActionEvent>() {
                                public void handle(ActionEvent actionEvent) {
                                  naturezaButton.setText(educacao.getText());
                                }
                              });

                              habitacao.setOnAction(new EventHandler<ActionEvent>() {
                                public void handle(ActionEvent actionEvent) {
                                  naturezaButton.setText(habitacao.getText());
                                }
                              });

                              lares.setOnAction(new EventHandler<ActionEvent>() {
                                public void handle(ActionEvent actionEvent) {
                                  naturezaButton.setText(lares.getText());
                                }
                              });

                              repAutomoveis.setOnAction(new EventHandler<ActionEvent>() {
                                public void handle(ActionEvent actionEvent) {
                                  naturezaButton.setText(repAutomoveis.getText());
                                }
                              });

                              restAlojam.setOnAction(new EventHandler<ActionEvent>() {
                                public void handle(ActionEvent actionEvent) {
                                  naturezaButton.setText(restAlojam.getText());
                                }
                              });

                              cabeleireiros.setOnAction(new EventHandler<ActionEvent>() {
                                public void handle(ActionEvent actionEvent) {
                                  naturezaButton.setText(cabeleireiros.getText());
                                }
                              });

                              repMotociclos.setOnAction(new EventHandler<ActionEvent>() {
                                public void handle(ActionEvent actionEvent) {
                                  naturezaButton.setText(repMotociclos.getText());
                                }
                              });

                              atiVeterinarias.setOnAction(new EventHandler<ActionEvent>() {
                                public void handle(ActionEvent actionEvent) {
                                  naturezaButton.setText(atiVeterinarias.getText());
                                }
                              });

                              passesMensais.setOnAction(new EventHandler<ActionEvent>() {
                                public void handle(ActionEvent actionEvent) {
                                  naturezaButton.setText(passesMensais.getText());
                                }
                              });

                              submeter.setOnAction(new EventHandler<ActionEvent>() {
                                public void handle(ActionEvent actionEvent) {
                                    Factura factura = getTableView().getItems().get(getIndex());

                                    if (naturezaButton.getText().equals(despesasGerais.getText())){
                                        try{
                                            struct.setCAEFactura(factura, logged_user_nif, 1);
                                        }
                                        catch(CINullException err){
                                            Alert alert = new Alert(AlertType.WARNING);
                                            alert.setTitle("Aviso");
                                            alert.setHeaderText(null);
                                            alert.setContentText("Algo correu mal.");
                                            alert.showAndWait();

                                            loadCAEFacturas();
                                            screen.setScene(dashboard_CAEfacturas);
                                        }
                                        catch(CCNullException err){
                                            Alert alert = new Alert(AlertType.WARNING);
                                            alert.setTitle("Aviso");
                                            alert.setHeaderText(null);
                                            alert.setContentText("Algo correu mal.");
                                            alert.showAndWait();

                                            loadCAEFacturas();
                                            screen.setScene(dashboard_CAEfacturas);
                                        }
                                    }

                                    else if (naturezaButton.getText().equals(saude.getText())){
                                        try{
                                            struct.setCAEFactura(factura, logged_user_nif, 2);
                                        }
                                        catch(CINullException err){
                                            Alert alert = new Alert(AlertType.WARNING);
                                            alert.setTitle("Aviso");
                                            alert.setHeaderText(null);
                                            alert.setContentText("Algo correu mal.");
                                            alert.showAndWait();

                                            loadCAEFacturas();
                                            screen.setScene(dashboard_CAEfacturas);
                                        }
                                        catch(CCNullException err){
                                            Alert alert = new Alert(AlertType.WARNING);
                                            alert.setTitle("Aviso");
                                            alert.setHeaderText(null);
                                            alert.setContentText("Algo correu mal.");
                                            alert.showAndWait();

                                            loadCAEFacturas();
                                            screen.setScene(dashboard_CAEfacturas);
                                        }
                                    }
                                    else if (naturezaButton.getText().equals(educacao.getText())){
                                        try{
                                            struct.setCAEFactura(factura, logged_user_nif, 3);
                                        }
                                        catch(CINullException err){
                                            Alert alert = new Alert(AlertType.WARNING);
                                            alert.setTitle("Aviso");
                                            alert.setHeaderText(null);
                                            alert.setContentText("Algo correu mal.");
                                            alert.showAndWait();

                                            loadCAEFacturas();
                                            screen.setScene(dashboard_CAEfacturas);
                                        }
                                        catch(CCNullException err){
                                            Alert alert = new Alert(AlertType.WARNING);
                                            alert.setTitle("Aviso");
                                            alert.setHeaderText(null);
                                            alert.setContentText("Algo correu mal.");
                                            alert.showAndWait();

                                            loadCAEFacturas();
                                            screen.setScene(dashboard_CAEfacturas);
                                        }
                                    }
                                    else if (naturezaButton.getText().equals(habitacao.getText())){
                                        try{
                                            struct.setCAEFactura(factura, logged_user_nif, 4);
                                        }
                                        catch(CINullException err){
                                            Alert alert = new Alert(AlertType.WARNING);
                                            alert.setTitle("Aviso");
                                            alert.setHeaderText(null);
                                            alert.setContentText("Algo correu mal.");
                                            alert.showAndWait();

                                            loadCAEFacturas();
                                            screen.setScene(dashboard_CAEfacturas);
                                        }
                                        catch(CCNullException err){
                                            Alert alert = new Alert(AlertType.WARNING);
                                            alert.setTitle("Aviso");
                                            alert.setHeaderText(null);
                                            alert.setContentText("Algo correu mal.");
                                            alert.showAndWait();

                                            loadCAEFacturas();
                                            screen.setScene(dashboard_CAEfacturas);
                                        }
                                    }

                                    else if (naturezaButton.getText().equals(lares.getText())){
                                        try{
                                            struct.setCAEFactura(factura, logged_user_nif, 5);
                                        }
                                        catch(CINullException err){
                                            Alert alert = new Alert(AlertType.WARNING);
                                            alert.setTitle("Aviso");
                                            alert.setHeaderText(null);
                                            alert.setContentText("Algo correu mal.");
                                            alert.showAndWait();

                                            loadCAEFacturas();
                                            screen.setScene(dashboard_CAEfacturas);
                                        }
                                        catch(CCNullException err){
                                            Alert alert = new Alert(AlertType.WARNING);
                                            alert.setTitle("Aviso");
                                            alert.setHeaderText(null);
                                            alert.setContentText("Algo correu mal.");
                                            alert.showAndWait();

                                            loadCAEFacturas();
                                            screen.setScene(dashboard_CAEfacturas);
                                        }
                                    }

                                    else if (naturezaButton.getText().equals(repAutomoveis.getText())){
                                        try{
                                            struct.setCAEFactura(factura, logged_user_nif, 6);
                                        }
                                        catch(CINullException err){
                                            Alert alert = new Alert(AlertType.WARNING);
                                            alert.setTitle("Aviso");
                                            alert.setHeaderText(null);
                                            alert.setContentText("Algo correu mal.");
                                            alert.showAndWait();

                                            loadCAEFacturas();
                                            screen.setScene(dashboard_CAEfacturas);
                                        }
                                        catch(CCNullException err){
                                            Alert alert = new Alert(AlertType.WARNING);
                                            alert.setTitle("Aviso");
                                            alert.setHeaderText(null);
                                            alert.setContentText("Algo correu mal.");
                                            alert.showAndWait();

                                            loadCAEFacturas();
                                            screen.setScene(dashboard_CAEfacturas);
                                        }
                                    }

                                    else if (naturezaButton.getText().equals(restAlojam.getText())){
                                        try{
                                            struct.setCAEFactura(factura, logged_user_nif, 7);
                                        }
                                        catch(CINullException err){
                                            Alert alert = new Alert(AlertType.WARNING);
                                            alert.setTitle("Aviso");
                                            alert.setHeaderText(null);
                                            alert.setContentText("Algo correu mal.");
                                            alert.showAndWait();

                                            loadCAEFacturas();
                                            screen.setScene(dashboard_CAEfacturas);
                                        }
                                        catch(CCNullException err){
                                            Alert alert = new Alert(AlertType.WARNING);
                                            alert.setTitle("Aviso");
                                            alert.setHeaderText(null);
                                            alert.setContentText("Algo correu mal.");
                                            alert.showAndWait();

                                            loadCAEFacturas();
                                            screen.setScene(dashboard_CAEfacturas);
                                        }
                                    }

                                    else if (naturezaButton.getText().equals(cabeleireiros.getText())){
                                        try{
                                            struct.setCAEFactura(factura, logged_user_nif, 8);
                                        }
                                        catch(CINullException err){
                                            Alert alert = new Alert(AlertType.WARNING);
                                            alert.setTitle("Aviso");
                                            alert.setHeaderText(null);
                                            alert.setContentText("Algo correu mal.");
                                            alert.showAndWait();

                                            loadCAEFacturas();
                                            screen.setScene(dashboard_CAEfacturas);
                                        }
                                        catch(CCNullException err){
                                            Alert alert = new Alert(AlertType.WARNING);
                                            alert.setTitle("Aviso");
                                            alert.setHeaderText(null);
                                            alert.setContentText("Algo correu mal.");
                                            alert.showAndWait();

                                            loadCAEFacturas();
                                            screen.setScene(dashboard_CAEfacturas);
                                        }
                                    }

                                    else if (naturezaButton.getText().equals(repMotociclos.getText())){
                                        try{
                                            struct.setCAEFactura(factura, logged_user_nif, 9);
                                        }
                                        catch(CINullException err){
                                            Alert alert = new Alert(AlertType.WARNING);
                                            alert.setTitle("Aviso");
                                            alert.setHeaderText(null);
                                            alert.setContentText("Algo correu mal.");
                                            alert.showAndWait();

                                            loadCAEFacturas();
                                            screen.setScene(dashboard_CAEfacturas);
                                        }
                                        catch(CCNullException err){
                                            Alert alert = new Alert(AlertType.WARNING);
                                            alert.setTitle("Aviso");
                                            alert.setHeaderText(null);
                                            alert.setContentText("Algo correu mal.");
                                            alert.showAndWait();

                                            loadCAEFacturas();
                                            screen.setScene(dashboard_CAEfacturas);
                                        }
                                    }

                                    else if (naturezaButton.getText().equals(atiVeterinarias.getText())){
                                        try{
                                            struct.setCAEFactura(factura, logged_user_nif, 10);
                                        }
                                        catch(CINullException err){
                                            Alert alert = new Alert(AlertType.WARNING);
                                            alert.setTitle("Aviso");
                                            alert.setHeaderText(null);
                                            alert.setContentText("Algo correu mal.");
                                            alert.showAndWait();

                                            loadCAEFacturas();
                                            screen.setScene(dashboard_CAEfacturas);
                                        }
                                        catch(CCNullException err){
                                            Alert alert = new Alert(AlertType.WARNING);
                                            alert.setTitle("Aviso");
                                            alert.setHeaderText(null);
                                            alert.setContentText("Algo correu mal.");
                                            alert.showAndWait();

                                            loadCAEFacturas();
                                            screen.setScene(dashboard_CAEfacturas);
                                        }
                                    }

                                    else if (naturezaButton.getText().equals(passesMensais.getText())){
                                        try{
                                            struct.setCAEFactura(factura, logged_user_nif, 11);
                                        }
                                        catch(CINullException err){
                                            Alert alert = new Alert(AlertType.WARNING);
                                            alert.setTitle("Aviso");
                                            alert.setHeaderText(null);
                                            alert.setContentText("Algo correu mal.");
                                            alert.showAndWait();

                                            loadCAEFacturas();
                                            screen.setScene(dashboard_CAEfacturas);
                                        }
                                        catch(CCNullException err){
                                            Alert alert = new Alert(AlertType.WARNING);
                                            alert.setTitle("Aviso");
                                            alert.setHeaderText(null);
                                            alert.setContentText("Algo correu mal.");
                                            alert.showAndWait();

                                            loadCAEFacturas();
                                            screen.setScene(dashboard_CAEfacturas);
                                        }
                                    }

                                    loadCAEFacturas();
                                    screen.setScene(dashboard_CAEfacturas);
                                }
                            });
                                setGraphic(hbox);
                                setText(null);
                            }
                            catch(CCNullException err){
                                Alert alert = new Alert(AlertType.WARNING);
                                alert.setTitle("Aviso");
                                alert.setHeaderText(null);
                                alert.setContentText("Algo correu mal.");
                                alert.showAndWait();

                                loadDashboardIndividual();
                                screen.setScene(dashboard_individual);
                            }
                        }
                    }
                };
                return cell;
            }
        };

        natureza.setCellFactory(cellFactory);

        ObservableList<Factura> data = FXCollections.observableArrayList();

        for(Factura factura: struct.getFacturasColetivosOuIndividual(logged_user_nif)) {
            if(factura.getNatureza() == 0)
                data.add(factura);
        }

        table = new TableView<>();
        table.setItems(data);
        table.getColumns().addAll(numero, dataEmissao, valor, natureza);

        grid.getChildren().addAll(table);


        Button voltar = new Button("Voltar");
        HBox hbVoltar = new HBox(10);
        hbVoltar.setAlignment(Pos.BOTTOM_LEFT);
        hbVoltar.getChildren().add(voltar);
        grid.add(hbVoltar, 0, 35);


        voltar.setOnAction(e -> {
            loadDashboardIndividual();
            screen.setScene(this.dashboard_individual);
        });

        this.dashboard_CAEfacturas = new Scene(grid, 500, 450);
    }

    private void loadHistoricoCAES() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(30, 30, 30, 30));

        TableView<Factura> table;

        TableColumn<Factura, Integer> numero = new TableColumn<>("Número da Factura");
        numero.setCellValueFactory(
                new PropertyValueFactory<>("id"));
        numero.setMinWidth(130);

        TableColumn<Factura, Integer> nifEmitente = new TableColumn<>("Nif do Emitente");
        nifEmitente.setCellValueFactory(
                new PropertyValueFactory<>("nif_emitente"));
        nifEmitente.setMinWidth(120);

        TableColumn<Factura, BigDecimal> naturezas = new TableColumn<>("Naturezas");
        naturezas.setCellValueFactory(
                new PropertyValueFactory<>("historico_caes"));
        naturezas.setMinWidth(200);

        try{
            ObservableList<Factura> data = FXCollections.observableArrayList();

            for(Factura factura: struct.getContribuinteColetivo(logged_user_nif).getFacturas()) {
                if(factura.getHistorico_caes().size() >= 1)
                    data.add(factura);
            }

            table = new TableView<>();
            table.setItems(data);
            table.getColumns().addAll(numero, nifEmitente, naturezas);

            grid.getChildren().addAll(table);

            Button voltar = new Button("Voltar");
            HBox hbVoltar = new HBox(10);
            hbVoltar.setAlignment(Pos.BOTTOM_LEFT);
            hbVoltar.getChildren().add(voltar);
            grid.add(hbVoltar, 6, 9);

            voltar.setOnAction(e -> {
                loadDashboardColetivo();
                screen.setScene(this.dashboard_coletivo);
            });
        }
        catch(CCNullException err){
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Aviso");
            alert.setHeaderText(null);
            alert.setContentText("Não é possível calcular o histórico dos CAES.");
            alert.showAndWait();

            loadDashboardColetivo();
            screen.setScene(this.dashboard_coletivo);
        }
        this.dashboard_historicoCAES = new Scene(grid, 1150, 800);
    }

    private void loadAgregado(){
        GridPane grid = new GridPane();
        grid.setHgap(70);
        grid.setVgap(8);
        grid.setPadding(new Insets(40, 40, 40, 40));
        grid.setAlignment(Pos.CENTER);

        try{
            ContribuinteIndividual contribuinte_individual = struct.getContribuinteIndividual(logged_user_nif);

            Button direction_invoices = new Button("Fazer parte da direção deste agregado");
            HBox hbDirectionInvoices = new HBox(10);
            hbDirectionInvoices.setAlignment(Pos.CENTER);
            hbDirectionInvoices.getChildren().add(direction_invoices);
            grid.add(hbDirectionInvoices, 0, 0);

            direction_invoices.setOnAction(e -> {
                try{
                    if((!contribuinte_individual.pertenceDependentesAgregado(logged_user_nif)) &&
                       (!contribuinte_individual.pertenceDirecaoAgregado(logged_user_nif)) &&
                         contribuinte_individual.podeAdicionarDiretores()){

                        struct.addNovaDirecaoAgregado(logged_user_nif);

                        loadAgregado();
                        screen.setScene(this.dashboard_agregado);
                    }
                    else if(contribuinte_individual.pertenceDirecaoAgregado(logged_user_nif)){
                        Alert alert = new Alert(AlertType.INFORMATION);
                        alert.setTitle("Informação");
                        alert.setHeaderText(null);
                        alert.setContentText("Já fazes parte da direção do agregado.");
                        alert.showAndWait();

                        loadAgregado();
                        screen.setScene(this.dashboard_agregado);
                    }
                    else if(contribuinte_individual.pertenceDependentesAgregado(logged_user_nif)){
                        Alert alert = new Alert(AlertType.INFORMATION);
                        alert.setTitle("Informação");
                        alert.setHeaderText(null);
                        alert.setContentText("És um dependente do agregado, logo, não podes associar-te à direção do mesmo.");
                        alert.showAndWait();

                        loadAgregado();
                        screen.setScene(this.dashboard_agregado);
                    }
                    else if(!contribuinte_individual.podeAdicionarDiretores()){
                        Alert alert = new Alert(AlertType.INFORMATION);
                        alert.setTitle("Informação");
                        alert.setHeaderText(null);
                        alert.setContentText("A direção do agregado já está cheia.");
                        alert.showAndWait();

                        loadAgregado();
                        screen.setScene(this.dashboard_agregado);
                    }
                }
                catch(CINullException err){
                    Alert alert = new Alert(AlertType.WARNING);
                    alert.setTitle("Aviso");
                    alert.setHeaderText(null);
                    alert.setContentText("Não é possível efetuar essa ação.");
                    alert.showAndWait();

                    loadAgregado();
                    screen.setScene(this.dashboard_agregado);
                }
            });

            Button otherdirection_invoices = new Button("Fazer parte da direção de outro agregado");
            HBox hbOtherDirectionInvoices = new HBox(10);
            hbOtherDirectionInvoices.setAlignment(Pos.CENTER);
            hbOtherDirectionInvoices.getChildren().add(otherdirection_invoices);
            grid.add(hbOtherDirectionInvoices, 0, 2);

            otherdirection_invoices.setOnAction(e -> {
                loadNovoAgregado();
                screen.setScene(this.dashboard_novoagregado);
            });

            //Direção do Agregado
            Label direcao_nome = new Label("NIFs da direção do agregado: ");
            grid.add(direcao_nome, 0, 6);
            direcao_nome.setMaxWidth(Double.MAX_VALUE);
            direcao_nome.setAlignment(Pos.CENTER);

            Label direcao_nifs = new Label(contribuinte_individual.getNifsDirecaoAgregado().toString());
            grid.add(direcao_nifs, 1, 6);
            direcao_nifs.setMaxWidth(Double.MAX_VALUE);
            direcao_nifs.setAlignment(Pos.CENTER);

            //Dependentes do Agregado
            Label dependentes_nome = new Label("NIFs dos dependentes do agregado: ");
            grid.add(dependentes_nome, 0, 10);
            dependentes_nome.setMaxWidth(Double.MAX_VALUE);
            dependentes_nome.setAlignment(Pos.CENTER);

            Label dependentes_nifs = new Label(contribuinte_individual.getNifsDependentesAgregado().toString());
            grid.add(dependentes_nifs, 1, 10);
            dependentes_nifs.setMaxWidth(Double.MAX_VALUE);
            dependentes_nifs.setAlignment(Pos.CENTER);

            Button voltar = new Button("Voltar");
            HBox hbVoltar = new HBox(10);
            hbVoltar.setAlignment(Pos.BOTTOM_LEFT);
            hbVoltar.getChildren().add(voltar);
            grid.add(hbVoltar, 2, 30);


            voltar.setOnAction(e -> {
                loadDashboardIndividual();
                screen.setScene(this.dashboard_individual);
            });
        }
        catch(CINullException err){
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Aviso");
            alert.setHeaderText(null);
            alert.setContentText("Não é possível visualizar a informação contida neste separador.");
            alert.showAndWait();

            loadDashboardIndividual();
            screen.setScene(this.dashboard_individual);
        }

        this.dashboard_agregado = new Scene(grid, 1150, 800);
    }

    private void loadNovoAgregado(){
        GridPane grid = new GridPane();
        grid.setHgap(70);
        grid.setVgap(8);
        grid.setPadding(new Insets(40, 40, 40, 40));
        grid.setAlignment(Pos.CENTER);

        try{
            ContribuinteIndividual contribuinte_individual = struct.getContribuinteIndividual(logged_user_nif);

            Label nif = new Label("Inserir NIF do contribuinte da outra direção:");
            grid.add(nif, 0, 0);

            TextField nifField = new TextField();
            grid.add(nifField, 1, 0);


            Button submeter_invoices = new Button("Submeter");
            HBox hbSubmeterInvoices = new HBox(10);
            hbSubmeterInvoices.setAlignment(Pos.CENTER);
            hbSubmeterInvoices.getChildren().add(submeter_invoices);
            grid.add(hbSubmeterInvoices, 1, 8);

            submeter_invoices.setOnAction(e -> {
                try{
                    int nif_other = Integer.parseInt(nifField.getText());
                    ContribuinteIndividual contribuinte_other = struct.getContribuinteIndividual(nif_other);

                    if(contribuinte_individual.pertenceDirecaoAgregado(logged_user_nif) &&
                        contribuinte_other.pertenceDirecaoAgregado(nif_other) &&
                       contribuinte_individual.podeAdicionarDiretores() && contribuinte_other.podeAdicionarDiretores() &&
                       (!contribuinte_individual.pertenceDependentesAgregado(nif_other)) &&
                       (!contribuinte_other.pertenceDependentesAgregado(nif_other)) &&
                       (!contribuinte_individual.pertenceDirecaoAgregado(nif_other))){

                        struct.addNovaDirecaoOutroAgregado(logged_user_nif, nif_other);

                        Alert alert = new Alert(AlertType.INFORMATION);
                        alert.setTitle("Informação");
                        alert.setHeaderText(null);
                        alert.setContentText("Ação efetuada com sucesso.");
                        alert.showAndWait();

                        loadNovoAgregado();
                        screen.setScene(this.dashboard_novoagregado);
                    }
                    else if(!contribuinte_individual.pertenceDirecaoAgregado(logged_user_nif)){
                        Alert alert = new Alert(AlertType.INFORMATION);
                        alert.setTitle("Informação");
                        alert.setHeaderText(null);
                        alert.setContentText("Não és elegível para te associares a um agregado. Essa operação só pode ser efetuada por contribuintes na direção.");
                        alert.showAndWait();

                        loadNovoAgregado();
                        screen.setScene(this.dashboard_novoagregado);
                    }
                    else if(!contribuinte_individual.podeAdicionarDiretores()){
                        Alert alert = new Alert(AlertType.INFORMATION);
                        alert.setTitle("Informação");
                        alert.setHeaderText(null);
                        alert.setContentText("A direção do teu agregado familiar já está cheia.");
                        alert.showAndWait();

                        loadNovoAgregado();
                        screen.setScene(this.dashboard_novoagregado);
                    }
                    else if(!contribuinte_other.podeAdicionarDiretores()){
                        Alert alert = new Alert(AlertType.INFORMATION);
                        alert.setTitle("Informação");
                        alert.setHeaderText(null);
                        alert.setContentText("A direção do agregado familiar a que te pertendes associar já está cheia.");
                        alert.showAndWait();

                        loadNovoAgregado();
                        screen.setScene(this.dashboard_novoagregado);
                    }
                    else if(contribuinte_individual.pertenceDependentesAgregado(nif_other) ||
                            contribuinte_individual.pertenceDirecaoAgregado(nif_other)){
                        Alert alert = new Alert(AlertType.INFORMATION);
                        alert.setTitle("Informação");
                        alert.setHeaderText(null);
                        alert.setContentText("O contribuinte já faz parte do teu agregado.");
                        alert.showAndWait();

                        loadNovoAgregado();
                        screen.setScene(this.dashboard_novoagregado);
                    }
                    else if(contribuinte_other.pertenceDependentesAgregado(nif_other) ||
                            !contribuinte_other.pertenceDirecaoAgregado(nif_other)){
                        Alert alert = new Alert(AlertType.INFORMATION);
                        alert.setTitle("Informação");
                        alert.setHeaderText(null);
                        alert.setContentText("Tens que inserir um contribuinte que faça parte da direção do agregado a que te pertendes associar.");
                        alert.showAndWait();

                        loadNovoAgregado();
                        screen.setScene(this.dashboard_novoagregado);
                    }
                }
                catch(CINullException err){
                    Alert alert = new Alert(AlertType.WARNING);
                    alert.setTitle("Aviso");
                    alert.setHeaderText(null);
                    alert.setContentText("Não é possível efetuar essa ação. Esse contribuinte não existe.");
                    alert.showAndWait();

                    loadNovoAgregado();
                    screen.setScene(this.dashboard_novoagregado);
                }
            });

            Button voltar = new Button("Voltar");
            HBox hbVoltar = new HBox(10);
            hbVoltar.setAlignment(Pos.BOTTOM_LEFT);
            hbVoltar.getChildren().add(voltar);
            grid.add(hbVoltar, 2, 8);


            voltar.setOnAction(e -> {
                loadAgregado();
                screen.setScene(this.dashboard_agregado);
            });
        }
        catch(CINullException err){
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Aviso");
            alert.setHeaderText(null);
            alert.setContentText("Não é possível visualizar a informação contida neste separador.");
            alert.showAndWait();

            loadAgregado();
            screen.setScene(this.dashboard_agregado);
        }

        this.dashboard_novoagregado = new Scene(grid, 1150, 800);
    }

    /**
     * Ponto de entrada principal para a aplicação
     *
     * @param primaryStage estado inicial da aplicação
     */
    public void start(Stage primaryStage) {
        this.screen = primaryStage;
        this.screen.setTitle("JavaFactura");
        this.screen.setWidth(1300);
        this.screen.setHeight(800);

        this.struct = new JavaFactura();

        this.loadMain();
        this.loadDashboardColetivo();
        this.loadDashboardAdmin();

        this.screen.setScene(this.main);
        this.screen.show();
    }

    public static void main(final String[] arguments) {
        launch(arguments);
    }
}
