/**
 * A classe família numerosa é constituída por um agregado familiar com mais de quatro dependentes.
 *
 * @author A81047
 * @author A34900
 * @author A82339
 * @version 20180513
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.io.*;

public class FamiliaNumerosa extends ContribuinteIndividual implements Serializable, IncentivoFiscal{

     static private int familiaNumerosa = 5;

     /**
      * Construtor parametrizado
      *
      *
      * @param NIF Identificador
      * @param email Endereço de email
      * @param nome Nome
      * @param morada Morada
      * @param password Password
      * @param n_agregado Número do agregado
      * @param n_dependentes Número de dependentes
      * @param nifs_agregado NIF de cada um dos membros do agregado
      * @param coeficiente_fiscal Coeficiente Fiscal
      * @param grupos_facturas Facturas emitidas em nome do contribuinte por setor de atividade
      */

     public FamiliaNumerosa(ContribuinteIndividual familia) {

       super(familia.getNIF(), familia.getEmail(), familia.getNome(), familia.getMorada(), familia.getPassword(),
             familia.getN_direcao(), familia.getN_dependentes(), familia.getNifsDependentesAgregado(),
              familia.getNifsDirecaoAgregado(), familia.getCoeficiente_fiscal(), familia.getGrupos_facturas());


    }

    /**
     * Método que devolve a representação em String de uma Familia Numerosa.
     *
     * @return String que representa uma familia numerosa
     */
    public String toString() {
        StringBuilder facturas = new StringBuilder();

            for (ArrayList<Factura> setor: getGrupos_facturas().values()) {
                for(Factura umaFactura : setor) {
                    facturas.append(umaFactura.toString());
                 }
            }


        return "FamiliaNumerosa {" +
                super.toString() +
                ", Número de contribuintes na direção do Agregado= " + getN_direcao() +
                ", Número de dependentes do Agregado= " + getN_dependentes() +
                ", NIFs da direção do Agregado =" + getNifsDependentesAgregado().toString() +
                ", NIFs dos dependentes do Agregado =" + getNifsDirecaoAgregado().toString() +
                ", Coeficiente Fiscal =" + getCoeficiente_fiscal() +
                ", Facturas =" + facturas.toString() +
                '}';
    }

    /**
     * Método que faz uma cópia da familia numerosa receptor da mensagem.
     * Para tal invoca o construtor de cópia do contribuinte individual.
     *
     * @return cópia da familia numerosa
     */
    public FamiliaNumerosa clone() {
        return new FamiliaNumerosa(this);
    }

     /**
     * Devolve número de dependentes de uma família considerada numerosa.
     *
     * @return numero de dependentes
     */
    static public int getFamiliaNumerosa() {
        return familiaNumerosa;
    }

    /**
     * Atualiza número de dependentes necessários para constituir uma família numerosa.
     *
     * @param dependentes número de dependentes do agregado
     */
    static public void setFamiliaNumerosa(int dependentes) {
        familiaNumerosa = dependentes;
    }

    /**
     * Determina se uma família é numerosa.
     *
     * @return boleano que nos diz se a família é ou não numerosa para efeitos fiscais.
     */
    public boolean familiaNumerosa(){

        if (getN_dependentes() >= FamiliaNumerosa.familiaNumerosa)
            return true;
        return false;
    }


    /**
     * Determina o valor de reducão fiscal de uma familia. Se a familia for numerosa
     * tem direito a uma bonificação de 5% por cada dependente.
     *
     * @return reducao do imposto a pagar.
     */
    public double reducaoImposto() {
        double reducao = 1.0;

        if(familiaNumerosa()) {
            reducao = 0.05 * getN_dependentes();
        }

        return reducao;
    }
}
