/**
 * Faturas que podem ser feitas pelos contribuintes juntos das empresas.
 *
 * @author A81047
 * @author A34900
 * @author A82339
 * @version 20180513
 */

import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.io.*;

public class Factura implements Serializable{
    private int id;
    private int nif_emitente;
    private String designacao;
    private LocalDate data;
    private int nif_cliente;
    private String descricao;
    // 0 - sem natureza definida; 1->... - mapear a setor de atividade
    private int natureza;
    private BigDecimal valor;
    List<Integer> historico_caes; //guarda um arraylist com os caes que a fatura já teve

    static private int nextId = 1;//podem escrever no ficheiro e le-la pela mesma ordem

    /**
     * Construtores da classe Factura .
     * Declaração dos construtores por omissão,
     * parametrizado e de cópia.
     */
    public Factura() {
        this.id = Factura.getNextId();
        this.nif_emitente = 0;
        this.designacao = "";
        this.data = LocalDate.now();
        this.nif_cliente = 0;
        this.descricao = "";
        this.natureza = 0;
        this.valor = BigDecimal.ZERO;
        this.historico_caes = new ArrayList<>();
    }

    /**
     * Construtor parametrizado de Factura.
     * Aceita como parâmetros os valores para as variáveis de instâcia.
     *
     * @param id Identificador da Factura
     * @param nif_emitente NIF do emitente da factura
     * @param designacao Designação do emitente da factura
     * @param data Data de criação da factura
     * @param nif_cliente NIF do cliente
     * @param descricao Descrição da factura
     * @param natureza CAE da factura
     * @param valor Valor da factura
     */
    public Factura(int id, int nif_emitente, String designacao, LocalDate data, int nif_cliente,
                   String descricao, int natureza, BigDecimal valor, ArrayList<Integer> historico_caes) {
        this.id = id;
        this.nif_emitente = nif_emitente;
        this.designacao = designacao;
        this.data = data;
        this.nif_cliente = nif_cliente;
        this.descricao = descricao;
        this.natureza = natureza;
        this.valor = valor;
        this.historico_caes = historico_caes;
    }

    /**
     * Construtor de cópia de Factura .
     * Aceita como parâmetro outra Factura e utiliza os métodos
     * de acesso aos valores das variáveis de instância.
     *
     * @param umaFactura Objeto factura a ser replicado
     */
    public Factura(Factura umaFactura) {
        this.id = umaFactura.getId();
        this.nif_emitente = umaFactura.getNif_emitente();
        this.designacao = umaFactura.getDesignacao();
        this.data = umaFactura.getData();
        this.nif_cliente = umaFactura.getNif_cliente();
        this.descricao = umaFactura.getDescricao();
        this.natureza = umaFactura.getNatureza();
        this.valor = umaFactura.getValor();
        this.historico_caes = umaFactura.getHistorico_caes();
    }

    /**
     * Devolve o nextId.
     *
     * @return nextId
     */
    public static int getNextId() {
        return nextId++;
    }

    /**
     *  Devolve o nextId.
     *
     * @return nextId
     */
    public static void setNextId(int nextId) {
        Factura.nextId = nextId;
    }

    /**
     * Devolve Id da factura.
     *
     * @return Id da factura
     */
    public int getId() {
        return id;
    }

    /**
     * Actualiza o valor do Id da factura.
     *
     * @param id Identificador da factura
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Devolve NIF do emitente.
     *
     * @return NIF do emitente.
     */
    public int getNif_emitente() {
        return nif_emitente;
    }

    /**
     * Actualiza o valor do NIF do emitente.
     *
     * @param nif_emitente novo NIF do emitente.
     */
    public void setNif_emitente(int nif_emitente) {
        this.nif_emitente = nif_emitente;
    }

    /**
     * Devolve designação do emitente.
     *
     * @return designação do emitente.
     */
    public String getDesignacao() {
        return designacao;
    }

    /**
     * Actualiza o valor da designação do emitente.
     *
     * @param designacao nova designação do emitente.
     */
    public void setDesignacao(String designacao) {
        this.designacao = designacao;
    }

    /**
     * Devolve data da factura.
     *
     * @return data da factura.
     */
    public LocalDate getData() {
        return data;
    }

    /**
     * Actualiza data da factura.
     *
     * @param data nova data da factura.
     */
    public void setData(LocalDate data) {
        this.data = data;
    }

    /**
     * Devolve NIF do cliente.
     *
     * @return NIF do cliente.
     */
    public int getNif_cliente() {
        return nif_cliente;
    }

    /**
     * Actualiza NIF do cliente.
     *
     * @param nif_cliente novo NIF do cliente.
     */
    public void setNif_cliente(int nif_cliente) {
        this.nif_cliente = nif_cliente;
    }

    /**
     * Devolve descrição da factura.
     *
     * @return descrição da factura.
     */
    public String getDescricao() {
        return descricao;
    }

    /**
     * Actualiza descrição da factura.
     *
     * @param descricao nova descrição da factura.
     */
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    /**
     * Devolve a actividade económica a que factura diz respeito.
     *
     * @return natureza da factura.
     */
    public int getNatureza() {
        return natureza;
    }

    /**
     * Actualiza actividade económica a que factura diz respeito.
     *
     * @param natureza nova natureza da factura.
     */
    public void setNatureza(int natureza) {
        this.natureza = natureza;
    }

    /**
     * Devolve valor da factura.
     *
     * @return valor da factura.
     */
    public BigDecimal getValor() {
        return valor;
    }

    /**
     * Actualiza valor da factura.
     *
     * @param valor novo valor da factura.
     */
    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    /**
     * Devolve o histórico dos caes da fatura
     *
     * @return histórico dos caes
     */
    public ArrayList<Integer> getHistorico_caes() {
        return historico_caes.stream().
                  collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Atualiza o histórico dos caes da fatura
     *
     * @param histórico dos caes Historico dos caes
     */
    public void setHistorico_caes(ArrayList<Integer> historico_caes) {
        this.historico_caes = historico_caes.stream().
            collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Método que determina se duas facturas são iguais.
     *
     * @return booleano true se todos os valores das duas
     * facturas forem iguais
     */
    public boolean equals(Object object) {
        if (this == object)
            return true;

        if (object == null || getClass() != object.getClass())
            return false;

        Factura factura = (Factura) object;

        return id == factura.getId() &&
                nif_emitente == factura.getNif_emitente() &&
                designacao.equals(factura.getDesignacao()) &&
                data.equals(factura.getData()) &&
                nif_cliente == factura.getNif_cliente() &&
                descricao.equals(factura.getDescricao())&&
                natureza == factura.getNatureza() &&
                valor.equals(factura.getValor()) &&
                historico_caes.equals(factura.getHistorico_caes());
    }

    /**
     * Método que devolve a representação em String da Factura .
     * @return String com o valor das variáveis de instância da factura.
     */
    public String toString() {
        return "Factura{" +
                "Id=" + id +
                ", NIF do emitente=" + nif_emitente +
                ", Designação='" + designacao + '\'' +
                ", Data=" + data +
                ", NIF do cliente=" + nif_cliente +
                ", Descrição='" + descricao + '\'' +
                ", Natureza=" + natureza +
                ", Valor=" + valor +
                ", CAE=" + getHistorico_caes() +
                '}';
    }

    /**
     * Método que faz uma cópia do factura receptora da mensagem.
     * Para tal invoca o construtor de cópia.
     *
     * @return factura clone da factura que recebe a mensagem .
     */
    public Factura clone() {
        return new Factura(this);
    }

    /**
     * Adiciona uma nova informação ao histórico dos caes da fatura.
     *
     * @param newCae novo cae da fatura
     */
    public void addHistoricoCae(int newCae) {
        historico_caes.add(newCae);
    }

    /**
     * Remove o CAE 0 (sem Atividade) temporário (é o ultimo na lista).
     */
    public void removeSAHistoricoCae() {
        historico_caes.remove(historico_caes.size() - 1);
    }
}
