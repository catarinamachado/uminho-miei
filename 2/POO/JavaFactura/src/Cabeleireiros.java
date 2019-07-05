/**
 *Classe Cabeleireiros, subclasse de AtividadesEconomicas, não dá lugar a qualquer dedução fiscal.
 *
 * @author A81047
 * @author A34900
 * @author A82339
 * @version 20180513
 */

import java.math.BigDecimal;
import java.io.*;

public class Cabeleireiros extends AtividadesEconomicas implements Serializable {

    public static final Cabeleireiros objeto = new Cabeleireiros();
    /**
     * Construtor por omissão
     */
    public Cabeleireiros() {
        super(8, "Cabeleireiros", 0.0, new BigDecimal(0));
    }

    /**
     * Construtor cópia
     */
    public Cabeleireiros(Cabeleireiros atividade) {
        super(atividade.getCodigo(), atividade.getSetor(), atividade.getPercentagemDeducao(), atividade.getLimite());
    }

    /**
     * Método que faz uma cópia da atividade económica.
     * Para tal invoca o construtor de cópia.
     *
     * @return cópia da atividade económica.
     */
    public Cabeleireiros clone() {
        return new Cabeleireiros(this);
    }


}
