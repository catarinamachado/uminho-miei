/**
 * Contribuinte Coletivo, extensão da classe Contribuinte,
 * é uma entidade que emite facturas.
 *
 * @author A81047
 * @author A34900
 * @author A82339
 * @version 20180513
 */

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.io.*;

public class ContribuinteColetivo extends Contribuinte implements IncentivoFiscal, Serializable {
    private List<Integer> atividades_economicas;
    private double fator_deducao_fiscal; //será igual ao beneficio do concelho
    private String concelho;
    private List<Factura> facturas;

    /**
     * Construtor por omissão de Contribuinte Coletivo.
     */
    public ContribuinteColetivo() {
        super();
        atividades_economicas = new ArrayList<>();
        fator_deducao_fiscal = 0.0;
        concelho = "";
        facturas = new ArrayList<>();
    }

    /**
     * Construtor parametrizado de Contribuinte Coletivo.
     *
     * @param NIF Identificador
     * @param email Endereço de email
     * @param nome Nome
     * @param morada Morada
     * @param password Password
     * @param atividades_economicas Setores de atividade
     * @param fator_deducao_fiscal Fator de dedução fiscal
     * @param concelho Concelho da sede.
     * @param facturas Conjunto de Facturas emitidas
     */
    public ContribuinteColetivo(int NIF, String email, String nome, String morada, String password,
                                ArrayList<Integer> atividades_economicas, double fator_deducao_fiscal,
                                String concelho, ArrayList<Factura> facturas) {
        super(NIF, email, nome, morada, password);
        setAtividades_economicas(atividades_economicas);
        this.fator_deducao_fiscal = fator_deducao_fiscal;
        this.concelho = concelho;
        setFacturas(facturas);


    }

    /**
     * Construtor de Cópia
     *
     * @param umCC Contribuinte coletivo a ser replicado
     */
    public ContribuinteColetivo(ContribuinteColetivo umCC) {
        super(umCC.getNIF(), umCC.getEmail(), umCC.getNome(), umCC.getMorada(), umCC.getPassword());
        this.atividades_economicas = umCC.getAtividades_economicas();
        this.fator_deducao_fiscal = umCC.getFator_deducao_fiscal();
        this.concelho = umCC.getConcelho();
        this.facturas = umCC.getFacturas();
    }

    /**
     * Devolve setores de atividade.
     *
     * @return setores de atividade
     */
    public ArrayList<Integer> getAtividades_economicas() {
        return atividades_economicas.stream().
                collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Actualiza setores de atividade
     *
     * @param atividades_economicas setores de atividade
     */
    public void setAtividades_economicas(ArrayList<Integer> atividades_economicas) {
        this.atividades_economicas = atividades_economicas.stream().
                collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Devolve fator de dedução fiscal
     *
     * @return fator de dedução fiscal
     */
    public double getFator_deducao_fiscal() {
        return fator_deducao_fiscal;
    }

    /**
     * Atualiza fator de dedução fiscal
     *
     * @param fator_deducao_fiscal Fator de dedução fiscal
     */
    public void setFator_deducao_fiscal(double fator_deducao_fiscal) {
        this.fator_deducao_fiscal = fator_deducao_fiscal;
    }

    /**
     * Devolve o concelho da sede da empresa
     *
     * @return concelho da empresa
     */
    public String getConcelho() {
        return concelho;
    }

    /**
     * Atualiza concelho da sede da empresa
     *
     * @param concelho Concelho da empresa
     */
    public void setConcelho(String concelho) {
        this.concelho = concelho;
    }

    /**
     * Devolve facturas emitidas
     *
     * @return facturas emitidas
     */
    public ArrayList<Factura> getFacturas() {
        return facturas.stream().map(d -> d.clone()).collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Actualiza facturas emitidas
     *
     * @param facturas facturas emitidas
     */
    public void setFacturas(ArrayList<Factura> facturas) {
        this.facturas = facturas.stream().map(d -> d.clone()).collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Método que determina se dois contribuintes coletivos são iguais.
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

        ContribuinteColetivo contribuinte = (ContribuinteColetivo) object;
        return  super.equals(contribuinte) &&
                this.atividades_economicas.
                      equals(contribuinte.getAtividades_economicas()) &&
                Double.valueOf(this.fator_deducao_fiscal).
                      equals(Double.valueOf(contribuinte.getFator_deducao_fiscal())) &&
                this.concelho.equals(contribuinte.getConcelho()) &&
                this.facturas.equals(contribuinte.getFacturas());
    }

    /**
     * Método que devolve a representação em String de Contribuinte Coletivo.
     *
     * @return String que representa um contribuinte coletivo
     */
    public String toString() {
        return "ContribuinteColetivo{" +
                super.toString() +
                ", Atividades Económicas = " + atividades_economicas.toString() +
                ", Fator de dedução fiscal = " + fator_deducao_fiscal +
                ", Concelho = " + concelho +
                ", Facturas = " + facturas.toString() +
                '}';
    }

    /**
     * Método que faz uma cópia do contribuinte coletivo receptor da mensagem.
     * Para tal invoca o construtor de cópia .
     *
     * @return cópia do contribuinte coletivo
     */
    public ContribuinteColetivo clone() {
        return new ContribuinteColetivo(this);
    }

    /**
     * Adiciona nova factura emitida pelo contribuinte coletivo.
     *
     * @param factura factura emitida
     */
    public void addFacturaEmitida(Factura factura) {
        facturas.add(factura);
    }

    /**
     * Devolve valor total facturado.
     *
     * @return Total factura de sempre
     */
    public BigDecimal totalFacturado() {
        BigDecimal total = BigDecimal.ZERO;
        int size = facturas.size();

        for (int i = 0; i < size; i++)
            total = total.add(facturas.get(i).getValor());

        return total;
    }

    /**
     * Devolver valor total facturado entre duas datas.
     *
     * @param init data inicial
     * @param end data final
     * @return valor total facturado entre as duas datas de argumento
     */
    public BigDecimal totalFacturado(LocalDate init, LocalDate end) {
        BigDecimal total = BigDecimal.ZERO;
        int size = facturas.size();

        for (int i = 0; i < size; i++) {
            Factura f = facturas.get(i);
            if (f.getData().isAfter(init) && f.getData().isBefore(end))
                total = total.add(f.getValor());
        }

        return total;
    }

    /**
     * Retorna o numero de facturas emitidas pelo contribuinte
     *
     * @return numero de facturas emitidas pela empresa
     */
    public int numFacturas(){
        return getFacturas().size();
    }


    /**
     * Averigua se o contribuinte coletivo tem um determinado setor de atividade económica
     *
     * @param atividade código da atividade económica
     * @return true se possuí essa atividade, false caso contrário
     */
    public boolean temAtividade(int atividade){
        return this.atividades_economicas.contains(atividade);
    }

    /**
     * Calcula o aumento da dedução do contribuinte no caso da sede ser num Concelho do Interior.
     *
     * @return valor da percentual da dedução.
     */
    public double reducaoImposto(){
        Concelho concelhoCC = Concelho.getConcelho(concelho);

        //o fator de deducao vai ser igual ao beneficio, mas não é usado.
        //setFator_deducao_fiscal(concelhoCC.getBeneficio());

        return concelhoCC.getBeneficio();

    }

    /**
     * Averigua se as faturas emitidas pelo contribuinte coletivo podem sofrer alterações de CAE
     * (se a empresa só estiver inserida numa atividade económica todas as faturas emitidas por
     * ela não podem sofrer alterações de CAE, uma vez que todas as faturas farão obrigatoriamente
     * parte dessa atividade económica).
     *
     * @return true caso afirmativo, false caso contrário
     */
    public boolean podemAlterarCAEfaturas(){
        int nr_setores = this.getAtividades_economicas().size();

        return (nr_setores > 1);

    }

    /**
     * Devolve o código da única atividade económica da empresa se a empresa só estiver
     * associada a uma atividade (significa que todas as faturas emitidas pela empresa são
     * obrigatoriamente dessa atividade económica), 0 caso contrário.
     * Este método é utilizado quando o contribuinte coletivo cria uma nova fatura.
     *
     * @return código da única atividade económica da empresa ou 0 se a empresa tiver mais
     * do que uma atividade económica
     */
    public int inicializarAtividadeEconomica(){
        if(this.podemAlterarCAEfaturas()){
            return 0;
        }
        else
            return this.getAtividades_economicas().get(0);
    }

    /**
     * Devolve um array list vazio se a empresa só estiver associada a uma
     * atividade económica, ou um array com 0 na primeira posição caso contrário.
     * Este método é utilizado quando o contribuinte coletivo cria uma nova fatura
     * uma vez que cada fatura tem associado a si o histórico dos seus CAEs e se
     * a empresa só tiver uma atividade económica o CAE da fatura nunca pode ser
     * alterado, logo, o array list do históricos é vazio ,sempre. Se a empresa tiver
     * mais do que uma atividade, o CAE de todas as faturas emitidas por si
     * começam sempre por ser 0 (sem atividade definida)
     * pelo que essa informação deve ser conservada desde já no arraylist de históricos.
     *
     * @return arraylist com 0 na primeira posição ou array list vazio
     */
    public ArrayList<Integer> inicializarHistoricoCAES(){
        ArrayList<Integer> historicoCAES = new ArrayList<>();

        if(this.podemAlterarCAEfaturas()){
            historicoCAES.add(0);
            return historicoCAES;
        }
        else
            return historicoCAES;
    }
}
