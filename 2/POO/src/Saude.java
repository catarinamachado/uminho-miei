/**
 * Classe Saude, subclasse de AtividadesEconomicas.
 *
 * @author A81047
 * @author A34900
 * @author A82339
 * @version 20180513
 */

import java.math.BigDecimal;
import java.io.*;

public class Saude extends AtividadesEconomicas implements Serializable {

    public static final Saude objeto = new Saude();

    /**
     * Construtor por omissão
     */
    public Saude() {
        super(2, "Saude", 0.15, new BigDecimal(400));
    }

    /**
     * Construtor cópia
     */
    public Saude(Saude atividade) {
        super(atividade.getCodigo(), atividade.getSetor(), atividade.getPercentagemDeducao(), atividade.getLimite());
    }

    /**
     * Método que faz uma cópia da atividade económica.
     * Para tal invoca o construtor de cópia.
     *
     * @return cópia da atividade económica.
     */
    public Saude clone() {
        return new Saude(this);
    }

    /**
     * Devolve o valor da dedução após realizar uma despesa desta classe.
     *
     *@param valorDespesa Valor total da despesa.
     *@param ci um contribuinte individual.
     *@param cc um contribuinte  coletivo.
     *
     * @return valor da dedução
     */
   public BigDecimal calculaDeducao(BigDecimal valorDespesa, ContribuinteIndividual ci, ContribuinteColetivo cc) {

        BigDecimal valor = valorDespesa.multiply(new BigDecimal(ci.getCoeficiente_fiscal())).
                        multiply(new BigDecimal(getPercentagemDeducao()));

        return valor;

    }


    /**
     * Verifica se determinada despesa do contribuinte ultrapassa o limite de dedução previsto.
     * Se o numero de dependentes for maior do que quatro pessoas o limite do setor tem um aumento
     * de acordo com o numero de dependentes (5% por cada dependente).
     *
     *@param valorDespesa Valor total da despesa.
     *@param ci um contribuinte individual.
     *
     * @return o valor do novo Limite
     */

    public BigDecimal calculaLimite(ContribuinteIndividual ci, BigDecimal valor) {

        BigDecimal novoLimite = BigDecimal.ZERO;

          if(ci instanceof FamiliaNumerosa){
                FamiliaNumerosa familia = (FamiliaNumerosa)ci;
                novoLimite =(new BigDecimal(familia.reducaoImposto())).multiply(getLimite());
                novoLimite = novoLimite.add(getLimite());

                int res = valor.compareTo(novoLimite);
                if(res == 1) {
                     valor = novoLimite;
                }


            }

            else {

                int res = valor.compareTo(getLimite());
                if(res == 1) {
                    valor = getLimite();
                }
            }

          return valor;
    }

}
