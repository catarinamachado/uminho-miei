/**
 * Classe abstrata que implementa os métodos comuns às Atividades Economicas.
 *
 * @author A81047
 * @author A34900
 * @author A82339
 * @version 20180513
 */

import java.math.BigDecimal;
import java.util.Map;
import java.util.HashMap;
import java.io.*;

public abstract class AtividadesEconomicas implements Serializable {
   private int codigo;
   private String setor;
   private double percentagem_deducao;
   private BigDecimal limite_deducao;


   static private Map<Integer, AtividadesEconomicas> atividades;

    /**
     * Construtor por omissão
     */
    public AtividadesEconomicas() {
        this.codigo = 0;
        this.setor = "";
        this.percentagem_deducao = 0;
        this.limite_deducao = BigDecimal.ZERO;
    }

    /**
     * Construtor parametrizado
     * @param codigo Setor da atividade económica
     * @param setor Setor da atividade económica
     * @param percentagem_deducao percentagem da dedução
     * @param limite_deducao Limite máximo da deducao
     */
    public AtividadesEconomicas(int codigo, String setor, double percentagem_deducao, BigDecimal limite_deducao) {
        this.codigo = codigo;
        this.setor = setor;
        this.percentagem_deducao = percentagem_deducao;
        this.limite_deducao = limite_deducao;
    }

    /**
     * Construtor cópia
     */
    public AtividadesEconomicas(AtividadesEconomicas umaAtividade) {
        this.codigo = umaAtividade.getCodigo();
        this.setor = umaAtividade.getSetor();
        this.percentagem_deducao = umaAtividade.getPercentagemDeducao();
        this.limite_deducao = umaAtividade.getLimite();
    }

    /**
     * Devolve o código do setor da atividade económica.
     *
     * @return setor
     */
    public int getCodigo() {
        return codigo;
    }

    /**
     * Devolve o setor da atividade económica.
     *
     * @return setor
     */
    public String getSetor() {
        return setor;
    }

    /**
     * Devolve a percentagem que um contribuinte pode deduzir.
     *
     * @return percentagem da dedução
     */
    public double getPercentagemDeducao() {
        return percentagem_deducao;
    }

    /**
     * Devolve o limite máximo da dedução de um dado setor de atividade.
     *
     * @return valor do limite de determinada dedução
     */
    public BigDecimal getLimite() {
        return limite_deducao;
    }

    /**
     * Estabelece o limite máximo da dedução de um dado setor de atividade.
     *
     * @param limite valor do limite de determinada dedução
     */
    public void setLimite(BigDecimal limite) {
         this.limite_deducao = limite;
    }

    /**
     * Método que posterga a cópia da dedução relativa à atividade económica.
     */
    public abstract AtividadesEconomicas clone();

    /**
     * Método que verifica se duas atividades são iguais.
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

        AtividadesEconomicas umaAtividade = (AtividadesEconomicas) object;
        return  (codigo == umaAtividade.getCodigo() &&
                setor.equals(umaAtividade.getSetor()) &&
                percentagem_deducao == umaAtividade.getPercentagemDeducao() &&
                limite_deducao.equals(umaAtividade.getLimite()));
    }

    /**
     * Método que devolve a representação em String de Atividades Economicas.
     *
     * @return String que representa uma atividade
     */
    public String toString() {
        return  "Código =" + codigo + '\'' +
                "Setor =" + setor + '\'' +
                ", Percentagem da Dedução ='" + percentagem_deducao + '\'' +
                ", Limite da dedução ='" + limite_deducao + '\'';

    }

    /**
     * Devolve valor de hash baseado no codigo da atividade económica.
     *
     * @return valor de hash
     */
    public int hashCode() {
        return java.util.Objects.hash(super.hashCode(), codigo);
    }

    /**
     * Devolve o valor da dedução após realizar uma despesa.
     *
     *@param valorDespesa Valor total da despesa.
     *@param ci contribuinte individual.
     *@param cc contribuinte coletivo.
     *
     * @return valor da dedução
     */
    public BigDecimal calculaDeducao(BigDecimal valorDespesa, ContribuinteIndividual ci, ContribuinteColetivo cc) {
        return valorDespesa.multiply(new BigDecimal(ci.getCoeficiente_fiscal())).
                multiply(new BigDecimal(getPercentagemDeducao()));

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

    /**
     * Cria todas as atividades económcias disponíveis no sistema.
     *
     */
    public static void criarAtividades() {
        AtividadesEconomicas[] todas = {SemAtividade.objeto, Gerais.objeto, Saude.objeto, Educacao.objeto,
                                        Lares.objeto, Habitacao.objeto, Cabeleireiros.objeto, Automoveis.objeto,
                                        Motociclos.objeto, Veterinaria.objeto, PassesTransportes.objeto, Restauracao.objeto};

        atividades = new HashMap<>();

        for(AtividadesEconomicas umaAtividade: todas) {
            atividades.put(umaAtividade.getCodigo(), umaAtividade);
        }
    }

    /**
     * Devolve uma atividade económcia disponível no sistema.
     *
     **@param codigo Numero que identifica a atividade economica.
     *
     * @return uma Atividade Económica.
     *
     */
    public static AtividadesEconomicas getAtividade(int codigo) {
        return atividades.get(codigo);
    }

}
