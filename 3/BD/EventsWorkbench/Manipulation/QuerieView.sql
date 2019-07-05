CREATE VIEW FDivulgacaoInfluencia as
    Select
        D.*,
        count(PEPD.Participante_Entidade_Id) as Influenciados
    From
        Divulgacao as D,
        PermiteEntrada_Evento_Participante_Divulgacao as PEPD
    where
        PEPD.Divulgacao_id = D.id
    group by
        PEPD.Divulgacao_id
    order by
        count(PEPD.Participante_Entidade_Id) DESC;
    
CREATE VIEW FContagemTipoDivulgacao as
    Select
        D.tipo,
        count(D.tipo) as Contagem
    From
        Divulgacao as D,
        PermiteEntrada_Evento_Participante_Divulgacao as PEPD
    where
        PEPD.Divulgacao_id = D.id
    group by
        D.tipo
    order by
        count(D.tipo) DESC;

        