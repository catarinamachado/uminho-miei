USE EventsWorkbench;
    
Create view Locais(
        Id,
        Nome,
        Lotacao,
        Descricao,
        Tipo,
        Endereco,
        Email,
        Telemovel
    )
) as
Select
    E.id,
    L.Lotacao,
    L.Descricao,
    L.Tipo,
    E.Endereco,
    E.Nome,
    E.Email,
    E.Telemovel
From
    Evento as E,
    Local as L,
where
    L.Entidade_Id = E.id;