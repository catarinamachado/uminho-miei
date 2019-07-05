/**
 * Classe principal da aplicação JavaFactura
 *
 * @author A81047
 * @author A34900
 * @author A82339
 * @version 20180304
 */

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.io.*;

public class JavaFactura implements Serializable {
    private Map<Integer, ContribuinteIndividual> individuais;
    private Map<Integer, ContribuinteColetivo> coletivos;


    /**
     * Construtor por omissão de JavaFactura.
     */
    public JavaFactura() {
        this.individuais = new HashMap<>();
        this.coletivos = new HashMap<>();
        AtividadesEconomicas.criarAtividades();
        Concelho.criarConcelhos();
    }

    /**
     * Construtor parametrizado de JavaFactura.
     *
     * @param individuais HashMap dos contribuintes individuais do sistema
     * @param coletivos HashMap dos contribuintes coletivos do sistema
     */
    public JavaFactura(Map<Integer, ContribuinteIndividual> individuais, Map<Integer, ContribuinteColetivo> coletivos) {
        individuais.putAll(this.individuais);
        this.individuais.forEach((k,v) -> v = v.clone());

        coletivos.putAll(this.coletivos);
        this.coletivos.forEach((k,v) -> v = v.clone());

        AtividadesEconomicas.criarAtividades();
        Concelho.criarConcelhos();
    }

    /**
     * Construtor de Cópia.
     *
     * @param jf JavaFactura a ser replicado
     */
    public JavaFactura(JavaFactura jf) {
        this.individuais = jf.getIndividuais();
        this.coletivos = jf.getColetivos();
        AtividadesEconomicas.criarAtividades();
        Concelho.criarConcelhos();
    }


    /**
     * Devolve HashMap com os contribuintes individuais do sistema.
     *
     * @return HashMap com os contribuintes individuais do sistema
     */
    public Map<Integer, ContribuinteIndividual> getIndividuais() {
         return individuais.entrySet().stream().collect(Collectors.toMap(k -> k.getKey(), v -> v.getValue().clone()));
    }

    /**
     * Atualiza conjunto dos contribuintes individuais do sistema.
     *
     * @param individuais HashMap com os contribuintes individuais do sistema
     */
    public void setIndividuais(HashMap<Integer, ContribuinteIndividual> individuais) {
        individuais.putAll(this.individuais);
        this.individuais.forEach((k,v) -> v = v.clone());
    }

    /**
     * Devolve HashMap com os contribuintes coletivos do sistema.
     *
     * @return HashMap com os contribuintes coletivos do sistema
     */
    public Map<Integer, ContribuinteColetivo> getColetivos() {
        return coletivos.entrySet().stream().collect(Collectors.toMap(k -> k.getKey(), v -> v.getValue().clone()));
    }


    /**
     * Atualiza conjunto dos contribuintes coletivos do sistema.
     *
     * @param coletivos HashMap com os contribuintes coletivos do sistema
     */
    public void setColetivos(HashMap<Integer, ContribuinteColetivo> coletivos) {
        coletivos.putAll(this.coletivos);
        this.coletivos.forEach((k,v) -> v = v.clone());
    }

    /**
     * Devolve um concelho do sistema se ele existir.
     *
     * @return Concelho um dos concelhos do sistema
     */
    public Concelho getUmConcelho(String nome) {
        Concelho umConcelho = Concelho.getConcelho(nome);

        return umConcelho;
    }

    /**
     * Método que determina se dois JavaFactura objeto são iguais.
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

        JavaFactura javafactura = (JavaFactura) object;

        return this.individuais.equals(javafactura.getIndividuais()) &&
               this.coletivos.equals(javafactura.getColetivos());
    }

    /**
     * Método que devolve a representação em String de JavaFactura.
     *
     * @return String que representa JavaFactura
     */
    public String toString() {
        return "JavaFactura{" +
                "individuais= " + individuais +
                ", coletivos= " + coletivos +
                '}';
    }

    /**
     * Método que faz uma cópia da JavaFactura receptora da mensagem.
     * Para tal invoca o construtor de cópia .
     *
     * @return cópia de JavaFactura
     */
    public JavaFactura clone() {
        return new JavaFactura(this);
    }

    /**
     * Método que adiciona um novo Contribuinte Individual ao sistema
     *
     * @param umCI Contribuinte Individual
     */
    public void add(ContribuinteIndividual umCI) {
        this.individuais.put(umCI.getNIF(), umCI.clone());
    }

    /**
     * Método que adiciona um novo Contribuinte Coletivo ao sistema
     *
     * @param umCC Contribuinte Coletivo
     */
    public void add(ContribuinteColetivo umCC) {
        this.coletivos.put(umCC.getNIF(), umCC.clone());
    }

    /**
     * Método que adiciona uma nova Factura ao sistema
     *
     * @param f Factura
     */
    public void addFactura(Factura f) {
        individuais.get(f.getNif_cliente()).addFacturaRegistada(f);
        coletivos.get(f.getNif_emitente()).addFacturaEmitida(f);
    }

    /**
     * Método que verifica se existe um contribuinte individual no sistema com o NIF passado como parâmetro.
     *
     * @param NIF Identificador do Contribuinte Individual
     * @return true se existe um contribuinte individual com o NIF passado como parâmetro.
     */
    public Boolean existIndividual(int NIF) {
        ContribuinteIndividual ci = this.individuais.get(NIF);

        return (ci != null);
    }

    /**
     * Método que verifica se existe um contribuinte individual no sistema com o NIF e password passados como parâmetro.
     *
     * @param NIF Identificador do Contribuinte Individual
     * @param password Password
     * @return true se existe um contribuinte individual com o NIF e password passados como parâmetro.
     * @throws CINullException
     */
    public Boolean containsIndividual(int NIF, String password) throws CINullException {
        ContribuinteIndividual ci = this.individuais.get(NIF);
        boolean flag = false;

        if(ci == null)
            throw new CINullException("CI com nif " + NIF + " não existe");
        else
            flag = ci.getPassword().equals(password);

        return flag;
    }

    /**
     * Método que verifica se existe um contribuinte coletivo no sistema com o NIF e password passados como parâmetro.
     *
     * @param NIF Identificador do Contribuinte Coletivo
     * @param password Password
     * @return true se existe um contribuinte coletivo com o NIF e password passados como parâmetro
     * @throws CCNullException
     */
    public Boolean containsColetivo(int NIF, String password) throws CCNullException {
        ContribuinteColetivo cc = this.coletivos.get(NIF);
        boolean flag = false;

        if(cc == null)
            throw new CCNullException("CC com nif " + NIF + " não existe");
        else
            flag = cc.getPassword().equals(password);

        return flag;
    }

    /**
     * Adiciona ao ArrayList users um dos dez contribuintes que mais gastaram
     *
     * @param users ArrayList com os 10 contribuintes que mais gastaram
     * @param v um contribuinte
     */
    private void addTopContribuinte(ContribuinteIndividual v, ArrayList<ContribuinteIndividual> users) {
        for (int i = 0; i < 10; i++)
            if (v.getValorTotal().compareTo(users.get(i).getValorTotal()) > 0 ) {
                users.add(i, v);
                return;
            }
    }

    /**
     * Atualiza o ArrayList argumento com os 10 contribuintes que mais gastaram
     *
     * @param users ArrayList com os 10 contribuintes que mais gastaram
     */
    public void getTop10Contribuintes(ArrayList<ContribuinteIndividual> users) {
        for (int i = 0; i < 10; i++)
            users.add(new ContribuinteIndividual());

        individuais.forEach((k,v) -> addTopContribuinte(v,users));
    }

    /**
     * Ordena as faturas de um contribuinte coletivo por valor das mesmas.
     *
     * @param umCC um contribuintecoletivo
     * @return uma Lista de facturas ordenadas por valor
     */
    public List<Factura> ordenaFaturasValor(int NIF) {
        if(!coletivos.containsKey(NIF)) {
            return new ArrayList<Factura>();
        }

        return coletivos.get(NIF).getFacturas().stream().
                sorted(Comparator.comparing(Factura :: getValor)).
                collect(Collectors.toList());
    }

    /**
     * Ordena as faturas de um contribuinte coletivo por data das mesmas.
     *
     * @param NIF NIF de um contribuinte coletivo
     * @return uma Lista de facturas ordenadas por data
     */
    public List<Factura> ordenaFaturasData(int NIF) {
        if(!coletivos.containsKey(NIF)) {
            return new ArrayList<Factura>();
        }

        return coletivos.get(NIF).getFacturas().stream().
                sorted(Comparator.comparing(Factura :: getData)).
                collect(Collectors.toList());
    }


    /**
     * Devolve a lista das facturas emitidas a um contribuinte num determinado periodo.
     *
     * @param NIFcc identificador do contribuinte coletivo
     * @param NIFci identificador do contribuinte individual
     * @param init data de início
     * @param end data de fim
     * @return Lista da facturas emitidas a determinado contribuinte num determinado periodo
     */
    public List<Factura> faturasPeriodoContribuinte(int NIFcc, int NIFci, LocalDate init, LocalDate end) {
        if(!coletivos.containsKey(NIFcc)) {
            return new ArrayList<Factura>();
        }

        return coletivos.get(NIFcc).getFacturas().stream().
                    filter(f-> f.getNif_cliente() == NIFci || NIFci == 0).
                    filter(f -> f.getData().isAfter(init) && f.getData().isBefore(end)).
                    collect(Collectors.toList());
    }

    /**
     * Devolve a lista das facturas emitidas a um contribuinte ordenadas por valor decrescente de despesa.
     *
     *
     * @param NIFcc identificador do contribuinte coletivo
     * @param NIFci identificador do contribuinte individual
     * @return Lista da facturas emitidas a determinado contribuinte ordenadas por valor decrescente
     */
    public List<Factura> faturasValorContribuinte(int NIFcc, int NIFci) {
        if(!coletivos.containsKey(NIFcc)) {
            return new ArrayList<Factura>();
        }

        return coletivos.get(NIFcc).getFacturas().stream().
                filter(f-> f.getNif_cliente() == NIFci || NIFci == 0).
                sorted(Comparator.comparing(Factura :: getValor).reversed()).
                collect(Collectors.toList());
    }

    /**
     * Devolve o total facturado por um contribuinte coletivo do sistema.
     *
     * @param NIF identificador do contribuinte coletivo
     * @return total facturado
     */
    public BigDecimal totalFacturado(int NIF) {
        return coletivos.get(NIF).totalFacturado();
    }

    /**
     * Devolve o valor facturado entre duas datas por um contribuinte coletivo do sistema.
     *
     * @param NIF identificador do contribuinte coletivo
     * @param init data de início
     * @param end data de fim
     * @return total facturado
     */
    public BigDecimal totalFacturado(int NIF, LocalDate init, LocalDate end) {
        return coletivos.get(NIF).totalFacturado(init, end);
    }

    /**
     * Devolve as faturas emitidas por um Contribuinte coletivo ou individual
     *
     * @return as faturas emitidas por um Contribuinte coletivo ou individidual
     */
    public ArrayList<Factura> getFacturasColetivosOuIndividual(int NIF) {
        ArrayList<Factura> facturas;
        ContribuinteColetivo umContribuinteCol = coletivos.get(NIF);
        ContribuinteIndividual umContribuinteInd = individuais.get(NIF);

        if(umContribuinteCol != null) {
            facturas = umContribuinteCol.getFacturas();
        }
        else if(umContribuinteInd != null) {
            facturas = new ArrayList<>();
            for(ArrayList<Factura> grupoFactura : umContribuinteInd.getGrupos_facturas().values()) {
                facturas.addAll(grupoFactura);
            }
        }
        else facturas = new ArrayList<>();

        return facturas;
    }

    /**
     * Devolve contribuinte individual com o NIF passado como parâmetro
     *
     * @param NIF indentificador do contribuinte
     * @return contribuinte individual com NIF passado como parâmetro
     * @throws CINullException
     */
    public ContribuinteIndividual getContribuinteIndividual(int NIF) throws CINullException {
        ContribuinteIndividual umContribuinte = individuais.get(NIF);

        if(umContribuinte == null)
            throw new CINullException("CI com nif " + NIF + " não existe");

        return umContribuinte.clone();
    }

    /**
     * Atualiza o CAE de uma factura para sem atividade, caso o contribuinte coletivo esteja inserido
     * em mais do que 1 setor de atividade económica.
     *
     * @param factura factura a ser alterada
     * @param NIF indentificador do contribuinte
     * @throws CINullException, CCNullException
     */
    public void eliminarCaeFatura(Factura factura, int NIF) throws CINullException, CCNullException {;
        int nif_emitente = factura.getNif_emitente();
        ContribuinteIndividual umContribuinteI = individuais.get(NIF);
        ContribuinteColetivo umContribuinteC = coletivos.get(nif_emitente);

        if(umContribuinteI == null)
            throw new CINullException("CI com nif " + NIF + " não existe");

        else if(umContribuinteC == null)
            throw new CCNullException("CC com nif " + nif_emitente + " não existe");

        else if(umContribuinteC.podemAlterarCAEfaturas()){
            umContribuinteI.eliminaCAE(factura);
        }
    }

    /**
     * Atualiza o CAE de uma factura, caso o contribuinte coletivo pertença a esse setor de atividade económica.
     *
     * @param factura factura a ser alterada
     * @param NIF indentificador do contribuinte
     * @param newNatureza nova natureza da factura
     * @throws CINullException, CCNullException
     */
    public void setCAEFactura(Factura factura, int NIF, int newNatureza) throws CINullException, CCNullException {
        int nif_emitente = factura.getNif_emitente();
        int id = factura.getId();
        ContribuinteIndividual umContribuinteI = individuais.get(NIF);
        ContribuinteColetivo umContribuinteC = coletivos.get(nif_emitente);

        if(umContribuinteI == null)
            throw new CINullException("CI com nif " + NIF + " não existe");

        else if(umContribuinteC == null)
            throw new CCNullException("CC com nif " + nif_emitente + " não existe");

        else if(umContribuinteC.temAtividade(newNatureza)){
            umContribuinteI.setCAE(factura, newNatureza);
        }
    }

    /**
     * Devolve o valor total das deduções de um determinado setor de atividade.
     *
     * @param NIF Nif do contribuinte.
     * @param atividade uma tividade económica.
     *
     * @return BigDecimal valor da dedução.
     * @throws CINullException
     */
    public BigDecimal getDeducaoTotalIndividuoSetor(int NIF, AtividadesEconomicas atividade) throws CINullException {
        BigDecimal total = BigDecimal.ZERO;
        int codigo = atividade.getCodigo();
        ContribuinteIndividual umCI = individuais.get(NIF);

        if(umCI == null)
            throw new CINullException("CI com nif " + NIF + " não existe");

        else{
            ArrayList<Factura> setor = umCI.getGrupos_facturas().get(codigo);

            if(setor == null) {
                return total;
            }

            for(Factura umaFactura: setor) {
                ContribuinteColetivo umCC = coletivos.get(umaFactura.getNif_emitente());
                total = total.add(atividade.calculaDeducao(umaFactura.getValor(), umCI, umCC));
            }



            //verifica se o total é superior ao limite da deducao da atividade económica
            total = atividade.calculaLimite(umCI, total);

            return total.setScale(2, RoundingMode.DOWN);
        }
    }

    /**
     * Devolve o somatório das deduções de um contribuinte.
     *
     * @param NIF Nif do contribuinte.
     *
     * @return O valor total das deduções de um contribuinte.
     * @throws CINullException
     */
    public BigDecimal getDeducoesTotalIndividuo(int NIF) throws CINullException {
        BigDecimal totalDeducoes = BigDecimal.ZERO;
        ContribuinteIndividual umContribuinte = individuais.get(NIF);

        if(umContribuinte == null)
            throw new CINullException("CI com nif " + NIF + " não existe");
        else{
            for(Integer key: umContribuinte.getGrupos_facturas().keySet()) {
                AtividadesEconomicas atividade = AtividadesEconomicas.getAtividade(key);
                totalDeducoes = totalDeducoes.add(getDeducaoTotalIndividuoSetor(NIF,atividade));
            }
        }

        return totalDeducoes.setScale(2, RoundingMode.DOWN);
    }

    /**
     * Devolve o somatório das deduções do agregado familiar.
     *
     * @param NIF Nif do contribuinte.
     *
     * @return O valor total das deduções de um agregado familiar.
     * @throws CINullException
     */
    public BigDecimal getDeducoesAgregado(int NIF) throws CINullException {
        BigDecimal total = BigDecimal.ZERO;
        ContribuinteIndividual umContribuinte = individuais.get(NIF);

        if(umContribuinte == null)
            throw new CINullException("CI com nif " + NIF + " não existe");
        else{
            for(int nif_ind_dependentes: umContribuinte.getNifsDependentesAgregado())
                total = total.add(getDeducoesTotalIndividuo(nif_ind_dependentes));

            for(int nif_ind_direcao: umContribuinte.getNifsDirecaoAgregado())
                total = total.add(getDeducoesTotalIndividuo(nif_ind_direcao));
        }


        return total.setScale(2, RoundingMode.DOWN);
    }

    /**
      * Devolve o somatório das despesas do agregado familiar.
      *
      * @param NIF Nif do contribuinte.
      *
      * @return O valor total da despesa efetuada pelo conjunto dos membros do agregado familiar.
      * @throws CINullException
      */
     public BigDecimal getDespesaTotalAgregado(int NIF) throws CINullException {
        BigDecimal total = BigDecimal.ZERO;
        ContribuinteIndividual umContribuinte = individuais.get(NIF);

        if(umContribuinte == null)
            throw new CINullException("CI com nif " + NIF + " não existe");
        else{
            for(int nif_ind_dependentes: umContribuinte.getNifsDependentesAgregado()) {
                ContribuinteIndividual contribuinte_agregado = individuais.get(nif_ind_dependentes);
                total = total.add(contribuinte_agregado.getDespesaTotalIndividuo());
            }
            for(int nif_ind_direcao: umContribuinte.getNifsDirecaoAgregado()) {
                ContribuinteIndividual contribuinte_agregado = individuais.get(nif_ind_direcao);
                total = total.add(contribuinte_agregado.getDespesaTotalIndividuo());
            }
        }
        return total.setScale(2, RoundingMode.DOWN);
     }

    /**
     * Devolve uma cópia do contribuinte coletivo com o NIF passado como parâmetro
     *
     * @param NIF indentificador do contribuinte
     * @return contribuinte coletivo com NIF passado como parâmetro
     * @throws CCNullException
     */
    public ContribuinteColetivo getContribuinteColetivo(int NIF) throws CCNullException {
        ContribuinteColetivo umContribuinte = coletivos.get(NIF);

        if(umContribuinte == null)
            throw new CCNullException("CC com nif " + NIF + " não existe");

        return umContribuinte.clone();
    }

    /**
     * Adiciona um novo dependente ao contribuinte individual que associou o dependente e atualiza
     * essa informação também nos restantes membros do agregado
     *
     * @param nif_pai indentificador do contribuinte que associou o dependente
     * @param nif_dependente indentificador do novo contribuinte do agregado (dependente)
     * @throws CINullException
     */
    public void addNovoDependenteAgregado(int nif_pai, int nif_dependente) throws CINullException {
        ContribuinteIndividual ci_pai = individuais.get(nif_pai);
        ContribuinteIndividual ci_dependente = individuais.get(nif_dependente);

        if (ci_pai == null)
            throw new CINullException("CI com nif " + nif_pai + " não existe");
        else if(ci_dependente == null)
            throw new CINullException("CI com nif " + nif_dependente + " não existe");
        else{
            //se o contribuinte dependente é elegível para ser um dependente do agregado
            if(ci_dependente.possoSerDependente()){
                //se o contribuinte pai é elegível para ser associar um dependente (faz parte da direção)
                if(ci_pai.pertenceDirecaoAgregado(nif_pai)){
                    for(int nifs_dependentes: ci_pai.getNifsDependentesAgregado()) {
                        ContribuinteIndividual contr = individuais.get(nifs_dependentes);
                        contr.addDependente(nif_dependente);
                    }
                    for(int nifs_direcao: ci_pai.getNifsDirecaoAgregado()) {
                        ContribuinteIndividual contr = individuais.get(nifs_direcao);
                        contr.addDependente(nif_dependente);
                    }

                    ci_dependente.atualizaInfoNovoMembro(ci_pai);
                }
            }
        }


        //verifica se o contribuinte pertence a uma familia numerosa
        if(ci_pai.getN_dependentes() >= FamiliaNumerosa.getFamiliaNumerosa()){
                //se o pai faz parte de uma familia numerosa e ainda não foi instanciado como tal
                if(!(ci_pai instanceof FamiliaNumerosa)) {
                    for(int nifsAgregado : ci_pai.getNifsDependentesAgregado()) {
                        ContribuinteIndividual umContribuinte = individuais.get(nifsAgregado);
                        FamiliaNumerosa umCiFamiliaNumerosa = new FamiliaNumerosa(umContribuinte);
                        individuais.put(nifsAgregado, umCiFamiliaNumerosa);
                    }

                    for(int nifsDirecao : ci_pai.getNifsDirecaoAgregado()) {
                        ContribuinteIndividual umContribuinte = individuais.get(nifsDirecao);
                        FamiliaNumerosa umCiFamiliaNumerosa = new FamiliaNumerosa(umContribuinte);
                        individuais.put(nifsDirecao, umCiFamiliaNumerosa);
                    }
                }
                //se o pai já foi instanciado como FamiliaNumerosa mas um dos seus dependentes não,
                //por exemplo, por ter sido gerado depois da familia numerosa estar constituida
                else if (!(ci_dependente instanceof FamiliaNumerosa)){
                   FamiliaNumerosa umCiFamiliaNumerosa = new FamiliaNumerosa(ci_dependente);
                   individuais.put(nif_dependente, umCiFamiliaNumerosa);
                }
        }

    }

    /**
     * Adiciona um novo contribuinte individual (passado como parâmetro) à sua direção do agregado
     *
     * @param nif indentificador do contribuinte que quer fazer parte da direcao do agregado
     * @throws CINullException
     */
    public void addNovaDirecaoAgregado(int nif) throws CINullException {
        ContribuinteIndividual ci = individuais.get(nif);

        if (ci == null)
            throw new CINullException("CI com nif " + nif + " não existe");
        else{
            if(!ci.pertenceDependentesAgregado(nif)){ //se eu sou um dependente logo não posso fazer parte da direção
                if(!ci.pertenceDirecaoAgregado(nif)){ //se já faço parte da direção
                    if(ci.podeAdicionarDiretores()){  //direcao cheia já
                        ci.addDirecaoAgregado(nif);
                    }
                }
            }
        }
    }

    /**
     * Adiciona um novo contribuinte individual à direção de outro agregado e atualiza
     * essa informação também nos restantes membros do agregado
     *
     * @param nif_novad indentificador do contribuinte que faz parte da direcao do agregado
     * @param nif_d indentificador do contribuinte que quer fazer parte da direcao do agregado
     * @throws CINullException
     */
    public void addNovaDirecaoOutroAgregado(int nif_novad, int nif_d) throws CINullException {
        ContribuinteIndividual ci_novad = individuais.get(nif_novad);
        ContribuinteIndividual ci_d = individuais.get(nif_d);

        if (ci_novad == null)
            throw new CINullException("CI com nif " + nif_novad + " não existe");
        else if(ci_d == null)
            throw new CINullException("CI com nif " + nif_d + " não existe");
        else{
            if(ci_d.pertenceDirecaoAgregado(nif_d) && ci_novad.pertenceDirecaoAgregado(nif_novad)){ //o contribuinte nif_d tem que ser um diretor de um agregado e o nif_novad também
                if(ci_d.podeAdicionarDiretores() && ci_novad.podeAdicionarDiretores()){ //ambas direcoes cheias já ou uma delas
                    if(!ci_d.pertenceDependentesAgregado(nif_novad) && !ci_novad.pertenceDependentesAgregado(nif_novad)){ //se o nif_novad é um dependente logo não pode fazer parte da direção
                        if(!ci_d.pertenceDirecaoAgregado(nif_novad)){ //se já faço parte da direção do novo agregado
                            //atualizar informação do membro mais antigo do agregado
                            ci_d.addDirecaoAgregado(nif_novad);
                            for(int nifs_o_dependentes: ci_novad.getNifsDependentesAgregado()) {
                                ci_d.addDependente(nifs_o_dependentes);
                            }
                            //atualizar informação do agregado do membro mais antigo
                            for(int nifs_dependentes: ci_d.getNifsDependentesAgregado()) {
                                ContribuinteIndividual contr = individuais.get(nifs_dependentes);
                                contr.atualizaInfoNovoMembro(ci_d);

                            }
                            //atualizar informação dos novos dependentes do agregado
                            for(int nifs_dependentes: ci_novad.getNifsDependentesAgregado()) {
                                ContribuinteIndividual contr = individuais.get(nifs_dependentes);
                                contr.atualizaInfoNovoMembro(ci_d);
                            }
                            //atualizar informação do novo diretor do agregado
                            ci_novad.atualizaInfoNovoMembro(ci_d);
                        }
                    }
                }
            }
        }
    }

    /**
     * Devolve as X as empresas que mais emitiram facturas.
     *
     * @return int qtasEmpresas- numero de empresas
     *
     */
    public List<ContribuinteColetivo> EmpresasComMaisFacturas(int qtasEmpresas) {
        return coletivos.values().stream().
               sorted(Comparator.comparing(ContribuinteColetivo:: numFacturas).reversed()).
               limit(qtasEmpresas).collect(Collectors.toList());
    }

    /**
     * Devolve o valor total da dedução de uma factura.
     *
     * @param NIF Nif do contribuinte.
     * @param atividade uma tividade económica.
     *
     * @return BigDecimal valor da dedução.
     */
    private BigDecimal getDeducaoFactura(int NIF, Factura f) {
        BigDecimal total = BigDecimal.ZERO;
        ContribuinteIndividual umCI = individuais.get(NIF);
        int codigo = f.getNatureza();

        AtividadesEconomicas atividade = AtividadesEconomicas.getAtividade(codigo);

        if(umCI == null) {
             AtividadesEconomicas a = AtividadesEconomicas.getAtividade(10000);
             a.getCodigo();
             return total;
        }

        ContribuinteColetivo umCC = coletivos.get(f.getNif_emitente());
        total = total.add(atividade.calculaDeducao(f.getValor(), umCI,umCC));


        return total.setScale(2, RoundingMode.DOWN);
    }

    /**
     * Devolve o total da dedução de um contribuinte coletivo.
     *
     * @param NIF Nif do contribuinte coletivo.

     *
     * @return int numero.
     */
    public BigDecimal totalDeducaoCC(int NIF) {
        BigDecimal valor = BigDecimal.ZERO;
        ContribuinteColetivo cc = coletivos.get(NIF);

            for(Factura f: cc.getFacturas()) {
                valor = valor.add(getDeducaoFactura(f.getNif_cliente(), f));
            }

             return valor.setScale(2, RoundingMode.DOWN);
    }



    /**
     * Devolve o valor que as faturas de um conjunto de empresas representa em termos de deduções ficais.
     *
     * @param List<ContribuinteColetivo>  lista com um conjunto de contribuintes coletivos
     *
     * @return BigDecimal valor das deduções de um conjunto de facturas
     */
    public BigDecimal deducaoConjunta(List<ContribuinteColetivo> contribuintes) {
        BigDecimal valor = BigDecimal.ZERO;

        for(ContribuinteColetivo cc: contribuintes){
            for(Factura f: cc.getFacturas()) {
                valor = valor.add(getDeducaoFactura(f.getNif_cliente(), f));
            }
        }
        return valor.setScale(2, RoundingMode.DOWN);
    }

    /**
     * Grava o estado do programa para um ficheiro.
     *
     * @param nomeFicheiro nome do ficheiro onde se vai guardar o Estado do programa.
     * @param nomeFicheiroNextID nome do ficheiro onde se guarda o Id da próxima factura.
     */
    public void guardaEstado(String nomeFicheiro, String nomeFicheiroNextID) throws IOException {
        FileOutputStream fileOut = new FileOutputStream(nomeFicheiroNextID);
        DataOutputStream dataOut = new DataOutputStream(fileOut);
        dataOut.writeInt(Factura.getNextId());
        dataOut.flush();
        dataOut.close();
        fileOut.close();

        fileOut = new FileOutputStream(nomeFicheiro);
        ObjectOutputStream out = new ObjectOutputStream(fileOut);
        out.writeObject(this);
        out.flush();
        out.close();
        fileOut.close();
    }

   /**
     * Recupera o estado do programa a partir de um ficheiro.
     *
     * @param nomeFicheiro nome do ficheiro que se quer carregar.
     *
     * @return O estado do programa
     */
   public static JavaFactura carregaEstado(String nomeFicheiro, String nomeFicheiroNextID) throws FileNotFoundException,
                                                                IOException,
                                                                ClassNotFoundException{
       FileInputStream fileIn = new FileInputStream(nomeFicheiroNextID);
       DataInputStream din = new DataInputStream(fileIn);
       int i = din.readInt();
       Factura.setNextId(i);
       din.close();
       fileIn.close();

       fileIn = new FileInputStream(nomeFicheiro);
       ObjectInputStream in = new ObjectInputStream(fileIn);
       JavaFactura programa = (JavaFactura) in.readObject();
       in.close();
       fileIn.close();

       return programa;
   }

    /**
     * Imprime uma factura de um contribuinte individual para um ficheiro.
     *
     * @param nome Nome do ficheiro
     * @param id Id da factura
     * @param contribuinte Id do contribuinte
     * @throws IOException
     * @throws FacturaNaoExisteException
     */
    public void imprimeFacturaIndividual(String nome, int id, int contribuinte) throws IOException, FacturaNaoExisteException {
        ContribuinteIndividual i = this.individuais.get(contribuinte);

        Collection facCol = i.getGrupos_facturas().values();

        Factura fac = null;
        for(Object af : facCol)
            for(Object f : (ArrayList)af)
                if(((Factura)f).getId() == id)
                    fac = (Factura) f;

        if (fac == null)
            throw new FacturaNaoExisteException("A Factura " + id + " não existe!");

        ContribuinteColetivo c = this.coletivos.get(fac.getNif_emitente());

        String s =
                "<!DOCTYPE html><html><head><title>Factura</title><style>* { margin: 0; padding: 0; }body { font: 14px/1.4 Georgia, serif; }#page-wrap { width: 800px; margin: 0 auto; }#header { height: 15px; width: 100%; margin: 20px 0; background: #222; text-align: center; color: white; font: bold 15px Helvetica, Sans-Serif; text-decoration: uppercase; letter-spacing: 20px; padding: 8px 0px; }table { border-collapse: collapse; }table td, table th { border: 1px solid black; padding: 5px; }#meta { margin-top: 1px; width: 300px; float: right; }#meta td { text-align: right;  }#meta td.meta-head { text-align: left; background: #eee; }#terms { text-align: center; margin: 20px 0 0 0; }#terms h5 { text-transform: uppercase; font: 13px Helvetica, Sans-Serif; letter-spacing: 10px; border-bottom: 1px solid black; padding: 0 0 8px 0; margin: 0 0 8px 0; }</style></head><body><div id=\"page-wrap\"><p id=\"header\">FACTURA</p><div>"
                        + i.getNome()
                        + "<br/>NIF:"
                        + i.getNIF()
                        + "</div><div><br/><br/><p style=\"font-size: 200%; font-weight: bold;\">"
                        + c.getNome()
                        + "<p/>NIF:"
                        + c.getNIF()
                        + "<br/>"
                        + c.getMorada()
                        + "<br/>"
                        + c.getEmail()
                        + "<br/><br/></div><p style=\"text-align: center;\">"
                        + fac.getDescricao()
                        + "<br/><br/><br/></p><div id=\"customer\"><table id=\"meta\"><tr><td class=\"meta-head\">Factura</td><td>"
                        + fac.getId()
                        + "</td></tr><tr><td class=\"meta-head\">Data</td><td>"
                        + fac.getData().toString()
                        + "</td></tr><tr><td class=\"meta-head\">Total</td><td>&euro;"
                        + fac.getValor().toString()
                        + "</td></tr></table></div><br/><br/><br/><br/><br/><br/><div id=\"terms\"><h5>Termos</h5><p>Processado por JavaFactura 1.0<br/>N&#227;o indicado para fins comerciais.</p></div></div></body></html>";

        FileOutputStream fileOut = new FileOutputStream(nome + ".html");
        Writer w = new BufferedWriter(new OutputStreamWriter(fileOut, StandardCharsets.UTF_8));
        w.write(s);
        w.flush();
        w.close();
        fileOut.close();
    }

    /**
     * Imprime uma factura de um contribuinte coletivo para um ficheiro.
     *
     * @param nome Nome do ficheiro
     * @param id Id da factura
     * @param contribuinte Id do contribuinte
     * @throws IOException
     * @throws FacturaNaoExisteException
     */
    public void imprimeFacturaColetivo(String nome, int id, int contribuinte) throws IOException, FacturaNaoExisteException {
        ContribuinteColetivo c = this.coletivos.get(contribuinte);

        Factura fac = null;
        List<Factura> fs = c.getFacturas();

        for (Factura f : fs)
            if(f.getId() == id)
                fac = f;

        if(fac == null)
            throw new FacturaNaoExisteException("A Factura " + id + " não existe!");

        ContribuinteIndividual i = this.individuais.get(fac.getNif_cliente());

        String s =
                "<!DOCTYPE html><html><head><title>Factura</title><style>* { margin: 0; padding: 0; }body { font: 14px/1.4 Georgia, serif; }#page-wrap { width: 800px; margin: 0 auto; }#header { height: 15px; width: 100%; margin: 20px 0; background: #222; text-align: center; color: white; font: bold 15px Helvetica, Sans-Serif; text-decoration: uppercase; letter-spacing: 20px; padding: 8px 0px; }table { border-collapse: collapse; }table td, table th { border: 1px solid black; padding: 5px; }#meta { margin-top: 1px; width: 300px; float: right; }#meta td { text-align: right;  }#meta td.meta-head { text-align: left; background: #eee; }#terms { text-align: center; margin: 20px 0 0 0; }#terms h5 { text-transform: uppercase; font: 13px Helvetica, Sans-Serif; letter-spacing: 10px; border-bottom: 1px solid black; padding: 0 0 8px 0; margin: 0 0 8px 0; }</style></head><body><div id=\"page-wrap\"><p id=\"header\">FACTURA</p><div>"
                + i.getNome()
                + "<br/>NIF:"
                + i.getNIF()
                + "</div><div><br/><br/><p style=\"font-size: 200%; font-weight: bold;\">"
                + c.getNome()
                + "<p/>NIF:"
                + c.getNIF()
                + "<br/>"
                + c.getMorada()
                + "<br/>"
                + c.getEmail()
                + "<br/><br/></div><p style=\"text-align: center;\">"
                + fac.getDescricao()
                + "<br/><br/><br/></p><div id=\"customer\"><table id=\"meta\"><tr><td class=\"meta-head\">Factura</td><td>"
                + fac.getId()
                + "</td></tr><tr><td class=\"meta-head\">Data</td><td>"
                + fac.getData().toString()
                + "</td></tr><tr><td class=\"meta-head\">Total</td><td>&euro;"
                + fac.getValor().toString()
                + "</td></tr></table></div><br/><br/><br/><br/><br/><br/><div id=\"terms\"><h5>Termos</h5><p>Processado por JavaFactura 1.0<br/>N&#227;o indicado para fins comerciais.</p></div></div></body></html>";

        FileOutputStream fileOut = new FileOutputStream(nome + ".html");
        Writer w = new BufferedWriter(new OutputStreamWriter(fileOut, StandardCharsets.UTF_8));
        w.write(s);
        w.flush();
        w.close();
        fileOut.close();

    }

    /**
     * Exporta dados da aplicação para um ficheiro CSV.
     *
     * @param name Nome do ficheiro
     * @throws IOException
     */
   public void exportarCSV(String name) throws IOException {
       StringBuilder s = new StringBuilder();

       s.append("NextId,").append(Factura.getNextId()).append("\n");

       for (ContribuinteIndividual ci : individuais.values()) {
           StringBuilder nifsDependentes = new StringBuilder();
           for (Integer i : ci.getNifsDependentesAgregado())
               nifsDependentes.append(i).append("-");
           if(nifsDependentes.length() == 0)
               nifsDependentes.append("?");

           StringBuilder nifsDirecao = new StringBuilder();
           for (Integer i : ci.getNifsDirecaoAgregado())
               nifsDirecao.append(i).append("-");
           if(nifsDirecao.length() == 0)
               nifsDirecao.append("?");

           s.append("ContribuinteIndividual"
                ).append(",").append(ci.getNIF()
                ).append(",").append(ci.getEmail()
                ).append(",").append(ci.getNome()
                ).append(",").append(ci.getMorada()
                ).append(",").append(ci.getPassword()
                ).append(",").append(ci.getN_direcao()
                ).append(",").append(ci.getN_dependentes()
                ).append(",").append(nifsDependentes
                ).append(",").append(nifsDirecao
                ).append(",").append(ci.getCoeficiente_fiscal()
                ).append("\n"
           );
       }

       for (ContribuinteColetivo cc : coletivos.values()) {
           StringBuilder caes = new StringBuilder();
           for (Integer i : cc.getAtividades_economicas())
               caes.append("-").append(i).append("-");

           s.append("ContribuinteColetivo"
                  ).append(",").append(cc.getNIF()
                  ).append(",").append(cc.getEmail()
                  ).append(",").append(cc.getNome()
                  ).append(",").append(cc.getMorada()
                  ).append(",").append(cc.getPassword()
                  ).append(",").append(caes.toString()
                  ).append(",").append(cc.getConcelho()
                  ).append("\n"
           );

           for (Factura f : cc.getFacturas()) {
               StringBuilder caes_hist = new StringBuilder();
               for (Integer i : f.getHistorico_caes())
                   caes_hist.append(i).append("-");
               if(caes_hist.length() == 0)
                   caes_hist.append("?");

               s.append("Factura"
                    ).append(",").append(f.getId()
                    ).append(",").append(cc.getNIF()
                    ).append(",").append(cc.getNome()
                    ).append(",").append(f.getData().toString()
                    ).append(",").append(f.getNif_cliente()
                    ).append(",").append(f.getDescricao()
                    ).append(",").append(f.getNatureza()
                    ).append(",").append(f.getValor().toString()
                    ).append(",").append(caes_hist.toString()
                    ).append("\n"
               );
           }
       }



       FileOutputStream fileOut = new FileOutputStream(name);
       Writer w = new BufferedWriter(new OutputStreamWriter(fileOut, StandardCharsets.UTF_8));
       w.write(s.toString());
       w.flush();
       w.close();
       fileOut.close();
   }

    /**
     * Importa dados de um ficheiro CSV para a aplicação.
     *
     * @param name Nome do ficheiro
     * @throws IOException
     * @throws CSVformatIncorretException
     */
   public void importarCSV(String name) throws IOException, CSVformatIncorretException {
       String csvFile = name;
       String line;
       String cvsSplitBy = ",";

       BufferedReader br = new BufferedReader(new FileReader(csvFile));

       while ((line = br.readLine()) != null) {
           String[] object = line.split(cvsSplitBy);

           if (object[0].equals("ContribuinteIndividual")) {
               ArrayList<Integer> nifsdependentes = new ArrayList<>();
               String[] nifsdep = object[8].split("-");
               for (String nif : nifsdep)
                   if(!nif.equals("?"))
                       nifsdependentes.add(Integer.parseInt(nif));

               ArrayList<Integer> nifsdirecao = new ArrayList<>();
               String[] nifsdir = object[9].split("-");
               for (String nif : nifsdir)
                   if(!nif.equals("?"))
                       nifsdirecao.add(Integer.parseInt(nif));

               ContribuinteIndividual ci = new ContribuinteIndividual(
                       Integer.parseInt(object[1]), // NIF
                       object[2], // email
                       object[3], // nome
                       object[4], // morada
                       object[5], // password
                       Integer.parseInt(object[6]), // n_direcao
                       Integer.parseInt(object[7]), // n_dependentes
                       nifsdependentes,
                       nifsdirecao,
                       Double.parseDouble(object[10]), // coeficiente_fiscal
                       new HashMap<>() // grupo faturas
               );

               this.add(ci);
           }

           else if (object[0].equals("ContribuinteColetivo")) {
               ArrayList<Integer> atividades_economicas = new ArrayList<>();

               if(object[6].contains("-1-"))
                   atividades_economicas.add(1);
               if(object[6].contains("-2-"))
                   atividades_economicas.add(2);
               if(object[6].contains("-3-"))
                   atividades_economicas.add(3);
               if(object[6].contains("-4-"))
                   atividades_economicas.add(4);
               if(object[6].contains("-5-"))
                   atividades_economicas.add(5);
               if(object[6].contains("-6-"))
                   atividades_economicas.add(6);
               if(object[6].contains("-7-"))
                   atividades_economicas.add(7);
               if(object[6].contains("-8-"))
                   atividades_economicas.add(8);
               if(object[6].contains("-9-"))
                   atividades_economicas.add(9);
               if(object[6].contains("-10-"))
                   atividades_economicas.add(10);
               if(object[6].contains("-11-"))
                   atividades_economicas.add(11);

               Concelho umConcelho = this.getUmConcelho(object[7]);
               double fator_deducao_fiscal = 0.0;

               if(umConcelho != null) {
                   fator_deducao_fiscal = umConcelho.getBeneficio();
               }

               ContribuinteColetivo cc = new ContribuinteColetivo(
                       Integer.parseInt(object[1]), // NIF
                       object[2], // email
                       object[3], // nome
                       object[4], // morada
                       object[5], // password
                       atividades_economicas,
                       fator_deducao_fiscal,
                       object[7], // concelho
                       new ArrayList<>() // facturas
               );

               this.add(cc);
           }

           else if (object[0].equals("Factura")) {
               ArrayList<Integer> atividades_economicas = new ArrayList<>();

               String[] caes = object[9].split("-");
               for (String cae : caes)
                   if(!cae.equals("?"))
                        atividades_economicas.add(Integer.parseInt(cae));

               Factura f = new Factura(
                       Integer.parseInt(object[1]), // id
                       Integer.parseInt(object[2]), // nif emitente
                       object[3], // nome
                       LocalDate.parse(object[4]), // data
                       Integer.parseInt(object[5]), // nif cliente
                       object[6], // descrição
                       Integer.parseInt(object[7]), // natureza
                       new BigDecimal(object[8].replaceAll(",", "")), // valor
                       atividades_economicas
               );

               addFactura(f);
           }

           else if (object[0].equals("NextId")) {
               Factura.setNextId(Integer.parseInt(object[1]));
           }

           else
               throw new CSVformatIncorretException("Não existem entidades do tipo " + object[0]);
       }
   }
}
