package RelationalDB;

import java.sql.ResultSet;
import java.sql.SQLException;

public final class EventsWorkbenchGetter {

    private final RelationalDatabaseConnection l;

    public EventsWorkbenchGetter(String user, String password, String ip) throws SQLException, ClassNotFoundException {
        l = new RelationalDatabaseConnection("jdbc:mysql://" + ip + "/EventsWorkbench", user, password);
    }

    public void termina() throws SQLException{
        l.terminateConnection();
    }

    public ResultSet participante() throws SQLException{
        String participantes = "select Entidade.id, Entidade.nome, Entidade.email, Entidade.telemovel, Participante.genero, Participante.nif, Participante.DataDeNascimento " +
                " from Entidade inner join Participante on Entidade.id = Participante.entidade_id;";
        return this.l.query(participantes);
    }

    public ResultSet organizador() throws SQLException{
        String organizadores = "select Entidade.id, Entidade.nome, Entidade.email " +
                "from Entidade inner join Organizador on Entidade.id = Organizador.entidade_id;";
        return this.l.query(organizadores);
    }

    public ResultSet evento() throws SQLException {
        String eventos = "select id, nome from Evento;";
        return this.l.query(eventos);
    }

    public ResultSet divulgacao() throws SQLException {
        String divulgacoes = "select id, tipo, custo from Divulgacao;";
        return this.l.query(divulgacoes);
    }

    public ResultSet organiza() throws SQLException {
        String organizadorevento = "select * from Organizador_has_Evento;";
        return this.l.query(organizadorevento);
    }

    public ResultSet participa() throws SQLException {
        String evento_participante = "select participante_entidade_id, evento_id, preco " +
                " from PermiteEntrada_Evento_Participante_Divulgacao ;";
        return this.l.query(evento_participante);
    }

    public ResultSet divulga() throws SQLException {
        String evento_divulga = "select id, evento_id from Divulgacao ;";
        return this.l.query(evento_divulga);
    }

    public ResultSet influencia() throws SQLException {
        String participante_divulgacao = "select divulgacao_id, participante_entidade_id " +
                " from PermiteEntrada_Evento_Participante_Divulgacao where divulgacao_id IS NOT NULL; ";
        return this.l.query(participante_divulgacao);
    }

}
