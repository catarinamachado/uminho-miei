/**
 * Classe Lares, subclasse de AtividadesEconomicas.
 *
 * @author A81047
 * @author A34900
 * @author A82339
 * @version 20180513
 */

import java.math.BigDecimal;
import java.io.*;

public class Lares extends AtividadesEconomicas implements Serializable {

    public static final Lares objeto = new Lares();
    /**
     * Construtor por omissão
     */
    public Lares() {
        super(5, "Lares", 0.25, new BigDecimal(400));
    }

    /**
     * Construtor cópia
     */
    public Lares(Lares atividade) {
        super(atividade.getCodigo(), atividade.getSetor(), atividade.getPercentagemDeducao(), atividade.getLimite());
    }

    /**
     * Método que faz uma cópia da atividade económica.
     * Para tal invoca o construtor de cópia.
     *
     * @return cópia da atividade económica.
     */
    public Lares clone() {
        return new Lares(this);
    }

}
