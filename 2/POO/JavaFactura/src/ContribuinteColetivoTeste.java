/**
 * Classe FacturaTeste, classe de testes.
 *
 * @author A81047
 * @author A34900
 * @author A82339
 * @version 20180513
 */

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;
import java.math.BigDecimal;
import java.time.LocalDate;

public class ContribuinteColetivoTeste {
    ContribuinteColetivo cc;
    ArrayList<Integer> atividadesEmpresa;
    ArrayList<Factura> faturasCC;

    /**
     * Construtor default para a classe de teste ContribuinteColetivoTeste
     */
    public ContribuinteColetivoTeste() {
    }

    /**
     * Define o arraylist das atividades prestadas pelo contribuinte,
     * bem como as suas faturas emitidas e povoa essas estruturas.
     * Cria ainda um contribuinte coletivo.
     *
     * Chamado antes de cada método de caso de teste.
     */
    @Before
    public void setUp() {
        atividadesEmpresa = new ArrayList<>();
        faturasCC = new ArrayList<Factura>();

        atividadesEmpresa.add(6);
        atividadesEmpresa.add(9);

        ArrayList<Integer> caes = new ArrayList<>();
        caes.add(6);

        ArrayList<Integer> caes2 = new ArrayList<>();
        caes2.add(9);

        Factura f1 = new Factura(12, 500, "Motor&Lda",LocalDate.of(2018, 02, 13), 123, "Lavagem", Automoveis.objeto.getCodigo(), new BigDecimal(10), caes);
        Factura f2 = new Factura(1230, 500, "Motor&Lda",LocalDate.of(2018, 5, 10), 1243, "Pistões", Motociclos.objeto.getCodigo(), new BigDecimal(13), caes2);

        faturasCC.add(f1);
        faturasCC.add(f2);
        cc = new ContribuinteColetivo(500, "ola@gmail.com", "Motor&Lda", "Rua de Baixo", "ola", atividadesEmpresa, 0.0, "Braga", faturasCC);
    }

    /**
     * Tears down the test fixture.
     *
     * Chamado após cada método de teste de caso.
     */
    @After
    public void tearDown() {
    }

    /**
     * Método que testa o construtor vazio da classe ContribuinteColetivo.
     *
     */
    @Test
    public void testContribuinteVazio() {


        ContribuinteColetivo cc = new ContribuinteColetivo();

        assertEquals("Lista de atividades de uma empresa", cc.getAtividades_economicas() , new ArrayList<Integer>());
        assertEquals("Fator de deducao fiscal", cc.getFator_deducao_fiscal(), 0.0, 0.0);
        assertEquals("Nome do concelho onde está sediada a empresa", cc.getConcelho(), "");
        assertEquals("Lista das faturas emitidas", cc.getFacturas(), new ArrayList<Factura>());

    }

    /**
     * Método que testa o construtor parametrizado da classe ContribuinteColetivo.
     *
     */
     @Test
    public void testContribuinteParametrizado() {
        assertEquals("Lista de atividades de uma empresa", cc.getAtividades_economicas() , atividadesEmpresa);
        assertEquals("Fator de deducao fiscal", cc.getFator_deducao_fiscal(), 0.0, 0.0);
        assertEquals("Nome do concelho onde está sediada a empresa", cc.getConcelho(), "Braga");
        assertEquals("Lista das faturas emitidas", cc.getFacturas(), faturasCC);
    }

    /**
     * Método que testa o construtor cópia da classe ContribuinteColetivo.
     *
     */
     @Test
    public void testContribuinteCopia() {
        ContribuinteColetivo cc2 = new ContribuinteColetivo(cc);

        assertEquals("Lista de atividades de uma empresa", cc2.getAtividades_economicas() , atividadesEmpresa);
        assertEquals("Fator de deducao fiscal", cc2.getFator_deducao_fiscal(), 0.0, 0.0);
        assertEquals("Nome do concelho onde está sediada a empresa", cc2.getConcelho(), "Braga");
        assertEquals("Lista das faturas emitidas", cc2.getFacturas(), faturasCC);


        assertEquals("Objectos sao iguais", cc, cc2);
    }

    /**
     * Método que testa a alteração feita nos parametros do contribuinte.
     *
     */
    @Test
    public void testContribuinteSet() {
        ArrayList<Integer> atividadesEmpresa2 = new ArrayList<>();
        ArrayList<Factura> faturasCC2 = new ArrayList<Factura>();

        atividadesEmpresa2.add(1);
        atividadesEmpresa.add(11);

        ArrayList<Integer> caes = new ArrayList<>();
        caes.add(1);

        ArrayList<Integer> caes2 = new ArrayList<>();
        caes2.add(11);

        Factura f1 = new Factura(12, 500, "Motor&Lda",LocalDate.of(2018, 02, 13), 123, "Pano", Gerais.objeto.getCodigo(), new BigDecimal(90), caes);
        Factura f2 = new Factura(1230, 500, "Motor&Lda",LocalDate.of(2018, 5, 9), 1243, "Passe", PassesTransportes.objeto.getCodigo(), new BigDecimal(25), caes2);

        faturasCC.add(f1);
        faturasCC.add(f2);

        cc.setAtividades_economicas(atividadesEmpresa2);
        cc.setFator_deducao_fiscal(1);
        cc.setConcelho("Beja");
        cc.setFacturas(faturasCC2);

        assertEquals("Lista de atividades de uma empresa", cc.getAtividades_economicas() , atividadesEmpresa2);
        assertEquals("Fator de deducao fiscal", cc.getFator_deducao_fiscal(), 1.0, 0.0);
        assertEquals("Nome do concelho onde está sediada a empresa", cc.getConcelho(), "Beja");
        assertEquals("Lista das faturas emitidas", cc.getFacturas(), faturasCC2);
    }

    /**
     * Método que testa a se uma factura é emitida pelo contribuinte
     *
     */
    @Test
    public void testAddFacturaEmitida() {
        ArrayList<Integer> caes = new ArrayList<>();
        caes.add(0);
        caes.add(1);
        ArrayList<Integer> caes2 = new ArrayList<>();
        caes2.add(11);

        Factura f1 = new Factura(20, 500, "Motor&Lda",LocalDate.of(2018, 02, 13), 123, "Pano", Automoveis.objeto.getCodigo(), new BigDecimal(90), caes);
        Factura f2 = new Factura(1230, 500, "Motor&Lda",LocalDate.of(2018, 5, 9), 1243, "Passe", PassesTransportes.objeto.getCodigo(), new BigDecimal(25), caes2);
        faturasCC.add(f1);
        cc.addFacturaEmitida(f1);

        assertEquals("Lista das faturas emitidas", cc.getFacturas(), faturasCC);
        cc.addFacturaEmitida(f2);
        faturasCC.add(f2);
        assertEquals("Lista das faturas emitidas", cc.getFacturas(), faturasCC);
    }

    /**
     * Método que testa o valor total faturado pelo contribuinte.
     *
     */
    @Test
    public void testtotalFacturado() {
        BigDecimal totalFaturado = cc.totalFacturado();


        assertEquals("Total facturado pela empresa", totalFaturado, new BigDecimal(23));
    }

    /**
     * Método que testa o valor total faturado pelo contribuinte num período determinado.
     *
     */
    @Test
    public void testtotalFacturadoPeriodo() {
        BigDecimal totalFaturado = cc.totalFacturado(LocalDate.of(2018, 4, 13), LocalDate.of(2018, 5, 13));
        assertEquals("Total facturado pela empresa", totalFaturado, new BigDecimal(13));

        BigDecimal semTotalFaturado = cc.totalFacturado(LocalDate.of(2017, 4, 13), LocalDate.of(2017, 5, 13));
        assertEquals("Total facturado pela empresa", semTotalFaturado, new BigDecimal(0));
    }
}
