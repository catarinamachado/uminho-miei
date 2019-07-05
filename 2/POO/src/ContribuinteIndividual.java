/**
 * Contribuinte Individual, extensão da classe Contribuinte,
 * é uma entidade sobre a qual apenas é emitida despesa.
 *
 * @author A81047
 * @author A34900
 * @author A82339
 * @version 20180513
 */

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.math.RoundingMode;
import java.io.*;

public class ContribuinteIndividual extends Contribuinte implements Serializable {
    private int n_direcao;
    private int n_dependentes;
    private List<Integer> nifsDependentesAgregado;
    private List<Integer> nifsDirecaoAgregado;
    private double coeficiente_fiscal;
    private Map<Integer, ArrayList<Factura>> grupos_facturas; //Cada inteiro representa um setor de atividade onde guarda um arraylist com as faturas


    /**
     * Construtor por omissão de Contribuinte Individual.
     */
    public ContribuinteIndividual() {
        super();
        this.n_direcao = 0;
        this.n_dependentes = 0;
        this.nifsDependentesAgregado = new ArrayList<>();
        this.nifsDirecaoAgregado = new ArrayList<>();
        this.coeficiente_fiscal = 1;
        this.grupos_facturas = new HashMap<>();
    }

    /**
     * Construtor parametrizado de Contribuinte Individual.
     *
     * @param NIF Identificador
     * @param email Endereço de email
     * @param nome Nome
     * @param morada Morada
     * @param password Password
     * @param n_agregado Número do agregado
     * @param  n_dependentes Número de dependentes;
     * @param nifs_agregado NIF de cada um dos membros do agregado
     * @param coeficiente_fiscal Coeficiente Fiscal
     * @param grupos_facturas Facturas emitidas em nome do contribuinte por setor de atividade
     */
    public ContribuinteIndividual(int NIF, String email, String nome, String morada, String password,
                                  int n_direcao, int n_dependentes, ArrayList<Integer> nifsDependentesAgregado,
                                  ArrayList<Integer> nifsDirecaoAgregado, double coeficiente_fiscal,
                                  Map <Integer, ArrayList<Factura>> grupo_facturas) {
        super(NIF, email, nome, morada, password);
        this.n_direcao = n_direcao;
        this.n_dependentes = n_dependentes;
        setNifsDependentesAgregado(nifsDependentesAgregado);
        setNifsDirecaoAgregado(nifsDirecaoAgregado);
        this.coeficiente_fiscal = coeficiente_fiscal;
        setGrupos_facturas(grupo_facturas);
    }

    /**
     * Construtor de Cópia.
     *
     * @param umCI Contribuinte Individual a ser replicado
     */
    public ContribuinteIndividual(ContribuinteIndividual umCI) {
        super(umCI.getNIF(), umCI.getEmail(), umCI.getNome(), umCI.getMorada(), umCI.getPassword());
        this.n_direcao = umCI.getN_direcao();
        this.n_dependentes = umCI.getN_dependentes();
        this.nifsDependentesAgregado = umCI.getNifsDependentesAgregado();
        this.nifsDirecaoAgregado = umCI.getNifsDirecaoAgregado();
        this.coeficiente_fiscal = umCI.getCoeficiente_fiscal();
        this.grupos_facturas = umCI.getGrupos_facturas();
    }

    /**
     * Devolve o número de pessoas na direção do agregado.
     *
     * @return n_direcao
     */
    public int getN_direcao() {
        return n_direcao;
    }

    /**
     * Atualiza o número de pessoas na direção do agregado.
     *
     * @param n_direcao número de pessoas na direção do agregado
     */
    public void setN_direcao(int n_direcao) {
        this.n_direcao = n_direcao;
    }

    /**
     * Devolve número de dependentes do contribuinte.
     *
     * @return n_depedentes
     */
    public int getN_dependentes() {
        return n_dependentes;
    }

    /**
     * Atualiza número de dependentes do contribuinte.
     *
     * @param n_dependentes número de dependentes do contribuinte
     */
    public void setN_dependentes(int n_dependentes) {
        this.n_dependentes = n_dependentes;
    }

    /**
     * Devolve NIFs dos dependentes do agregado.
     *
     * @return nifsDependentesAgregado
     */
    public ArrayList<Integer> getNifsDependentesAgregado() {
        return nifsDependentesAgregado.stream().
                  collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Atualiza NIFs dos dependentes do agregado.
     *
     * @param nifsDependentesAgregado
     */
    public void setNifsDependentesAgregado(ArrayList<Integer> nifsDependentesAgregado) {
        this.nifsDependentesAgregado = nifsDependentesAgregado.stream().
            collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Devolve NIFs da direção do agregado.
     *
     * @return nifsDirecaoAgregado
     */
    public ArrayList<Integer> getNifsDirecaoAgregado() {
        return nifsDirecaoAgregado.stream().
                  collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Atualiza NIFs da direção do agregado.
     *
     * @param nifsDirecaoAgregado
     */
    public void setNifsDirecaoAgregado(ArrayList<Integer> nifsDirecaoAgregado) {
        this.nifsDirecaoAgregado = nifsDirecaoAgregado.stream().
            collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Devolve coeficiente fiscal.
     *
     * @return coeficiente_fiscal
     */
    public double getCoeficiente_fiscal() {
        return coeficiente_fiscal;
    }

    /**
     * Atualiza valor do coeficiente fiscal.
     *
     * @param coeficiente_fiscal coeficiente fiscal
     */
    public void setCoeficiente_fiscal(double coeficiente_fiscal) {
        this.coeficiente_fiscal = coeficiente_fiscal;
    }


    /**
     * Devolve facturas do contribuinte
     *
     * @return grupos_facturas
     */
    public Map<Integer, ArrayList<Factura>> getGrupos_facturas() {
        return this.grupos_facturas.entrySet().stream().
                            collect(Collectors.toMap(f -> f.getKey(),f -> f.getValue().stream().
                            collect(Collectors.toCollection(ArrayList::new))));
    }

    /**
     * Atualiza facturas do contribuinte.
     *
     * @param grupos_facturas facturas do contribuinte
     */
    public void setGrupos_facturas(Map<Integer,ArrayList<Factura>> grupos_facturas) {
        this.grupos_facturas = grupos_facturas.entrySet().stream().
                        collect(Collectors.toMap(f -> f.getKey(),f -> f.getValue().stream().
                        collect(Collectors.toCollection(ArrayList::new))));
    }

    /**
     * Método que determina se dois contribuintes individuais são iguais.
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

        ContribuinteIndividual contribuinte = (ContribuinteIndividual) object;

        return  super.equals(contribuinte) &&
                this.n_direcao == contribuinte.getN_direcao() &&
                this.n_dependentes == contribuinte.getN_dependentes() &&
                this.nifsDependentesAgregado.equals(contribuinte.getNifsDependentesAgregado()) &&
                this.nifsDirecaoAgregado.equals(contribuinte.getNifsDirecaoAgregado()) &&
                Double.valueOf(this.coeficiente_fiscal).
                      equals(Double.valueOf(contribuinte.getCoeficiente_fiscal())) &&
                this.grupos_facturas.equals(contribuinte.getGrupos_facturas());
    }

    /**
     * Método que devolve a representação em String de Contribuinte Individual.
     *
     * @return String que representa um contribuinte individual
     */
    public String toString() {
        StringBuilder facturas = new StringBuilder();

            for (ArrayList<Factura> setor: grupos_facturas.values()) {
                for(Factura umaFactura : setor) {
                    facturas.append(umaFactura.toString());
                 }
            }


        return "ContribuinteIndividual {" +
                super.toString() +
                ", Número de contribuintes na direção do Agregado= " + n_direcao +
                ", Número de dependentes do Agregado= " + n_dependentes +
                ", NIFs da direção do Agregado =" + nifsDependentesAgregado.toString() +
                ", NIFs dos dependentes do Agregado =" + nifsDirecaoAgregado.toString() +
                ", Coeficiente Fiscal =" + coeficiente_fiscal +
                ", Facturas =" + facturas.toString() +
                '}';
    }

    /**
     * Método que faz uma cópia do contribuinte individual receptor da mensagem.
     * Para tal invoca o construtor de cópia.
     *
     * @return cópia do contribuinte
     */
    public ContribuinteIndividual clone() {
        return new ContribuinteIndividual(this);
    }

    /**
     * Devolve valor total das facturas do contribuinte.
     *
     * @return valor_total valor total das facturas
     */
    public BigDecimal getValorTotal() {
        BigDecimal valorTotal = new BigDecimal(0);

        for(ArrayList<Factura> faturas : grupos_facturas.values()) {
            for(Factura f : faturas) {
                valorTotal = valorTotal.add(f.getValor());
            }
        }

        return valorTotal;
    }


    /**
     * Adiciona nova factura emitida ao contribuinte individual.
     *
     * @param factura factura registada
     */
    public void addFacturaRegistada(Factura factura) {
        Integer key = factura.getNatureza();

        ArrayList<Factura> setor = grupos_facturas.get(key);

        if (setor == null) {
            setor = new ArrayList<>();
            grupos_facturas.put(key, setor);
        }

        setor.add(factura);
    }


    /**
     * Devolve o somatório das despesas de um setor de atividade.
     *
     * @param codigo setor de atividade que corresponde ao índice do arrayList das faturas do grupos_facturas.
     */
    public BigDecimal getDespesaTotalSetor(int key) {
        BigDecimal total = BigDecimal.ZERO;

        ArrayList<Factura> setor = grupos_facturas.get(key);

        if (setor == null) {
            return total;
        }

        for(Factura fatura : setor) {
           total = total.add(fatura.getValor());
        }

        return total.setScale(2, RoundingMode.DOWN);
    }

    /**
     * Devolve o somatório das despesas de um contribuinte.
     *
     * @return O valor total da despesa efetuada por um contribuinte.
     */
    public BigDecimal getDespesaTotalIndividuo() {
        BigDecimal total = BigDecimal.ZERO;

       for(Map.Entry<Integer, ArrayList<Factura>> f : grupos_facturas.entrySet()) {
            for(Factura umafactura : f.getValue()){
                total = total.add(umafactura.getValor());
            }
    }
        return total.setScale(2, RoundingMode.DOWN);
    }

    /**
     * Atualiza todas as facturas de um determinado setor.
     *
     * @param facturas factura a ser alterada
     * @param key natureza do setor
     */
    public void setFacturasSetor(ArrayList<Factura> facturas, int key){
        grupos_facturas.put(key, facturas);
    }

    /**
     * Atualiza a informação do novo membro do agregado (fica com as mesmas informações do restante agregado).
     *
     * @param ContribuinteIndividual ci_pai informações do pai
     */
    public void atualizaInfoNovoMembro(ContribuinteIndividual ci_pai) {
        this.setN_direcao(ci_pai.getN_direcao());
        this.setN_dependentes(ci_pai.getN_dependentes());
        this.setNifsDependentesAgregado(ci_pai.getNifsDependentesAgregado());
        this.setNifsDirecaoAgregado(ci_pai.getNifsDirecaoAgregado());
        this.setCoeficiente_fiscal(ci_pai.getCoeficiente_fiscal());
    }

    /**
     * Adiciona um dependente ao agregado familiar
     *
     * @param nif_dependente nif do dependente
     */
    public void addDependente(int nif_dependente) {
        this.nifsDependentesAgregado.add(nif_dependente);
        this.setN_dependentes(this.getN_dependentes() + 1);
        this.setCoeficiente_fiscal(this.getCoeficiente_fiscal() + 0.1);
    }

    /**
     * Adiciona um contribuinte (passado como parâmetro) à direção do agregado familiar
     *
     * @param nif nif do contribuinte a adicionar À direção
     */
    public void addDirecaoAgregado(int nif) {
        this.setN_direcao(this.getN_direcao() + 1);
        this.nifsDirecaoAgregado.add(nif);
        this.setCoeficiente_fiscal(this.getCoeficiente_fiscal() + 0.1);
    }

     /**
     * Averigua se o contribuinte do nif passado como parâmetro faz parte
     * da direção do agregado familiar do contribuinte individual.
     *
     * @param nif_averiguar nif do contribuinte a averiguar se faz parte da direção do agregado
     * @return true caso afirmativo, false caso contrário
     */
    public boolean pertenceDirecaoAgregado(int nif_averiguar) {
        return this.getNifsDirecaoAgregado().contains(nif_averiguar);
    }

    /**
     * Averigua se podem ser agregados novos diretores de agregado
     * ao agregado do contribuinte individual.
     *
     * @return true caso afirmativo, false caso contrário
     */
    public boolean podeAdicionarDiretores() {
        return (this.getN_direcao() < 2);
    }

    /**
     * Averigua se o contribuinte pode ser dependente de um agregado.
     *
     * @return true caso afirmativo, false caso contrário
     */
    public boolean possoSerDependente() {
        return this.getN_direcao() == 0 && this.getN_dependentes() == 0;
    }

    /**
     * Averigua se o contribuinte do nif passado como parâmetro é um dependente no
     * meu agregado familiar.
     *
     * @param nif nif do contribuinte a averiguar se é dependente no meu agregado
     * @return true caso afirmativo, false caso contrário
     */
    public boolean pertenceDependentesAgregado(int nif) {
        return this.getNifsDependentesAgregado().contains(nif);
    }

    /**
     * Atualiza o CAE de uma factura para sem atividade.
     *
     * @param factura factura a ser alterada
     */
    public void eliminaCAE(Factura factura) {
        int id = factura.getId();
        int nif_emitente = factura.getNif_emitente();
        ArrayList<Integer> historico = factura.getHistorico_caes();
        Integer key = factura.getNatureza();

        ArrayList<Factura> facturasExSetor = this.getGrupos_facturas().get(key).stream().
                                     filter(f -> f.getId() != id || f.getNif_emitente() != nif_emitente).
                                     collect(Collectors.toCollection(ArrayList::new));

        this.setFacturasSetor(facturasExSetor, key);

        factura.setNatureza(0);

        //poderá ser um 0 temporário uma vez que a intenção do utilizador
        //poderá ser apenas alterar o cae da fatura e não removê-lo
        if(historico.get(historico.size() - 1) != 0)
            factura.addHistoricoCae(SemAtividade.objeto.getCodigo());

        this.addFacturaRegistada(factura);
    }

    /**
     * Atualiza o CAE de uma factura.
     *
     * @param factura factura a ser alterada
     * @param newNatureza nova natureza da factura
     */
    public void setCAE(Factura factura, int newNatureza) {
        int nif_emitente = factura.getNif_emitente();
        int id = factura.getId();

        ArrayList<Factura> facturasSemAtividade = this.getGrupos_facturas().get(0).stream().
                                                     filter(f -> f.getId() != id ||
                                                                 f.getNif_emitente() != nif_emitente).
                                                     collect(Collectors.toCollection(ArrayList::new));

        this.setFacturasSetor(facturasSemAtividade, 0);

        factura.setNatureza(newNatureza);

        //significa que se trata do 0 temporário
        if (factura.historico_caes.size() != 1){
            factura.removeSAHistoricoCae();
        }

        factura.addHistoricoCae(newNatureza);

        this.addFacturaRegistada(factura);

    }

}
