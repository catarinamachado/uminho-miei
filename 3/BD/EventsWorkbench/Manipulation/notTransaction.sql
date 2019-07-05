USE EventsWorkbench;
DELIMITER $$ 
CREATE PROCEDURE addClassificacao(
    IN id_particante INT,
    IN id_evento INT,
    IN apreciation DECIMAL(4, 2)
) 
    BEGIN
        UPDATE
            PermiteEntrada_Evento_Participante_Divulgacao as PEPD
        SET
            PEPD.Classificacao = apreciation
        WHERE
            PEPD.Evento_id = id_evento
            and PEPD.Entidade_Participante_id = id_particante 
    END $$ 
DELIMITER ;

DELIMITER $$ 
CREATE PROCEDURE ValidarDivulgacao(IN id_divulgacao INT)
    BEGIN
        UPDATE
            Divulgacao as D
        SET
            D.Validade = 2
        WHERE
            D.id = id_divulgacao 
    END $$ 
DELIMITER ;

DELIMITER $$ 
CREATE PROCEDURE AlterarEstadoParticipacao(
        IN id_evento INT,
        IN id_participante INT,
        IN estado_novo ENUM('Válido', 'Cancelado', 'Reservado')
    ) 
    BEGIN
        UPDATE
            PermiteEntrada_Evento_Participante_Divulgacao as PEPD
        SET
            PEPD.Estado = estado_novo
        WHERE
            EPD.Participante_Entidade_id = id_particante
            and PEPD.Evento_id = id_evento 
            
    END $$ 
DELIMITER ; 

DELIMITER $$ 
CREATE PROCEDURE CriarPlataforma(
        IN id INT,
        IN nome VARCHAR(64)
    ) 
    BEGIN
        INSERT INTO
            Plataforma(id, nome)
        VALUES(id, nome) 
    END $$ 
        
DELIMITER ; 

DELIMITER $$ 
CREATE PROCEDURE CriarDivulgacao(
        IN d_id INT,
        IN d_conteudo TEXT(1024),
        IN d_custo DECIMAL(12, 2),
        IN d_tipo ENUM(
            "Audiovisual",
            "Áudio",
            "Publicação",
            "Cartaz",
            "Email",
            "Brochuras",
            "Aresentações",
            "Promoções",
            "Panfletos",
            "Outros"
        ),
        IN d_DataInicio DATETIME,
        IN d_DateFim DATETIME,
        IN d_plataforma_id INT,
        IN d_Evento_id INT
    ) 
    BEGIN
        INSERT INTO
            Divulgacao(
                id,
                conteudo,
                tipo,
                custo,
                DataInicio,
                DataFim,
                plataforma_id,
                Evento_id
                     )
        VALUES(
            d_id,
            d_conteudo,
            d_tipo,
            d_custo,
            d_DataInicio,
            d_DataFim,
            d_plataforma_id,
            d_Evento_id
    ) 
    END $$ 
DELIMITER;

-- 1. Adicionar Local
DELIMITER $$
CREATE PROCEDURE addLocalComEntidadeExistente(IN id INT, IN lotacao INT, IN descricao TEXT(256), 
														IN tipo ENUM('Bares‎', 'Casas noturnas', 'Casa de espetáculos', 'Hotelaria', 'Restaurantes', 
                                                        'Centros Culturais', 'Multiusos', 'Quintas', 'Outros'))
    BEGIN
		INSERT INTO Local (entidade_id, lotacao, descricao, tipo)
		VALUES (id, lotacao, descricao, tipo);    
    END $$
DELIMITER ;

# 2. Adicionar Organizador
DELIMITER $$
CREATE PROCEDURE addOrganizadorComEntidadeExistente(IN id INT, IN descricao TEXT(512))
    BEGIN
		INSERT INTO Organizador(entidade_id, descricao)
		VALUES (id, descricao);
    END $$
DELIMITER ;

# 3. Adicionar Participante
DELIMITER $$
CREATE PROCEDURE addParticipanteComEntidadeExistente(IN id INT, IN datadenascimento DATE, IN genero ENUM('Feminino', 'Masculino'), IN nif INT)
    BEGIN
		INSERT INTO Participante (entidade_id, datadenascimento, genero, nif)
		VALUES  (id, datadenascimento, genero, nif);
    END $$
DELIMITER ;
