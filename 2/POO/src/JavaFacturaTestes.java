/**
 * A classe de teste JavaFacturaTestes.
 *
 * @author A81047
 * @author A34900
 * @author A82339
 * @version 20180520
 */

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;
import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.List;
import java.util.HashMap;
import java.math.RoundingMode;

public class JavaFacturaTestes {
    private JavaFactura jc;
    ContribuinteIndividual ci_pai;
    List<Integer> nifsDependentesAgregado;
    HashMap<Integer, ArrayList<Factura>> exemplo;
    private List<Integer> nifsDirecaoAgregado;
    /**
     * Construtor default para a classe de teste JavaFacturaTestes
     */
    public JavaFacturaTestes() {
    }

    /**
     * Define a .
     *
     * Chamado antes de cada método de caso de teste.
     */
    @Before
    public void setUp() {
        jc = new JavaFactura();
        exemplo = new HashMap<Integer, ArrayList<Factura>>();
        nifsDependentesAgregado = new ArrayList<Integer>();
        nifsDirecaoAgregado = new ArrayList<Integer>();

        AtividadesEconomicas.criarAtividades();

        nifsDirecaoAgregado.add(1);
        nifsDependentesAgregado.add(2);
        nifsDependentesAgregado.add(3);
        nifsDependentesAgregado.add(4);
        nifsDependentesAgregado.add(5);
        nifsDependentesAgregado.add(6);

        ArrayList<Integer> caes = new ArrayList<>();
        caes.add(4);

        Factura f1 = new Factura(12, 511, "Hospital",LocalDate.of(2018, 5, 13), 1, "Urgencia", Saude.objeto.getCodigo(), new BigDecimal(2000), caes);
        Factura f2 = new Factura(121, 511, "CasaSaude&Lda",LocalDate.of(2018, 5, 01), 1, "Consulta", Saude.objeto.getCodigo(), new BigDecimal(500), caes);
        Factura f3 = new Factura(200, 511, "Pharma.SA",LocalDate.of(2018, 3, 13), 1, "Bennurom", Saude.objeto.getCodigo(), new BigDecimal(4), caes);
        Factura f4 = new Factura(409, 523, "Teste.Lda",LocalDate.of(2018, 01, 13), 1, "Tac", Saude.objeto.getCodigo(), new BigDecimal(1000), caes);

        ArrayList<Factura> saude = new ArrayList<Factura>();
        saude.add(f1);
        saude.add(f2);
        saude.add(f3);
        saude.add(f4);

        exemplo.put(f1.getNatureza(), saude);


        ci_pai = new ContribuinteIndividual(1, "ca@gmail.com", "Ana Santos", "Rua da Estrela", "sim123",
                                        1, 5, (ArrayList<Integer>) nifsDependentesAgregado, (ArrayList<Integer>) nifsDirecaoAgregado,1.6, exemplo);

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
     * Método que testa se as faturas de determinada empresa
     * estão ordenadas por valor ou por data.
     *
     */
    @Test
    public void testFaturaOrdenada(){
    ArrayList<Integer> atividadesEmpresa = new ArrayList<>();
    ArrayList<Factura> faturasCC = new ArrayList<Factura>();


        atividadesEmpresa.add(6);
        atividadesEmpresa.add(9);

        ArrayList<Integer> caes = new ArrayList<>();
        caes.add(6);

        ArrayList<Integer> caes2 = new ArrayList<>();
        caes2.add(9);

        Factura f1 = new Factura(12, 500, "Motor&Lda",LocalDate.of(2018, 02, 13), 123, "Lavagem", Automoveis.objeto.getCodigo(), new BigDecimal(10), caes);
        Factura f2 = new Factura(1230, 500, "Motor&Lda",LocalDate.of(2018, 5, 10), 1243, "Pistões", Motociclos.objeto.getCodigo(), new BigDecimal(9), caes2);
        Factura f3 = new Factura(126, 500, "Motor&Lda",LocalDate.of(2018, 5, 21), 1243, "Pistões", Motociclos.objeto.getCodigo(), new BigDecimal(13), caes2);


        faturasCC.add(f1);
        faturasCC.add(f2);
        faturasCC.add(f3);

        ContribuinteColetivo  cc = new ContribuinteColetivo(500, "ola@gmail.com", "Motor&Lda", "Rua de Baixo", "ola", atividadesEmpresa, 0.0, "Braga", faturasCC);

        jc.add(cc);
        List<Factura> ordenadoValor = jc.ordenaFaturasValor(cc.getNIF());
        List<Factura> ordenadoData = jc.ordenaFaturasData(cc.getNIF());

        ArrayList<Factura> novo = new ArrayList<>();
        novo.add(f2);
        novo.add(f1);
        novo.add(f3);

        ArrayList<Factura> novo2 = new ArrayList<>();
        novo2.add(f1);
        novo2.add(f2);
        novo2.add(f3);


        assertEquals("ordenado deve ser igual a novo", novo, ordenadoValor);
        assertEquals("ordenado deve ser igual a novo", novo2, ordenadoData);
    }

    /**
     * Método que testa se as faturas emitidas por uma empresa
     * a um contribuinte estão dentro de um intervalo de tempo predefinido.
     *
     */
    @Test
    public void testFaturaPeriodo(){
    ArrayList<Integer> atividadesEmpresa = new ArrayList<>();
    ArrayList<Factura> faturasCC = new ArrayList<Factura>();


        atividadesEmpresa.add(6);
        atividadesEmpresa.add(9);

        ArrayList<Integer> caes = new ArrayList<>();
        caes.add(6);

        ArrayList<Integer> caes2 = new ArrayList<>();
        caes2.add(9);

        Factura f1 = new Factura(12, 500, "Motor&Lda",LocalDate.of(2018, 02, 13), 123, "Lavagem", Automoveis.objeto.getCodigo(), new BigDecimal(10), caes);
        Factura f2 = new Factura(1230, 500, "Motor&Lda",LocalDate.of(2018, 5, 10), 1243, "Pistões", Motociclos.objeto.getCodigo(), new BigDecimal(9), caes2);
        Factura f3 = new Factura(126, 500, "Motor&Lda",LocalDate.of(2018, 5, 21), 1243, "Pistões", Motociclos.objeto.getCodigo(), new BigDecimal(13), caes2);


        faturasCC.add(f1);
        faturasCC.add(f2);
        faturasCC.add(f3);

        ContribuinteColetivo  cc = new ContribuinteColetivo(500, "ola@gmail.com", "Motor&Lda", "Rua de Baixo", "ola", atividadesEmpresa, 0.0, "Braga", faturasCC);

        jc.add(cc);

        List<Factura> faturasPeriodo = jc.faturasPeriodoContribuinte(cc.getNIF(),123, LocalDate.of(2017, 12, 21), LocalDate.of(2018, 04, 21));

        ArrayList<Factura> novo = new ArrayList<>();
        novo.add(f1);

        assertEquals("ordenado deve ser igual a novo", novo,faturasPeriodo);

   }
   /**
     * Método que testa se quais as X empresas com mais facturas
     *
     */
    @Test
    public void testEmpresasComMaisFacturas(){
    ArrayList<Integer> atividadesEmpresa = new ArrayList<>();
    ArrayList<Factura> faturasCC = new ArrayList<Factura>();

    atividadesEmpresa.add(1);


    ArrayList<Integer> caes = new ArrayList<>();
    caes.add(1);



    Factura f1 = new Factura(12, 501, "Motor&Lda",LocalDate.of(2018, 02, 13), 123, "Lavagem", Gerais.objeto.getCodigo(), new BigDecimal(10), caes);
    Factura f2 = new Factura(12, 501, "Motor&Lda",LocalDate.of(2018, 5, 10), 1243, "Pistões", Gerais.objeto.getCodigo(), new BigDecimal(9), caes);
    Factura f3 = new Factura(124, 501, "Motor&Lda",LocalDate.of(2018, 5, 21), 1243, "Pistões", Gerais.objeto.getCodigo(), new BigDecimal(13), caes);


    faturasCC.add(f1);
    faturasCC.add(f2);
    faturasCC.add(f3);

    ContribuinteColetivo  cc = new ContribuinteColetivo(501, "ola@gmail.com", "Motor&Lda", "Rua de Baixo", "ola", atividadesEmpresa, 0.0, "Braga", faturasCC);


    ArrayList<Integer> atividadesEmpresa2 = new ArrayList<>();
    ArrayList<Factura> faturasCC2 = new ArrayList<Factura>();

    atividadesEmpresa2.add(1);


    ArrayList<Integer> caes3 = new ArrayList<>();
    caes3.add(1);

    Factura f4 = new Factura(12, 502, "Salsa&Lda",LocalDate.of(2018, 02, 13), 12, "Lavagem", Gerais.objeto.getCodigo(), new BigDecimal(10), caes3);
    Factura f5 = new Factura(12, 502, "Salsa&Lda",LocalDate.of(2018, 5, 10), 12, "Pistões", Gerais.objeto.getCodigo(), new BigDecimal(9), caes3);
    Factura f6 = new Factura(124, 502, "Salsa&Lda",LocalDate.of(2018, 5, 21), 12, "Pistões", Gerais.objeto.getCodigo(), new BigDecimal(13), caes3);
    Factura f7 = new Factura(124, 502, "Salsa&Lda",LocalDate.of(2018, 5, 21), 124, "Pistões", Gerais.objeto.getCodigo(), new BigDecimal(20), caes3);

    faturasCC2.add(f4);
    faturasCC2.add(f5);
    faturasCC2.add(f6);
    faturasCC2.add(f7);


    ContribuinteColetivo  cc2 = new ContribuinteColetivo(502, "ola@gmail.com", "Motor&Lda", "Rua de Baixo", "ola", atividadesEmpresa2, 0.0, "Braga", faturasCC2);




    ArrayList<Integer> atividadesEmpresa3 = new ArrayList<>();
    ArrayList<Factura> faturasCC3 = new ArrayList<Factura>();

    atividadesEmpresa3.add(1);


    ArrayList<Integer> caes4 = new ArrayList<>();
    caes4.add(1);

    Factura f8 = new Factura(124, 503, "Salsa&Lda",LocalDate.of(2018, 02, 13), 12, "Lavagem", Gerais.objeto.getCodigo(), new BigDecimal(10), caes4);
    Factura f9 = new Factura(122, 503, "Salsa&Lda",LocalDate.of(2018, 5, 10), 12, "Pistões", Gerais.objeto.getCodigo(), new BigDecimal(10), caes4);
    Factura f10 = new Factura(112, 503, "Salsa&Lda",LocalDate.of(2018, 5, 21), 12, "Pistões", Gerais.objeto.getCodigo(), new BigDecimal(10), caes4);
    Factura f11 = new Factura(152, 503, "Salsa&Lda",LocalDate.of(2018, 5, 21), 12, "Pistões", Gerais.objeto.getCodigo(), new BigDecimal(10), caes4);
    Factura f12 = new Factura(162, 503, "Salsa&Lda",LocalDate.of(2018, 02, 13), 12, "Lavagem", Gerais.objeto.getCodigo(), new BigDecimal(10), caes4);
    Factura f13 = new Factura(128, 503, "Salsa&Lda",LocalDate.of(2018, 5, 10), 12, "Pistões", Gerais.objeto.getCodigo(), new BigDecimal(10), caes4);
    Factura f14 = new Factura(1240, 503, "Salsa&Lda",LocalDate.of(2018, 5, 21), 124, "Pistões", Gerais.objeto.getCodigo(), new BigDecimal(10), caes4);
    Factura f15 = new Factura(1249, 503, "Salsa&Lda",LocalDate.of(2018, 5, 21), 124, "Pistões", Gerais.objeto.getCodigo(), new BigDecimal(10), caes4);




    faturasCC3.add(f8);
    faturasCC3.add(f9);
    faturasCC3.add(f10);
    faturasCC3.add(f11);
    faturasCC3.add(f12);
    faturasCC3.add(f13);
    faturasCC3.add(f14);
    faturasCC3.add(f15);


    ContribuinteColetivo  cc3 = new ContribuinteColetivo(503, "ola@gmail.com", "Motor&Lda", "Rua de Baixo", "ola", atividadesEmpresa3, 0.0, "Braga", faturasCC3);


    ArrayList<Integer> atividadesEmpresa4 = new ArrayList<>();
    ArrayList<Factura> faturasCC5 = new ArrayList<Factura>();

    atividadesEmpresa4.add(1);

    ArrayList<Integer> caes5 = new ArrayList<>();
    caes5.add(1);

    Factura f16 = new Factura(1223, 504, "Salsa&Lda",LocalDate.of(2018, 02, 13), 12, "Lavagem", Gerais.objeto.getCodigo(), new BigDecimal(10), caes5);
    Factura f17 = new Factura(12245, 504, "Salsa&Lda",LocalDate.of(2018, 5, 10), 12, "Pistões", Gerais.objeto.getCodigo(), new BigDecimal(10), caes5);
    Factura f18 = new Factura(12466, 504, "Salsa&Lda",LocalDate.of(2018, 5, 21), 12, "Pistões", Gerais.objeto.getCodigo(), new BigDecimal(10), caes5);
    Factura f19 = new Factura(1297, 504, "Salsa&Lda",LocalDate.of(2018, 5, 21), 12, "Pistões", Gerais.objeto.getCodigo(), new BigDecimal(10), caes5);
    Factura f20 = new Factura(12686, 504, "Salsa&Lda",LocalDate.of(2018, 02, 13), 12, "Lavagem", Gerais.objeto.getCodigo(), new BigDecimal(10), caes5);
    Factura f21 = new Factura(12568, 504, "Salsa&Lda",LocalDate.of(2018, 5, 10), 12, "Pistões", Gerais.objeto.getCodigo(), new BigDecimal(10), caes5);
    Factura f22 = new Factura(102, 504, "Salsa&Lda",LocalDate.of(2018, 5, 21), 12, "Pistões", Gerais.objeto.getCodigo(), new BigDecimal(10), caes5);
    Factura f23 = new Factura(1241, 504, "Salsa&Lda",LocalDate.of(2018, 5, 21), 124, "Pistões", Gerais.objeto.getCodigo(), new BigDecimal(10), caes5);
    Factura f24 = new Factura(1234, 504, "Salsa&Lda",LocalDate.of(2018, 5, 21), 124, "Pistões", Gerais.objeto.getCodigo(), new BigDecimal(10), caes5);

    faturasCC5.add(f16);
    faturasCC5.add(f17);
    faturasCC5.add(f18);
    faturasCC5.add(f19);
    faturasCC5.add(f20);
    faturasCC5.add(f21);
    faturasCC5.add(f22);
    faturasCC5.add(f23);
    faturasCC5.add(f24);

    ContribuinteColetivo  cc4 = new ContribuinteColetivo(504, "ola@gmail.com", "Motor&Lda", "Rua de Baixo", "ola", atividadesEmpresa4, 0.0, "Braga", faturasCC5);

    jc.add(cc);
    jc.add(cc2);
    jc.add(cc3);
    jc.add(cc4);

    ArrayList<Integer> agregado2 = new ArrayList<Integer>();
    ArrayList<Integer> direcao2 = new ArrayList<Integer>();

    direcao2.add(12);
    direcao2.add(124);


    HashMap<Integer, ArrayList<Factura>> exemplo2 = new HashMap<Integer, ArrayList<Factura>>();

    ArrayList<Factura> gerais = new ArrayList<Factura>();

    gerais.add(f8);
    gerais.add(f9);
    gerais.add(f10);
    gerais.add(f11);
    gerais.add(f12);
    gerais.add(f13);
    gerais.add(f16);
    gerais.add(f17);
    gerais.add(f18);
    gerais.add(f19);
    gerais.add(f20);
    gerais.add(f21);
    gerais.add(f22);

    exemplo2.put((f8.getNatureza()), gerais);
    exemplo2.put((f9.getNatureza()), gerais);
    exemplo2.put((f11.getNatureza()), gerais);
    exemplo2.put((f12.getNatureza()), gerais);
    exemplo2.put((f13.getNatureza()), gerais);
    exemplo2.put((f16.getNatureza()), gerais);
    exemplo2.put((f17.getNatureza()), gerais);
    exemplo2.put((f18.getNatureza()), gerais);
    exemplo2.put((f19.getNatureza()), gerais);
    exemplo2.put((f20.getNatureza()), gerais);
    exemplo2.put((f21.getNatureza()), gerais);
    exemplo2.put((f22.getNatureza()), gerais);

    gerais.add(f14);
    gerais.add(f15);
    gerais.add(f23);
    gerais.add(f24);
    exemplo2.put((f14.getNatureza()), gerais);
    exemplo2.put((f15.getNatureza()), gerais);
    exemplo2.put((f23.getNatureza()), gerais);
    exemplo2.put((f24.getNatureza()), gerais);



    ContribuinteIndividual ci1 = new ContribuinteIndividual(124, "ca@gmail.com", "Ana Santos", "Rua da Estrela", "sim123",
                                   2, 0, agregado2, direcao2,1.0, exemplo2);
    ContribuinteIndividual ci2 = new ContribuinteIndividual(12, "ca@gmail.com", "Rui Santos", "Rua da Estrela", "sim123",
                                   2, 0, agregado2, direcao2,1.0, exemplo2);

    jc.add(ci1);
    jc.add(ci2);

    assertEquals("Existtem 4 CC", 4, jc.getColetivos().size());
    assertEquals("Existem 2 CI", 2, jc.getIndividuais().size());
    List<ContribuinteColetivo> conjunto = jc.EmpresasComMaisFacturas(2);
    BigDecimal valorTotalDeducoes = jc.deducaoConjunta(conjunto);

     List<ContribuinteColetivo> expected = new ArrayList<>();
     expected.add(cc4);
     expected.add(cc3);

    assertEquals("A lista dos que emitiram mais facturas", expected, conjunto);
    assertEquals("O valor das deducoes de todas as facturas é", 59.5, valorTotalDeducoes.doubleValue(), 0.9);
    }

   /**
     * Método que testa se quais as X empresas com mais facturas
     *
     */
    @Test
    public void testFacturasPorContribuinte() {
    ArrayList<Integer> atividadesEmpresa4 = new ArrayList<>();
    ArrayList<Factura> faturasCC5 = new ArrayList<Factura>();

    atividadesEmpresa4.add(1);

    ArrayList<Integer> caes5 = new ArrayList<>();
    caes5.add(1);

    Factura f16 = new Factura(1223, 504, "Salsa&Lda",LocalDate.of(2018, 02, 13), 12, "Lavagem", Gerais.objeto.getCodigo(), new BigDecimal(40), caes5);
    Factura f17 = new Factura(12245, 504, "Salsa&Lda",LocalDate.of(2018, 5, 10), 12, "Pistões", Gerais.objeto.getCodigo(), new BigDecimal(80), caes5);
    Factura f18 = new Factura(12466, 504, "Salsa&Lda",LocalDate.of(2018, 5, 21), 12, "Pistões", Gerais.objeto.getCodigo(), new BigDecimal(20), caes5);
    Factura f19 = new Factura(1297, 504, "Salsa&Lda",LocalDate.of(2018, 5, 21), 12, "Pistões", Gerais.objeto.getCodigo(), new BigDecimal(18), caes5);
    Factura f20 = new Factura(12686, 504, "Salsa&Lda",LocalDate.of(2018, 02, 13), 12, "Lavagem", Gerais.objeto.getCodigo(), new BigDecimal(1), caes5);
    Factura f21 = new Factura(12568, 504, "Salsa&Lda",LocalDate.of(2018, 5, 10), 12, "Pistões", Gerais.objeto.getCodigo(), new BigDecimal(10), caes5);
    Factura f22 = new Factura(102, 504, "Salsa&Lda",LocalDate.of(2018, 5, 21), 12, "Pistões", Gerais.objeto.getCodigo(), new BigDecimal(100), caes5);
    Factura f23 = new Factura(1241, 504, "Salsa&Lda",LocalDate.of(2018, 5, 21), 124, "Pistões", Gerais.objeto.getCodigo(), new BigDecimal(10), caes5);
    Factura f24 = new Factura(1234, 504, "Salsa&Lda",LocalDate.of(2018, 5, 21), 124, "Pistões", Gerais.objeto.getCodigo(), new BigDecimal(19), caes5);

    faturasCC5.add(f16);
    faturasCC5.add(f17);
    faturasCC5.add(f18);
    faturasCC5.add(f19);
    faturasCC5.add(f20);
    faturasCC5.add(f21);
    faturasCC5.add(f22);
    faturasCC5.add(f23);
    faturasCC5.add(f24);

    ContribuinteColetivo  cc4 = new ContribuinteColetivo(504, "ola@gmail.com", "Motor&Lda", "Rua de Baixo", "ola", atividadesEmpresa4, 0.0, "Braga", faturasCC5);

    jc.add(cc4);

    List<Factura> faturas = new ArrayList<>();



    faturas = jc.faturasValorContribuinte(504, 12);

    List<Factura> expected = new ArrayList<>();
    expected.add(f22);
    expected.add(f17);
    expected.add(f16);
    expected.add(f18);
    expected.add(f19);
    expected.add(f21);
    expected.add(f20);

    assertEquals("A lista das facturas por contribuinte ordenadas por ordem decrescente ", expected, faturas);

    }

   /**
     * Método que testa as deduções de um setor.
     *
     */
    @Test
    public void testDeducaoTotalIndividuoSetor() {

        FamiliaNumerosa ci = new FamiliaNumerosa(ci_pai);
        BigDecimal valor = BigDecimal.ZERO;


        jc.add(ci);

        try{
        valor = jc.getDeducaoTotalIndividuoSetor(ci.getNIF(), Saude.objeto);
        }
        catch(CINullException e) {
        }

        BigDecimal expected = (new BigDecimal(500.00)).setScale(2, RoundingMode.DOWN);

        assertEquals("O valor total da deducao deve ser ", expected, valor);

    }

}
