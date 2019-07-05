/**
 * Classe Restauracao, subclasse de AtividadesEconomicas.
 *
 * @author A81047
 * @author A34900
 * @author A82339
 * @version 20180513
 */

import java.math.BigDecimal;
import java.io.*;

public class Restauracao extends AtividadesEconomicas implements Serializable {

    public static final Restauracao objeto = new Restauracao();

    public Restauracao() {
        super(7, "Restauracao", 0.05, new BigDecimal(100));
    }

    public Restauracao(Restauracao atividade) {
        super(atividade.getCodigo(), atividade.getSetor(), atividade.getPercentagemDeducao(), atividade.getLimite());
    }

    public Restauracao clone() {
        return new Restauracao(this);
    }

    /**
     * Devolve o valor da dedução após realizar uma despesa desta classe. Se a empresa que
     * emitiu a fatura estiver sedidada num concelho do interior o valor da dedução aumenta.
     *
     *@param valorDespesa Valor total da despesa.
     *@param ci contribuinte individual.
     *@param cc contribuinte coletivo.
     *
     * @return valor da dedução
     */
    public BigDecimal calculaDeducao(BigDecimal valorDespesa, ContribuinteIndividual ci, ContribuinteColetivo cc) {
        double deducao = cc.reducaoImposto() + getPercentagemDeducao();

        //multiplica pelo coeficiente fiscal e pela deducao
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
