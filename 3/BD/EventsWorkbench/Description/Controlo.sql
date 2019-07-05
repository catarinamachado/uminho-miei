USE EventsWorkbench;

CREATE ROLE Funcionario;
CREATE ROLE Organizador;
CREATE ROLE Administrador;
CREATE ROLE Participante;


### Administrador
GRANT ALL PRIVILEGES ON * TO Administrador;
##

### Funcionario
GRANT SELECT,UPDATE,INSERT ON EventsWorkbench.* TO Funcionario;
GRANT EXECUTE ON PROCEDURE EventsWorkbench.FParticipanteEvento TO Funcionario;
GRANT EXECUTE ON PROCEDURE EventsWorkbench.FEventoEntreDatas TO Funcionario;
GRANT EXECUTE ON PROCEDURE EventsWorkbench.FEventoEmLocal TO Funcionario;
GRANT EXECUTE ON PROCEDURE EventsWorkbench.FParticipanteMaisGastaTotal TO Funcionario;
GRANT EXECUTE ON PROCEDURE EventsWorkbench.FParticipanteMaisGastaTipoEvento TO Funcionario;
GRANT EXECUTE ON PROCEDURE EventsWorkbench.FParticipanteMaisGastaOrg TO Funcionario;
GRANT EXECUTE ON PROCEDURE EventsWorkbench.FDivulgacaoEficazTipoBruto TO Funcionario;
GRANT EXECUTE ON PROCEDURE EventsWorkbench.FDivulgacaoEficazTipoProporcao TO Funcionario;

##

### Organiador
GRANT EXECUTE ON PROCEDURE EventsWorkbench.OParticipantesDeEvento TO Organizador;
GRANT EXECUTE ON PROCEDURE EventsWorkbench.OPlataformasDeEvento TO Organizador;
GRANT EXECUTE ON PROCEDURE EventsWorkbench.OLocaisDeEventos TO Organizador;
GRANT EXECUTE ON PROCEDURE EventsWorkbench.OEventos TO Organizador;
GRANT EXECUTE ON PROCEDURE EventsWorkbench.ODivulgacao TO Organizador;
GRANT EXECUTE ON PROCEDURE EventsWorkbench.ODivulgacaoInfluencia TO Organizador;

GRANT SELECT ON EventsWorkbench.Local TO Organizador;
GRANT SELECT ON EventsWorkbench.Plataforma TO Organizador;

##

### Participante

##


CREATE USER 'funcionario24'@'localhost' IDENTIFIED BY 'funcionariopassword';
GRANT Funcionario to 'funcionario24';

CREATE USER 'organizador10'@'localhost' IDENTIFIED BY 'organizadorpassword';
GRANT Organizador to 'organizador10';

CREATE USER 'participante32'@'localhost' IDENTIFIED BY 'participantepassword';
GRANT Particpante to 'participante32';



