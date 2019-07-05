/**
 * Classe FacturaTeste, classe de testes.
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
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;
import java.time.LocalDate;
import java.math.RoundingMode;


public class ContribuinteIndTeste {
    ContribuinteIndividual ci;
    List<Integer> nifsDependentesAgregado;
    HashMap<Integer, ArrayList<Factura>> exemplo;
    private List<Integer> nifsDirecaoAgregado;


    /**
     * Construtor default para a classe de teste ContribuinteIndTeste
     */
    public ContribuinteIndTeste() {
    }

    /**
     * Define a hashmap exemplo, o arraylist agregado e povoa essas duas estruturas de dados,
     * cria todas as atividades económicas, bem como um contribuinte.
     *
     * Chamado antes de cada método de caso de teste.
     */
    @Before
    public void setUp() {
        exemplo = new HashMap<Integer, ArrayList<Factura>>();
        nifsDependentesAgregado = new ArrayList<Integer>();
        nifsDirecaoAgregado = new ArrayList<Integer>();

        AtividadesEconomicas.criarAtividades();

        nifsDirecaoAgregado.add(12);
        nifsDependentesAgregado.add(123);
        nifsDependentesAgregado.add(230607);

        ArrayList<Integer> caes = new ArrayList<>();
        caes.add(0);
        caes.add(1);

        ArrayList<Integer> caes2 = new ArrayList<>();
        caes2.add(2);

        ArrayList<Integer> caes3 = new ArrayList<>();
        caes3.add(4);


        Factura f1 = new Factura(12, 11, "Sapatos&Lda",LocalDate.of(2018, 5, 13), 123, "SapatosNica", Gerais.objeto.getCodigo(), new BigDecimal(10), caes);
        Factura f2 = new Factura(121, 511, "Sapatos&Lda",LocalDate.of(2018, 5, 01), 123, "SapatosNica", Gerais.objeto.getCodigo(), new BigDecimal(10), caes);
        Factura f3 = new Factura(200, 11, "Pharma.SA",LocalDate.of(2018, 3, 13), 123, "Bennurom", Saude.objeto.getCodigo(), new BigDecimal(4), caes2);
        Factura f4 = new Factura(409, 23, "Casa&Moradia.Lda",LocalDate.of(2018, 01, 13), 198, "Renda", Habitacao.objeto.getCodigo(), new BigDecimal(1000), caes3);

        ArrayList<Factura> gerais = new ArrayList<Factura>();
        gerais.add(f1);
        gerais.add(f2);

        ArrayList<Factura> saude = new ArrayList<Factura>();
        saude.add(f3);

        ArrayList<Factura> habitacao = new ArrayList<Factura>();
        habitacao.add(f4);

        exemplo.put((f1.getNatureza()), gerais);
        exemplo.put((f3.getNatureza()), saude);
        exemplo.put((f4.getNatureza()), habitacao);

        ci = new ContribuinteIndividual(123, "ca@gmail.com", "Ana Santos", "Rua da Estrela", "sim123",
                                        1, 2, (ArrayList<Integer>) nifsDependentesAgregado, (ArrayList<Integer>) nifsDirecaoAgregado,1.0, exemplo);
    }

    /**
     * Tears down the test fixture.
     *
     * Chamado após cada método de teste de caso.
     */
    @After
    public void tearDown()
    {
    }

    /**
     * Método que testa o construtor vazio da classe ContribuinteIndividual.
     *
     */
    @Test
    public void testContribuinteVazio() {
        ContribuinteIndividual ci = new ContribuinteIndividual();



        assertEquals("Numero de pessoas do agregado a quem incumbe a direção", ci.getN_direcao(), 0);
        assertEquals("Numero de dependentes tem que ser 0", ci.getN_dependentes(), 0);
        assertEquals("A lista com os nifs dos dependentes deve ser vazia", ci.getNifsDependentesAgregado(), new ArrayList<Integer>());
        assertEquals("A lista com os nifs de quem tem a direcao deve ser vazia", ci.getNifsDirecaoAgregado(), new ArrayList<Integer>());
        assertEquals("O coeficiente fiscal devve ser 1", 1.0, ci.getCoeficiente_fiscal(), 0.0);
        assertEquals("O map das faturas tem de ser vazio", ci.getGrupos_facturas(), new HashMap<AtividadesEconomicas, ArrayList<Factura>>());

    }

    /**
     * Método que testa o construtor parametrizado da classe ContribuinteIndividual.
     *
     */
     @Test
    public void testContribuinteParametrizado() {
        assertEquals("Numero de pessoas do agregado a quem incumbe a direção", ci.getN_direcao(), 1);
        assertEquals("Numero de dependentes tem que ser 2", ci.getN_dependentes(), 2);
        assertEquals("Lista de nifs dos dependentes", ci.getNifsDependentesAgregado(), nifsDependentesAgregado);
        assertEquals("Lista de nifs dos dependentes",  ci.getNifsDirecaoAgregado(), nifsDirecaoAgregado);
        assertEquals("O coeficiente fiscal tem que ser 1.0", 1.0, ci.getCoeficiente_fiscal(), 0.0);
        assertEquals("O map das faturas tem de ser povoado", ci.getGrupos_facturas(), exemplo);

    }

    /**
     * Método que testa o construtor cópia da classe ContribuinteIndividual.
     *
     */
     @Test
    public void testContribuinteCopia() {
        ContribuinteIndividual ci2 = new ContribuinteIndividual(ci);


        assertEquals("Numero de pessoas do agregado a quem incumbe a direção", ci.getN_direcao(), 1);
        assertEquals("Numero de dependentes tem que ser 2", ci.getN_dependentes(), 2);
        assertEquals("Lista de nifs dos dependentes", ci.getNifsDependentesAgregado(), nifsDependentesAgregado);
        assertEquals("Lista de nifs dos dependentes",  ci.getNifsDirecaoAgregado(), nifsDirecaoAgregado);
        assertEquals("O coeficiente fiscal tem que ser 1.0", 1.0, ci2.getCoeficiente_fiscal(), 0.0);
        assertEquals("O map das faturas tem de ser povoado", ci2.getGrupos_facturas(), exemplo);


        assertEquals("Objectos sao iguais", ci, ci2);
    }

    /**
     * Método que testa a alteração feita nos parametros do contribuinte.
     *
     */
    @Test
    public void testContribuinteSet() {
        HashMap<Integer, ArrayList<Factura>> exemplo2 = new HashMap<Integer, ArrayList<Factura>>();
        ArrayList<Integer> agregado2 = new ArrayList<Integer>();
        ArrayList<Integer> direcao2 = new ArrayList<Integer>();
        ArrayList<Integer> caes = new ArrayList<>();
        caes.add(0);
        caes.add(1);
        ArrayList<Integer> caes2 = new ArrayList<>();
        caes.add(2);
        ArrayList<Integer> caes3 = new ArrayList<>();
        caes.add(4);


        direcao2.add(13);
        agregado2.add(234);
        agregado2.add(23078);
        agregado2.add(230);

        Factura f1 = new Factura(8, 145, "Starbucks&Lda",LocalDate.of(2018, 5, 13), 123, "Cafe", Gerais.objeto.getCodigo(), new BigDecimal(0.7), caes);
        Factura f2 = new Factura(34, 500, "Mango&Lda",LocalDate.of(2018, 5, 01), 123, "Casaco", Gerais.objeto.getCodigo(), new BigDecimal(39), caes);
        Factura f3 = new Factura(69, 509, "Pharmatone.SA",LocalDate.of(2018, 3, 13), 123, "Bennurom", Saude.objeto.getCodigo(), new BigDecimal(4), caes2);
        Factura f4 = new Factura(445, 5087, "Apartamentos.Lda",LocalDate.of(2018, 01, 13), 198, "Renda", Habitacao.objeto.getCodigo(), new BigDecimal(500), caes3);

        ArrayList<Factura> gerais = new ArrayList<Factura>();
        gerais.add(f1);
        gerais.add(f2);

        ArrayList<Factura> saude = new ArrayList<Factura>();
        saude.add(f3);

        ArrayList<Factura> habitacao = new ArrayList<Factura>();
        habitacao.add(f4);

        AtividadesEconomicas.criarAtividades();



        exemplo2.put((f1.getNatureza()), gerais);
        exemplo2.put((f3.getNatureza()), saude);
        exemplo2.put((f4.getNatureza()), habitacao);

        ci.setN_direcao(2);
        ci.setN_dependentes(2);
        ci.setNifsDependentesAgregado(agregado2);
        ci.setNifsDirecaoAgregado(direcao2);
        ci.setCoeficiente_fiscal(1.2);
        ci.setGrupos_facturas(exemplo2);

        assertEquals("Numero de pessoas do agregado a quem incumbe a direção", ci.getN_direcao(), 2);
        assertEquals("Numero de dependentes tem que ser 2", ci.getN_dependentes(), 2);
        assertEquals("Lista de nifs dos dependentes", ci.getNifsDependentesAgregado(), agregado2);
        assertEquals("Lista de nifs dos dependentes",  ci.getNifsDirecaoAgregado(), direcao2);
        assertEquals("O coeficiente fiscal tem que ser 1.2", 1.2, ci.getCoeficiente_fiscal(), 0.0);
        assertEquals("O map das faturas tem de ser povoado", ci.getGrupos_facturas(), exemplo2);

    }

    /**
     * Método que testa a despesa por setor de um contribuinte.
     *
     */
    @Test
    public void testContribuinteGetDespesaTotalSetor() {

        BigDecimal totalsaude = ci.getDespesaTotalSetor(Saude.objeto.getCodigo());

        BigDecimal totalgerais = ci.getDespesaTotalSetor(Gerais.objeto.getCodigo());

        BigDecimal totalAutomoveis = ci.getDespesaTotalSetor(Automoveis.objeto.getCodigo());

        assertEquals("O valor da despesa do setor é ", totalsaude , new BigDecimal(4.00).setScale(2, RoundingMode.DOWN));
        assertEquals("O valor da despesa do setor é ", totalgerais , new BigDecimal(20.00).setScale(2, RoundingMode.DOWN));
        assertEquals("O valor da despesa do setor é ", totalAutomoveis , new BigDecimal(0));

    }

    /**
     * Método que testa a despesa por setor de um contribuinte.
     *
     */
    @Test
    public void testContribuinteGetDespesaTotalIndividuo() {
    assertEquals("O valor da despesa do individuo é ", ci.getDespesaTotalIndividuo(), new BigDecimal(1024.00).setScale(2, RoundingMode.DOWN));
    }

    /**
     * Método que testa a se uma factura é adicionada ao contribuinte
     *
     */
    @Test
    public void testContribuinteAddFacturaRegistada() {

        ArrayList<Integer> caes2 = new ArrayList<>();
        caes2.add(1);
        Factura factura = new Factura(49, 145, "Starbucks&Lda",LocalDate.of(2018, 5, 13), 123, "Cafe", Gerais.objeto.getCodigo(), new BigDecimal(0.7), caes2);

        ci.addFacturaRegistada(factura);

        HashMap<Integer, ArrayList<Factura>> exemplo2 = new HashMap<Integer, ArrayList<Factura>>();
        ArrayList<Integer> caes = new ArrayList<>();
        caes.add(0);
        caes.add(1);


        ArrayList<Integer> caes3 = new ArrayList<>();
        caes3.add(2);

        ArrayList<Integer> caes4 = new ArrayList<>();
        caes4.add(4);

        Factura f1 = new Factura(12, 11, "Sapatos&Lda",LocalDate.of(2018, 5, 13), 123, "SapatosNica", Gerais.objeto.getCodigo(), new BigDecimal(10), caes);
        Factura f2 = new Factura(121, 511, "Sapatos&Lda",LocalDate.of(2018, 5, 01), 123, "SapatosNica", Gerais.objeto.getCodigo(), new BigDecimal(10), caes);
        Factura f3 = new Factura(200, 11, "Pharma.SA",LocalDate.of(2018, 3, 13), 123, "Bennurom", Saude.objeto.getCodigo(), new BigDecimal(4), caes3);
        Factura f4 = new Factura(409, 23, "Casa&Moradia.Lda",LocalDate.of(2018, 01, 13), 198, "Renda", Habitacao.objeto.getCodigo(), new BigDecimal(1000), caes4);

        ArrayList<Factura> gerais = new ArrayList<Factura>();
        gerais.add(f1);
        gerais.add(f2);
        gerais.add(factura);

        ArrayList<Factura> saude = new ArrayList<Factura>();
        saude.add(f3);

        ArrayList<Factura> habitacao = new ArrayList<Factura>();
        habitacao.add(f4);

        exemplo2.put(Gerais.objeto.getCodigo(), gerais);
        exemplo2.put(Saude.objeto.getCodigo(), saude);
        exemplo2.put(Habitacao.objeto.getCodigo(), habitacao);


        assertEquals("O map das faturas tem de ser povoado", ci.getGrupos_facturas(), exemplo2);
    }

    /**
     * Método que testa se uma factura alterada é corretamente adicionada ao contribuinte.
     *
     */
    @Test
    public void testSetFacturasSetor() {

        HashMap<Integer, ArrayList<Factura>> exemplo2 = new HashMap<Integer, ArrayList<Factura>>();

        ArrayList<Integer> caes = new ArrayList<>();
        caes.add(1);

        ArrayList<Integer> caes2 = new ArrayList<>();
        caes2.add(2);

        ArrayList<Integer> caes3 = new ArrayList<>();
        caes3.add(4);

        Factura f1 = new Factura(8, 145, "Starbucks&Lda",LocalDate.of(2018, 5, 13), 123, "Cafe", Gerais.objeto.getCodigo(), new BigDecimal(0.7), caes);
        Factura f2 = new Factura(34, 500, "Mango&Lda",LocalDate.of(2018, 5, 01), 123, "Casaco", Gerais.objeto.getCodigo(), new BigDecimal(39.8), caes);
        Factura f3 = new Factura(69, 509, "Pharmatone.SA",LocalDate.of(2018, 3, 13), 123, "Bennurom", Saude.objeto.getCodigo(), new BigDecimal(4.980), caes2);
        Factura f4 = new Factura(445, 5087, "Apartamentos.Lda",LocalDate.of(2018, 01, 13), 198, "Renda", Habitacao.objeto.getCodigo(), new BigDecimal(500), caes3);

        ArrayList<Factura> gerais = new ArrayList<Factura>();
        gerais.add(f1);
        gerais.add(f2);

        ArrayList<Factura> saude = new ArrayList<Factura>();
        saude.add(f3);

        ArrayList<Factura> habitacao = new ArrayList<Factura>();
        habitacao.add(f4);

        exemplo2.put(Gerais.objeto.getCodigo(), gerais);
        exemplo2.put(Saude.objeto.getCodigo(), saude);
        exemplo2.put(Habitacao.objeto.getCodigo(), habitacao);

        ci.setFacturasSetor(gerais, Gerais.objeto.getCodigo());
        ci.setFacturasSetor(saude, Saude.objeto.getCodigo());
        ci.setFacturasSetor(habitacao, Habitacao.objeto.getCodigo());

        assertEquals("O setor geral das faturas foi settado", ci.getGrupos_facturas(), exemplo2);
    }
}
