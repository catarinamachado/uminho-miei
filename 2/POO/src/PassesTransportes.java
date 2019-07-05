/**
 * Classe PassesTransportes, subclasse de AtividadesEconomicas.
 *
 * @author A81047
 * @author A34900
 * @author A82339
 * @version 20180513
 */

import java.math.BigDecimal;
import java.io.*;

public class PassesTransportes extends AtividadesEconomicas implements Serializable {

    public static final PassesTransportes objeto = new PassesTransportes();

    /**
     * Construtor por omissão
     */
    public PassesTransportes() {
        super(11, "PassesTransportes", 0.05, new BigDecimal(100));
    }

    /**
     * Construtor cópia
     */
    public PassesTransportes(PassesTransportes atividade) {
        super(atividade.getCodigo(), atividade.getSetor(), atividade.getPercentagemDeducao(), atividade.getLimite());
    }

    /**
     * Método que faz uma cópia da atividade económica.
     * Para tal invoca o construtor de cópia.
     *
     * @return cópia da atividade económica.
     */
    public PassesTransportes clone() {
        return new PassesTransportes(this);
    }

    /**
     * Devolve o valor da dedução após realizar uma despesa desta classe. Se a empresa que
     * emitiu a fatura estiver sedidada num concelho do interior o valor da dedução aumenta consoante
     * o valor do beneficio concedido.
     *
     *@param valorDespesa Valor total da despesa.
     *@param ci contribuinte individual.
     *@param cc contribuinte coletivo.
     *
     * @return valor da dedução
     */
    public BigDecimal calculaDeducao(BigDecimal valorDespesa, ContribuinteIndividual ci, ContribuinteColetivo cc) {
        double deducao = cc.reducaoImposto() + getPercentagemDeducao();


        BigDecimal valor = valorDespesa.multiply(new BigDecimal(ci.getCoeficiente_fiscal())).
                           multiply(new BigDecimal(deducao));

        return valor;
    }


    /**
     *
     * Verifica se determinada despesa do contribuinte ultrapassa o limite de dedução previsto.
     *
     *@param valor Valor da despesa.
     *@param ci um contribuinte individual.
     *
     * @return o valor do novo Limite
     */
    public BigDecimal calculaLimite(ContribuinteIndividual ci, BigDecimal valor) {
        int resultado = valor.compareTo(getLimite());

        if(resultado == 1) {
            valor = getLimite();
        }


        return valor;
    }
}
