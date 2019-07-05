delimiter $$
CREATE PROCEDURE FParticipanteEvento(IN n_evento int)
    begin
        Select distinct
            E.id as Id,
            P.genero as Genero,
            P.nif as Nif,
            P.datadenascimento as 'DatadeNascimento',
            Ent.nome as Nome, Ent.endereco as 'Endereço', 
            Ent.email as Email, Ent.telemovel as 'NumerodeTelefone'
        From
            Evento as E,
            PermiteEntrada_Evento_Participante_Divulgacao as PEPD,
            Participante as P,
	        Entidade as Ent
        where
	        E.id = n_evento
            and PEPD.Evento_id = E.id
            and P.Entidade_id = PEPD.Participante_Entidade_id
	        and Ent.id = P.Entidade_id;
	end $$
delimiter ;

delimiter $$
CREATE PROCEDURE FEventoEntreDatas(IN d_start DATETIME,IN d_end DATETIME)
    begin
        Select *
        From 
	        Evento as E
        where 
	        E.data between d_start and d_end;
	end $$
delimiter ;

delimiter $$
CREATE PROCEDURE FEventoEmLocal(IN n_local INT)
    begin
        Select E.*
        From 
	        Evento as E
        where 
	        E.Local_Entidade_Id = n_local;
	end $$
delimiter ;

delimiter $$
CREATE PROCEDURE FParticipanteMaisGastaTotal(In n int)
    begin
        Select 
	        E.*, P.*,
            sum(PEPD.Preco) as Gasto
        From 
	        Participante as P,
            PermiteEntrada_Evento_Participante_Divulgacao as PEPD,
            Entidade as E
        where 
            P.Entidade_id = PEPD.Participante_Entidade_Id
            and P.Entidade_id = E.id
        group by P.Entidade_id
        order by sum(PEPD.Preco) DESC
        limit n;   
	end $$
delimiter ;

delimiter $$
CREATE PROCEDURE FParticipanteMaisGastaTipoEvento(In n int, In n_tipo_e int)
    begin
        Select 
	        E.*,
            P.*,
            sum(PEPD.Preco) as Gasto
        From 
	        Participante as P,
            PermiteEntrada_Evento_Participante_Divulgacao as PEPD,
            Entidade as E,
            Evento as EV
        where 
            P.Entidade_id = PEPD.Participante_Entidade_Id
            and P.Entidade_id = E.id
            and PEPD.Evento_id = EV.id
            and EV.tipo = n_tipo_e

        group by P.Entidade_id
        order by sum(PEPD.Preco) DESC
        limit n;   
	end $$
delimiter ;

delimiter $$
CREATE PROCEDURE FParticipanteMaisGastaOrg(In n int, In n_org int)
    begin
        Select 
	        E.*,
            P.*,
            sum(PEPD.Preco) as Gasto
        From 
	        Participante as P,
            PermiteEntrada_Evento_Participante_Divulgacao as PEPD,
            Entidade as E,
            Evento as EV,
            Organizador as O,
            Organizador_has_Evento as OE

        where 
            P.Entidade_id = PEPD.Participante_Entidade_Id
            and P.Entidade_id = E.id
            and PEPD.Evento_id = EV.id
            and EV.id = OE.Evento_id
            and OE.Organizador_Entidade_id = n_org

        group by P.Entidade_id
        order by sum(PEPD.Preco) DESC
        limit n;   
	end $$
delimiter ;

delimiter $$
CREATE PROCEDURE FDivulgacaoEficazTipoBruto(In n_tipo_e int)
    begin
        Select 
	        D.tipo,
            count(PEPD.Participante_Entidade_Id) as Influenciados
        From 
	        Divulgacao as D,
            Evento as E,
            PermiteEntrada_Evento_Participante_Divulgacao as PEPD
        where
	        E.tipo = n_tipo_e
	        and D.Evento_id = E.id
            and PEPD.Evento_id = E.id
            and PEPD.Divulgacao_id = D.id
            group by D.tipo
            order by count(PEPD.Participante_Entidade_Id) DESC;
	end $$
delimiter ;

delimiter $$
CREATE PROCEDURE FDivulgacaoEficazTipoProporcao(In n_tipo_e int)
    begin
        Select 
	        D.tipo,
            sum(PEPD.Preco)/count(PEPD.Participante_Entidade_Id) as 'CustoPorParticipante'
        From 
	        Divulgacao as D,
            Evento as E,
            PermiteEntrada_Evento_Participante_Divulgacao as PEPD
        where
	        E.tipo = n_tipo_e
	        and D.Evento_id = E.id
            and PEPD.Evento_id = E.id
            and PEPD.Divulgacao_id = D.id
            group by D.tipo
            order by sum(PEPD.Preco)/count(PEPD.Participante_Entidade_Id) DESC;
	end $$
delimiter ;