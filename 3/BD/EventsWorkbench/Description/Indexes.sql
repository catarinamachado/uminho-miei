        
        -- Indices para Permite_Entrada_Evento_Participante_Divulgacao
        CREATE INDEX idx_participante on Permite_Entrada_Evento_Participante_Divulgacao (Participante_Entidade_Id);
        CREATE INDEX idx_divulgacao on Permite_Entrada_Evento_Participante_Divulgacao (Divulgacao_Id);

        --Indice para Local
        CREATE INDEX idx_tipo on Local (Tipo);

        --Indice para Divulgacao
        CREATE INDEX idx_evento on Divulgacao (Evento_Id);
