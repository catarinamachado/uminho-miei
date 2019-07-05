USE EventsWorkbench;

-- 1. Adicionar Local
DELIMITER $$
CREATE PROCEDURE addLocal(IN id INT, IN endereco VARCHAR(128), IN nome VARCHAR(64), 
														IN email VARCHAR(45), IN telemovel VARCHAR(15),
														IN lotacao INT, IN descricao TEXT(256), 
														IN tipo ENUM('Bares‎', 'Casas noturnas', 'Casa de espetáculos', 'Hotelaria', 'Restaurantes', 
                                                        'Centros Culturais', 'Multiusos', 'Quintas', 'Outros'))
BEGIN
        
	DECLARE EXIT HANDLER FOR SQLEXCEPTION 
    BEGIN
        ROLLBACK;
    END;
    
	START TRANSACTION;
		-- criar Entidade
        INSERT INTO Entidade (id, endereco, nome, email, telemovel)
		VALUES (id, endereco, nome, email, telemovel);
        -- criar Local
		INSERT INTO Local (entidade_id, lotacao, descricao, tipo)
		VALUES (id, lotacao, descricao, tipo);
        
		COMMIT;
    
END $$
DELIMITER ;

/* TESTE
CALL addLocal(91, "London", "Caty", "catarinsfa8787er8@gmail.com", "6ereff868", 34, "Teste do Local 3", 2);

SELECT * 
FROM Entidade
WHERE id = 91;
*/


# 2. Adicionar Organizador
DELIMITER $$
CREATE PROCEDURE addOrganizador(IN id INT, IN endereco VARCHAR(128), IN nome VARCHAR(64), 
																   IN email VARCHAR(45), IN telemovel VARCHAR(15), IN descricao TEXT(512))
BEGIN
	DECLARE EXIT HANDLER FOR SQLEXCEPTION 
    BEGIN
        ROLLBACK;
    END;
    
    START TRANSACTION;
		-- criar Entidade
        INSERT INTO Entidade (id, endereco, nome, email, telemovel)
		VALUES (id, endereco, nome, email, telemovel);
        -- criar Organizador
		INSERT INTO Organizador(entidade_id, descricao)
		VALUES (id, descricao);
        
		COMMIT;
    
END $$
DELIMITER ;

/* TESTE
CALL addOrganizador(901, "London", "Caty", "olagfg@gmail.com", "999fg888", "ola esta tudo bem e comigoooo");

SELECT * 
FROM Organizador
WHERE Entidade_Id = 901;

SELECT * 
FROM Entidade
WHERE id = 901;
*/


# 3. Adicionar Participante
DELIMITER $$
CREATE PROCEDURE addParticipante(IN id INT, IN endereco VARCHAR(128), IN nome VARCHAR(64), 
																 IN email VARCHAR(45), IN telemovel VARCHAR(15),  
																 IN datadenascimento DATE, IN genero ENUM('Feminino', 'Masculino'), IN nif INT)
BEGIN
	DECLARE EXIT HANDLER FOR SQLEXCEPTION 
    BEGIN
        ROLLBACK;
    END;
    
    START TRANSACTION;
		-- criar Entidade
        INSERT INTO Entidade (id, endereco, nome, email, telemovel)
		VALUES (id, endereco, nome, email, telemovel);
        -- criar Participante
		INSERT INTO Participante (entidade_id, datadenascimento, genero, nif)
		VALUES  (id, datadenascimento, genero, nif);
        
		COMMIT;
    
END $$
DELIMITER ;

/* TESTE
CALL addParticipante(905, "London", "Caty", "olaadfdfaa@gmail.com", "99fdf9sds888", '1998-04-01', 2, 876543234);

SELECT * 
FROM Participante
WHERE Entidade_id = 905;

SELECT * 
FROM Entidade
WHERE id = 905;
*/



# 4. Adicionar Evento
DELIMITER $$
CREATE PROCEDURE addEvento(IN id INT, IN nome VARCHAR(64), IN descricao TEXT(512), 
														  IN tipo ENUM('Discussão, informação e formação', 'Religiosos e comunitários', 'Académicos', 
														  'Políticos e de protocolo', 'Culturais, de lazer, desportivos e musicais', 'Culinária', 'Empresariais', 'Outros'), 
                                                          IN data DATETIME, IN duracao TIME, IN preco DECIMAL(7,2), IN dataInicioRegistoParticipantes DATETIME, 
                                                          IN dataFimRegistoParticipantes DATETIME, IN classificacao DECIMAL(4,3), IN numeroMaximoDeParticipantes INT, 
                                                          IN local_Entidade_Id INT)
BEGIN
	DECLARE maxlotacao INT;
	DECLARE `_rollback` BOOL DEFAULT 0;
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION SET `_rollback` = 1;
        
    START TRANSACTION;
		-- guardar lotacao maxima do local numa variavel
		SET maxlotacao = (SELECT Local.lotacao
										FROM Local
										WHERE Local.Entidade_Id = local_Entidade_Id);
		-- verificar se o local possui a lotacao maxima necessaria
		IF (maxlotacao < numeroMaximoDeParticipantes) THEN 
			SET `_rollback` = 1;
        END IF;
        -- criar Evento
		INSERT INTO Evento (id, nome, descricao, tipo, data, duracao, preco, dataInicioRegistoParticipantes, 
											dataFimRegistoParticipantes, classificacao, numeroMaximoDeParticipantes, local_Entidade_Id)
		VALUES (id, nome, descricao, tipo, data, duracao, preco, dataInicioRegistoParticipantes,  
						dataFimRegistoParticipantes, classificacao, numeroMaximoDeParticipantes, local_Entidade_Id);
                        
		IF `_rollback` THEN
			ROLLBACK;
		ELSE
			COMMIT;
		END IF;
    
END $$
DELIMITER ;


/* TESTE
CALL addEvento (112,'Quiver','Concerto do Quiver',5,'2017-08-23',CURRENT_TIME(), 34.34,'2017-08-10','2017-08-23',2.4,9,40);

INSERT INTO Evento (id, nome, descricao, tipo, data, duracao, preco, dataInicioRegistoParticipantes, dataFimRegistoParticipantes, classificacao, numeroMaximoDeParticipantes, local_Entidade_Id)
VALUES (105,'Quiver','Concerto do Quiver',5,'2017-08-23',CURRENT_TIME(), 34.34,'2017-08-10','2017-08-23',2.4,900,40);

SELECT Local.lotacao
FROM Local
WHERE Local.Entidade_Id = 40;

SELECT * 
FROM EVENTO
where id = 112;
*/



# 6. Associar um participante a um evento informando a divulgação
DELIMITER $$
CREATE PROCEDURE addParticipanteEventoDivulgacao(IN participante_entidade_id INT, IN evento_id INT, IN divulgacao_id INT, 
																							   IN classificacao INT, IN estado ENUM('Válido', 'Cancelado', 'Reservado') , 
                                                                                               IN preco DECIMAL(7,2), IN lugar VARCHAR(32))
BEGIN
	DECLARE evento_divulgacao INT;
	DECLARE nr_participantes_maximo INT;
    DECLARE nr_participantes_no_evento INT;
    DECLARE `_rollback` BOOL DEFAULT 0;
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION SET `_rollback` = 1;

    START TRANSACTION;
		-- guardar variavel com o id do evento publicitado na divulgacao
    	SET evento_divulgacao = (SELECT d.evento_id
													FROM Divulgacao d
													WHERE d.id = divulgacao_id);
        -- numero maximo de participantes permitido evento
		SET nr_participantes_maximo = (SELECT e.NumeroMaximoDeParticipantes
															 FROM Evento e
															 WHERE e.id = evento_id);
		-- numero de participantes inscritos no evento
		SET nr_participantes_no_evento = (SELECT COUNT(pe.evento_id)
																  FROM PermiteEntrada_Evento_Participante_Divulgacao pe
																   WHERE pe.evento_id = evento_id);
		-- verificar se o evento associado à divulgação é o mesmo evento que se prende adicionar ao relacionamento ternário
		IF (evento_divulgacao <> evento_id) THEN 
			SET `_rollback` = 1;
        END IF;
        -- verificar se o número de participantes máximo já foi atingido
		IF (nr_participantes_maximo = nr_participantes_no_evento) THEN 
			SET `_rollback` = 1;
        END IF;
        -- criar Evento-Participante-Divulgacao
		INSERT INTO PermiteEntrada_Evento_Participante_Divulgacao (participante_entidade_id,evento_id,divulgacao_id,classificacao,estado,preco,lugar)
		VALUES (participante_entidade_id,evento_id,divulgacao_id,classificacao,estado,preco,lugar);
        
		IF `_rollback` THEN
			ROLLBACK;
		ELSE
			COMMIT;
		END IF;
    
END $$
DELIMITER ;

/* TESTE
CALL addParticipanteEventoDivulgacao(8,2,2,6,1, 90, 'L78');

*/

# 8. Participante atualiza classificação

DELIMITER $$

CREATE TRIGGER after_classificacao_update
AFTER UPDATE ON PermiteEntrada_Evento_Participante_Divulgacao 
FOR EACH ROW 
BEGIN

DECLARE nr_classificacoes INT;
DECLARE ex_media DECIMAL(4,2);
DECLARE old_somaclassificacao_total INT;
DECLARE result INT;
SET nr_classificacoes = (SELECT COUNT(classificacao)
									     FROM PermiteEntrada_Evento_Participante_Divulgacao
									     WHERE evento_id = NEW.evento_id AND (classificacao IS NOT NULL));
SET ex_media = (SELECT classificacao
							 FROM Evento
							 WHERE id = NEW.evento_id);

IF OLD.classificacao IS NOT NULL THEN
	IF NEW.classificacao IS NOT NULL THEN
		SET old_somaclassificacao_total = nr_classificacoes * ex_media;
		SET result = (old_somaclassificacao_total - OLD.classificacao + NEW.classificacao) / nr_classificacoes;
	ELSE
		IF nr_classificacoes <> 0 THEN
			SET old_somaclassificacao_total = (nr_classificacoes + 1) * ex_media;
			SET result = (old_somaclassificacao_total - OLD.classificacao) / nr_classificacoes;
        ELSE
			SET result = null;
        END IF;
	END IF;
ELSE 
	IF ex_media IS NOT NULL THEN
		SET old_somaclassificacao_total = (nr_classificacoes - 1) * ex_media;
	ELSE 
		SET old_somaclassificacao_total = 0;
	END IF;
    SET result = (old_somaclassificacao_total + NEW.classificacao) / nr_classificacoes;
END IF;
									
UPDATE Evento
SET Evento.Classificacao = result
WHERE Evento.id = NEW.evento_id;   
    
END $$

DELIMITER ;


/* TESTE
DROP TRIGGER after_classificacao_update;
DROP TRIGGER after_classificacao_insert;


 SELECT *
 FROM Evento
 Where Evento.id = 2;

SELECT *
 FROM PermiteEntrada_Evento_Participante_Divulgacao
 Where Evento_id = 2;
 
UPDATE PermiteEntrada_Evento_Participante_Divulgacao
SET classificacao = 6
WHERE evento_id = 2 AND participante_entidade_id = 6;

UPDATE Evento
SET classificacao = 4
WHERE id = 2;


DELETE FROM PermiteEntrada_Evento_Participante_Divulgacao WHERE evento_id = 2 AND participante_entidade_id = 6;

 
SELECT COUNT(classificacao)
FROM PermiteEntrada_Evento_Participante_Divulgacao pe
WHERE evento_id = 2 AND classificacao IS NOT NULL;

CALL addParticipanteEventoDivulgacao(6,2,2,6,1, 90, 'L78');

CALL addEvento(105,'Quiver','Concerto do Quiver',5,'2017-08-23',CURRENT_TIME(), 34.34,'2017-08-10','2017-08-23',10,9,40);

UPDATE Evento
SET Evento.Classificacao = (Evento.classificacao*(5-1) + 10)  / 5
WHERE Evento.id = 20;

*/

# 8. Funcionário adiciona nova linha com classificação ja incluida do participante

DELIMITER $$

CREATE TRIGGER after_classificacao_insert
AFTER INSERT ON PermiteEntrada_Evento_Participante_Divulgacao 
FOR EACH ROW 
BEGIN

DECLARE nr_classificacoes INT;
DECLARE ex_media DECIMAL(4,2);
DECLARE old_somaclassificacao_total INT;

IF NEW.classificacao IS NOT NULL THEN

	SET nr_classificacoes = (SELECT COUNT(classificacao)
											FROM PermiteEntrada_Evento_Participante_Divulgacao
											WHERE evento_id = NEW.evento_id AND (NEW.classificacao IS NOT NULL));
	SET ex_media = (SELECT classificacao
								FROM Evento
								WHERE id = NEW.evento_id);

	IF ex_media IS NOT NULL THEN
		SET old_somaclassificacao_total = (nr_classificacoes - 1) * ex_media;
	ELSE 
		SET old_somaclassificacao_total = 0;
	END IF;
						
	UPDATE Evento
	SET Evento.Classificacao = (old_somaclassificacao_total + NEW.classificacao) / nr_classificacoes
	WHERE Evento.id = NEW.evento_id;   
    
END IF;
    
END $$

DELIMITER ;


# 9. Apagar uma entrada do relacionamento ternário

DELIMITER $$

CREATE TRIGGER after_classificacao_delete
AFTER DELETE ON PermiteEntrada_Evento_Participante_Divulgacao 
FOR EACH ROW 
BEGIN

DECLARE nr_classificacoes INT;
DECLARE ex_media DECIMAL(4,2);
DECLARE result INT;
DECLARE old_somaclassificacao_total INT;

IF OLD.classificacao IS NOT NULL THEN
	SET nr_classificacoes = (SELECT COUNT(classificacao)
									         FROM PermiteEntrada_Evento_Participante_Divulgacao
									         WHERE evento_id = OLD.evento_id AND (classificacao IS NOT NULL));
	SET ex_media = (SELECT classificacao
							     FROM Evento
							     WHERE id = OLD.evento_id);
	IF nr_classificacoes <> 0 THEN
		SET old_somaclassificacao_total = (nr_classificacoes + 1) * ex_media;
        SET result = (old_somaclassificacao_total - OLD.classificacao) / nr_classificacoes;
	ELSE
		SET result = null;
    END IF;
    
	UPDATE Evento
	SET Evento.Classificacao = result
	WHERE Evento.id = OLD.evento_id;    
    
END IF;

END $$

DELIMITER ;

/*

DROP TRIGGER after_classificacao_delete;


DELETE FROM PermiteEntrada_Evento_Participante_Divulgacao
WHERE Evento_id = 2 AND Participante_Entidade_id = 8 AND Divulgacao_id = 2;
*/
