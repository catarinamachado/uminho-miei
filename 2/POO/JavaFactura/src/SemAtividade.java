/**
 * Classe SemAtividade, subclasse de AtividadesEconomicas.
 *
 * @author A81047
 * @author A34900
 * @author A82339
 * @version 20180513
 */

import java.math.BigDecimal;
import java.io.*;

public class SemAtividade extends AtividadesEconomicas implements Serializable {
    public static final SemAtividade objeto = new SemAtividade();

    /**
     * Construtor por omissão
     */
    public SemAtividade() {
        super(0, "SemAtividade", 0.00, new BigDecimal(0));
    }

    /**
     * Construtor cópia
     */
    public SemAtividade(SemAtividade atividade) {
        super(atividade.getCodigo(), atividade.getSetor(), atividade.getPercentagemDeducao(), atividade.getLimite());
    }

    /**
     * Método que faz uma cópia da atividade económica.
     * Para tal invoca o construtor de cópia.
     *
     * @return cópia da atividade económica.
     */
    public SemAtividade clone() {
        return new SemAtividade(this);
    }


}
