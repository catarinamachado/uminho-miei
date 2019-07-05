/**
 * Contribuinte é a entidade que emite ou sobre a qual é emitida a despesa.
 *
 * @author A81047
 * @author A34900
 * @author A82339
 * @version 20180513
 */

import java.io.*;

public abstract class Contribuinte implements Serializable{
    private int NIF;
    private String email;
    private String nome;
    private String morada;
    private String password;

    /**
     * Construtor por omissão de Contribuinte.
     */
    public Contribuinte() {
        this.NIF = 0;
        this.email = this.nome = this.morada = this.password = "";
    }

    /**
     * Construtor parametrizado de Contribuinte.
     *
     * @param NIF Identificador
     * @param email Endereço de email
     * @param nome Nome
     * @param morada Morada
     * @param password Password
     */
    public Contribuinte(int NIF, String email, String nome, String morada, String password) {
        this.NIF = NIF;
        this.email = email;
        this.nome = nome;
        this.morada = morada;
        this.password = password;
    }

    /**
     * Construtor de Cópia
     *
     * @param umContribuinte Contribuinte a ser replicado
     */
    public Contribuinte(Contribuinte umContribuinte) {
        this.NIF = umContribuinte.getNIF();
        this.email = umContribuinte.getEmail();
        this.nome = umContribuinte.getNome();
        this.morada = umContribuinte.getMorada();
        this.password = umContribuinte.getPassword();
    }

    /**
     * Devolve identificador do contribuinte.
     *
     * @return NIF
     */
    public int getNIF() {
        return NIF;
    }

    /**
     * Actualiza o valor do identificador do contribuinte.
     *
     * @param NIF novo identificador
     */
    public void setNIF(int NIF) {
        this.NIF = NIF;
    }

    /**
     * Devolve endereço de email do contribuinte.
     *
     * @return email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Actualiza o valor do endereço de email do contribuinte.
     *
     * @param email novo email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Devolve nome do contribuinte.
     *
     * @return nome
     */
    public String getNome() {
        return nome;
    }

    /**
     * Actualiza o nome do contribuinte.
     *
     * @param nome nome do contribuinte
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * Devolve morada do contribuinte.
     *
     * @return morada
     */
    public String getMorada() {
        return morada;
    }

    /**
     * Actualiza o morada do contribuinte.
     *
     * @param morada morada do contribuinte
     */
    public void setMorada(String morada) {
        this.morada = morada;
    }

    /**
     * Devolve password do contribuinte.
     *
     * @return password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Actualiza o password do contribuinte.
     *
     * @param password password do contribuinte
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Método que determina se dois contribuintes são iguais.
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

        Contribuinte contribuinte = (Contribuinte) object;
        return  NIF == contribuinte.getNIF() &&
                email.equals(contribuinte.getEmail()) &&
                nome.equals(contribuinte.getNome()) &&
                morada.equals(contribuinte.getMorada()) &&
                password.equals(contribuinte.getPassword());
    }

    /**
     * Devolve valor de hash baseado no NIF do contribuinte.
     *
     * @return valor de hash
     */
    public int hashCode() {
        return java.util.Objects.hash(super.hashCode(), NIF);
    }

    /**
     * Método que devolve a representação em String de Contribuinte.
     *
     * @return String que representa um contribuinte
     */
    public String toString() {
        return  "NIF=" + NIF +
                ", Email='" + email + '\'' +
                ", Nome='" + nome + '\'' +
                ", Morada='" + morada + '\'' +
                ", Password='" + password + '\'';
    }

    /**
     * Método que faz uma cópia do contribuinte receptor da mensagem.
     * Para tal invoca o construtor de cópia .
     *
     * @return Cópia do contribuinte
     */
    public abstract Contribuinte clone();
}
