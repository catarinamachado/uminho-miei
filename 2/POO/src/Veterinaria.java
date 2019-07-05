/**
 * Classe Veterinaria, subclasse de AtividadesEconomicas.
 *
 * @author A81047
 * @author A34900
 * @author A82339
 * @version 20180513
 */

import java.math.BigDecimal;
import java.io.*;

public class Veterinaria extends AtividadesEconomicas implements Serializable {

    public static final Veterinaria objeto = new Veterinaria();

    public Veterinaria() {
        super(10, "Veterinaria", 0.05, new BigDecimal(100));
    }

    public Veterinaria(Veterinaria atividade) {
        super(atividade.getCodigo(), atividade.getSetor(), atividade.getPercentagemDeducao(), atividade.getLimite());
    }

    /**
     * Método que faz uma cópia da atividade económica.
     * Para tal invoca o construtor de cópia.
     *
     * @return cópia da atividade económica.
     */
    public Veterinaria clone() {
        return new Veterinaria(this);
    }

}
