/**
 * Classe Gerais, subclasse de AtividadesEconomicas.
 *
 * @author A81047
 * @author A34900
 * @author A82339
 * @version 20180513
 */

import java.math.BigDecimal;
import java.io.*;

public class Gerais extends AtividadesEconomicas implements Serializable {

        public static final Gerais objeto = new Gerais();

    /**
     * Construtor por omissão
     */
    public Gerais() {
        super(1, "Gerais", 0.35, new BigDecimal(100));
    }

    /**
     * Construtor cópia
     */
    public Gerais(Gerais atividade) {
        super(atividade.getCodigo(), atividade.getSetor(), atividade.getPercentagemDeducao(), atividade.getLimite());
    }

    /**
     * Método que faz uma cópia da atividade económica.
     * Para tal invoca o construtor de cópia.
     *
     * @return cópia da atividade económica.
     */
    public Gerais clone() {
        return new Gerais(this);
    }

}
