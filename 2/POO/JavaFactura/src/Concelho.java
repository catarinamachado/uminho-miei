/**
 * Classe criada para associar um incentivo fiscal a
 * determinados concelhos de Portugal Continental.
 *
 * @author A81047
 * @author A34900
 * @author A82339
 * @version 20180513
 */

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.io.*;

public class Concelho implements Serializable {
     private String nome;
     private double beneficio;

     static private Map<String, Concelho> concelhos;

     /**
     * Construtor por omissão
     */
    public Concelho() {
        this.nome = "";
        this.beneficio = 0.0;
    }

    /**
     * Construtor parametrizado
     * @param nome Nome do concelho
     */
    public Concelho(String nome,double beneficio) {
        this.nome = nome;
        this.beneficio = beneficio;
    }

    /**
     * Construtor cópia
     */
    public Concelho(Concelho umConcelho) {
        this.nome = umConcelho.getNome();
        this.beneficio = umConcelho.getBeneficio();
    }

    /**
     * Devolve o nome do concelho.
     *
     * @return nome do concelho
     */
    public String getNome() {
        return nome;
    }

    /**
     * Estabelece o nome do concelho.
     *
     * @param nome Nome do concelho
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * Devolve o valor do incentivo fiscal concedido pelo concelho do interior.
     *
     * @return valor do incentivo fiscal
     */
    public double getBeneficio() {
        return beneficio;
    }

    /**
     * Estabelece o valor do incentivo fiscal concedido pelo concelho.
     *
     * @param beneficio valor do incentivo fiscal
     */
    public void setBeneficio(double beneficio) {
        this.beneficio = beneficio;
    }

    /**
     * Método que verifica se dois concelhos são iguais.
     *
     * @param object Objecto a ser usado como termo de comparação.
     *
     * @return Boolean indicando se os dois objetos são iguais
     */
    public boolean equals(Object object) {
        if (this == object)
            return true;

        if (object == null || (this.getClass() != object.getClass()))
            return false;

        Concelho umConcelho = (Concelho) object;
        return  (nome.equals(umConcelho.getNome()) && beneficio == umConcelho.getBeneficio());
    }

    /**
     * Método que devolve a representação em String de Concelho.
     *
     * @return String que representa um concelho
     */
    public String toString() {
        return "Nome =" + nome + '\'' + "Beneficio = " + beneficio + '\'';

    }

    /**
     * Método que faz uma cópia do concelho.
     * Para tal invoca o construtor de cópia.
     *
     * @return cópia do concelho.
     */
    public Concelho clone() {
        return new Concelho(this);
    }

    /**
     * Devolve valor de hash baseado no codigo da atividade económica.
     *
     * @return valor de hash
     */
    public int hashCode() {
        return java.util.Objects.hash(super.hashCode(), nome);
    }

    /**
     * Método que cria todos os concelhos disponíveis no sistema e associa um valor de beneficio
     * ao concelho e insere-os na hashmap atividades.
     *
     */

    public static void criarConcelhos() {
        Concelho[] todos = {new Concelho("Viana do Castelo", 0.0), new Concelho("Braga", 0.0),
                            new Concelho("Porto", 0.0), new Concelho("Aveiro", 0.0),
                            new Concelho("Coimbra", 0.0), new Concelho("Leiria", 0.0),
                            new Concelho("Lisboa", 0.0), new Concelho("Setúbal", 0.0),
                            new Concelho("Santarém", 0.0),new Concelho("Faro", 0.0),
                            new Concelho("Vila Real", 0.05),
                            new Concelho("Bragança", 0.10),
                            new Concelho("Viseu", 0.10),
                            new Concelho("Guarda", 0.10),
                            new Concelho("Castelo Branco", 0.05),
                            new Concelho("Portalegre", 0.05),
                            new Concelho("Évora", 0.05),
                            new Concelho("Beja", 0.10)};

        concelhos = new HashMap<>();

        for(Concelho umConcelho: todos) {
            concelhos.put(umConcelho.getNome(), umConcelho);
        }
    }

    /**
     * Método que devolve um concelho
     *
     * @param nome Nome do concelho que se pretende.
     *
     * @return um Concelho.
     */

    public static Concelho getConcelho(String nome) {
        return concelhos.get(nome).clone();
    }


}
