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

public class ContribuinteTeste{
    /*
     * Classe criada para testar a classe contribuinte, já que esta é abstrata
     */
    class ContribuinteAnonimo extends Contribuinte {

        /**
        * Construtor vazio para a classe de teste.
        */
        public ContribuinteAnonimo() {
          super();
        }

        /**
        * Construtor parametrizado.
        */
        public ContribuinteAnonimo(int NIF, String email, String nome, String morada, String password) {
            super(NIF, email, nome, morada, password);
        }

        /**
        * Construtor cópia.
        */
        public ContribuinteAnonimo(ContribuinteAnonimo umContribuinte) {
            super(umContribuinte);
        }
        /**
        * Método que cria uma cópia do contribuinte anónimo.
        */
        public Contribuinte clone() {
            return new ContribuinteAnonimo(this);
        }
    }

    /**
     * Construtor default para a classe de teste ContribuinteTeste
     */
    public ContribuinteTeste() {
    }

    /**
     * Define a .
     *
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
     * Método que testa o construtor vazio da classe ContribuinteAnonimo.
     *
     */
    @Test
    public void testContribuinteVazio() {
        ContribuinteAnonimo ca = new ContribuinteAnonimo();

        assertEquals("NIF tem que ser zero", ca.getNIF(), 0);
        assertEquals("Email tem que ser vazio", ca.getEmail(), "");
        assertEquals("Nome tem que ser vazio", ca.getNome(), "");
        assertEquals("Morada tem que ser vazia", ca.getMorada(), "");
        assertEquals("Password tem que ser vazia", ca.getPassword(), "");
    }

    /**
     * Método que testa o construtor parametrizado da classe ContribuinteAnonimo.
     *
     */
     @Test
    public void testContribuinteParametrizado() {
        ContribuinteAnonimo ca = new ContribuinteAnonimo(123, "ca@gmail.com", "Ana Santos", "Rua da Estrela", "sim123");

        assertEquals("NIF tem que ser 123", ca.getNIF(), 123);
        assertEquals("Email tem que ser ca@gmai.com", ca.getEmail(),"ca@gmail.com" );
        assertEquals("Nome tem que ser Ana Santos", ca.getNome(), "Ana Santos");
        assertEquals("Morada tem que ser Rua da Estrela", ca.getMorada(), "Rua da Estrela");
        assertEquals("Password tem que ser sim123", ca.getPassword(), "sim123");
    }

    /**
     * Método que testa o construtor cópia da classe ContribuinteAnonimo.
     *
     */
     @Test
    public void testContribuinteCopia() {
        ContribuinteAnonimo ca = new ContribuinteAnonimo(123, "ca@gmail.com", "Ana Santos", "Rua da Estrela", "sim123");
        ContribuinteAnonimo ca2 = new ContribuinteAnonimo(ca);

        assertEquals("NIF tem que ser zero", ca2.getNIF(), 123);
        assertEquals("Email tem que ser vazio", ca2.getEmail(),"ca@gmail.com" );
        assertEquals("Nome tem que ser vazio", ca2.getNome(), "Ana Santos");
        assertEquals("Morada tem que ser vazia", ca2.getMorada(), "Rua da Estrela");
        assertEquals("Password tem que ser vazia", ca2.getPassword(), "sim123");

        assertEquals("Objectos sao iguais", ca, ca2);
    }

    /**
     * Método que testa a alteração dos parametros do  ContribuinteAnonimo.
     *
     */
    @Test
    public void testContribuinteSet() {
        ContribuinteAnonimo ca = new ContribuinteAnonimo(123, "ca@gmail.com", "Ana Santos", "Rua da Estrela", "sim123");

        ca.setNIF(20);
        ca.setEmail("contribuinte@gmail.com");
        ca.setNome("Lucas Sousa");
        ca.setMorada("Avenida da Rua");
        ca.setPassword("ola");

        assertEquals("NIF tem que ser zero", ca.getNIF(), 20);
        assertEquals("Email tem que ser vazio", ca.getEmail(),"contribuinte@gmail.com");
        assertEquals("Nome tem que ser vazio", ca.getNome(), "Lucas Sousa");
        assertEquals("Morada tem que ser vazia", ca.getMorada(), "Avenida da Rua");
        assertEquals("Password tem que ser vazia", ca.getPassword(), "ola");
    }
}
