delimiter $$
CREATE PROCEDURE OParticipantesDeEvento(IN n_org int,IN n_evento int)
    begin
        Select
            Ent.nome,
            Ent.endereco,
            Ent.email,
            Ent.telemovel
        From
            Evento as E,
            Organizador as O,
            Organizador_has_Evento as OE,
            PermiteEntrada_Evento_Participante_Divulgacao as PEPD,
            Participante as P,
	        Entidade as Ent
        where
            E.id = n_evento
            and E.id = OE.Evento_id
            and OE.Organizador_Entidade_id = n_org
            and PEPD.Evento_id = E.id
            and P.Entidade_id = PEPD.Participante_Entidade_id
	        and Ent.id = P.Entidade_id;
	end $$
delimiter ;

delimiter $$
CREATE PROCEDURE OPlataformasDeEvento(IN n_org int, IN n_div int)
    begin
        Select distinct
            P.*
        From
            Evento as E,
            Organizador as O,
            Organizador_has_Evento as OE,
            Divulgacao as D,
            Plataforma as P
        where
            E.id = OE.Evento_id
            and OE.Organizador_Entidade_id = n_org
            and D.Evento_id = E.id
            and D.id = n_div
            and D.Plataforma_id = P.id;
	end $$
delimiter ;

delimiter $$
CREATE PROCEDURE OLocaisDeEventos(IN n_org int)
    begin
        Select distinct
            Ent.nome,
            Ent.endereco,
            Ent.email,
            Ent.telemovel,
            L.tipo,
            L.descricao,
            L.lotacao
        From
            Evento as E,
            Organizador as O,
            Organizador_has_Evento as OE,
	        Local as L,
            Entidade as ent
        where
            E.id = OE.Evento_id
            and OE.Organizador_Entidade_id = n_org
	        and L.Entidade_id = L.Entidade_id
            and Ent.id = L.Entidade_id;
	end $$
delimiter ;

delimiter $$
CREATE PROCEDURE OEventos(IN n_org int)
    begin
        Select
            E.*
        From
            Evento as E,
            Organizador as O,
            Organizador_has_Evento as OE
        where
            E.id = OE.Evento_id
            and OE.Organizador_Entidade_id = n_org;
	end $$
delimiter ;

delimiter $$
CREATE PROCEDURE ODivulgacao(IN n_div INT,IN n_org INT)
    begin
        Select 
	        D.*
        From 
	        Divulgacao as D,
            Evento as E,
            Organizador_has_Evento as OE
        where
	        D.id = n_divulgacao
            and D.Evento_id = E.id
            and OE.Evento_id = E.id
            and OE.Organizador_Entidade_id = n_org;
	end $$
delimiter ;

delimiter $$
CREATE PROCEDURE ODivulgacaoInfluencia(IN n_div INT,IN n_org INT)
    begin
        Select 
	        D.*,
            count(PEPD.Participante_Entidade_Id) as Influenciados
        From 
	        Divulgacao as D,
            Evento as E,
            Organizador_has_Evento as OE,
            PermiteEntrada_Evento_Participante_Divulgacao as PEPD
        where
            D.id = n_div
	        and PEPD.Divulgacao_id = D.id
            and D.Evento_id = E.id
            and PEPD.Evento_id = E.id
            and OE.Evento_id = E.id
            and OE.Organizador_Entidade_id = n_org
            group by PEPD.Divulgacao_id;
	end $$
delimiter ;
