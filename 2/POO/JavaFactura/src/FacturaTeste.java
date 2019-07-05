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
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;

public class FacturaTeste{
    /**
     * Construtor default para a classe de teste FacturaTeste
     */
    public FacturaTeste() {
    }

    /**
     * Chamado antes de cada método de caso de teste.
     */
    @Before
    public void setUp() {
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
     * Método que testa o construtor vazio da classe Factura.
     *
     */
    @Test
    public void testFacturaVazia() {
        Factura f = new Factura();

        assertEquals("Nif do emitente tem que ser 0", f.getNif_emitente(), 0);
        assertEquals("Designação tem que ser vazia", f.getDesignacao(), "");
        assertEquals("Data tem que ser vazia", f.getData(), LocalDate.now());
        assertEquals("Nif do cliente tem que ser 0", f.getNif_cliente(), 0);
        assertEquals("Descrição tem que ser vazia", f.getDescricao(), "");
        assertEquals("Natureza tem que ser 0", f.getNatureza(), 0);
        assertEquals("Valor tem que ser 0", f.getValor(), BigDecimal.ZERO);
        assertEquals("Valor tem que ser 0", f.getHistorico_caes(), new ArrayList<>());
    }

    /**
     * Método que testa o construtor parametrizado da classe Factura.
     *
     */
    @Test
    public void testFacturaParametrizado() {
        ArrayList<Integer> caes = new ArrayList<>();
        caes.add(0);
        caes.add(1);

        Factura f = new Factura(12,11, "Sapatos&Lda",LocalDate.of(2018, 5, 13), 123, "SapatosNica", 1, new BigDecimal(10), caes);



        assertEquals("Id tem de ser 12", f.getId(), 12);
        assertEquals("Nif do emitente tem que ser 11", f.getNif_emitente(), 11);
        assertEquals("Designação tem que ser Sapatos&Lda", f.getDesignacao(), "Sapatos&Lda");
        assertEquals("Data tem que ser 2018-13-05", f.getData(), LocalDate.of(2018, 05, 13));
        assertEquals("Nif do cliente tem que ser 123", f.getNif_cliente(), 123);
        assertEquals("Descrição tem que ser SapatosNica", f.getDescricao(), "SapatosNica");
        assertEquals("Natureza tem que ser 1", f.getNatureza(), 1);
        assertEquals("Valor tem que ser 10", f.getValor(), new BigDecimal(10));
    }

    /**
     * Método que testa o construtor cópia da classe Factura.
     *
     */
    @Test
    public void testFacturaCopia() {
        ArrayList<Integer> caes = new ArrayList<>();
        caes.add(0);
        caes.add(1);
        Factura f = new Factura(12, 11, "Sapatos&Lda",LocalDate.of(2018, 5, 13), 123, "SapatosNica", 1, new BigDecimal(10), caes);
        Factura f2 = new Factura(f);


        assertEquals("Id tem de ser 12", f2.getId(), 12);
        assertEquals("Nif do emitente tem que ser 11", f2.getNif_emitente(), 11);
        assertEquals("Designação tem que ser Sapatos&Lda", f2.getDesignacao(), "Sapatos&Lda");
        assertEquals("Data tem que ser 2018-13-05", f2.getData(), LocalDate.of(2018, 05, 13));
        assertEquals("Nif do cliente tem que ser 123", f2.getNif_cliente(), 123);
        assertEquals("Descrição tem que ser SapatosNica", f2.getDescricao(), "SapatosNica");
        assertEquals("Natureza tem que ser 1", f2.getNatureza(), 1);
        assertEquals("Valor tem que ser 10", f2.getValor(), new BigDecimal(10));

        assertEquals("Objectos sao iguais", f, f2);
    }

    /**
     * Método que testa a alteração feita nos parametros da factura.
     *
     */
    @Test
    public void testFacturaSet() {
        ArrayList<Integer> caes = new ArrayList<>();
        caes.add(0);
        caes.add(1);
        Factura f = new Factura(12, 500, "Sapatos&Lda",LocalDate.of(2018, 5, 13), 123, "SapatosNica", 1, new BigDecimal(10), caes);

        f.setId(20);
        f.setNif_emitente(12);
        f.setDesignacao("Pharma.SA");
        f.setData(LocalDate.of(2018, 03, 01));
        f.setNif_cliente(12);
        f.setDescricao("Bennurom");
        f.setNatureza(2);
        f.setValor(new BigDecimal(4.329));


        assertEquals("Id tem de ser 20", f.getId(), 20);
        assertEquals("Nif do emitente tem que ser 11", f.getNif_emitente(), 12);
        assertEquals("Designação tem que ser Pharma.SA", f.getDesignacao(), "Pharma.SA");
        assertEquals("Data tem que ser 2018-01-03", f.getData(), LocalDate.of(2018, 3, 01));
        assertEquals("Nif do cliente tem que ser 12", f.getNif_cliente(), 12);
        assertEquals("Descrição tem que ser Bennurom", f.getDescricao(), "Bennurom");
        assertEquals("Natureza tem que ser 2", f.getNatureza(), 2);
        assertEquals("Valor tem que ser 4,329", f.getValor(), new BigDecimal(4.329));

    }

}
