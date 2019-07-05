USE EventsWorkbench;# RE1 arg:numorg

### Testar
Select *
From Entidade;

Select *
From Local;

Select *
From Organizador;

Select *
From Participante;

Select *
From Evento;

Select *
From PermiteEntrada_Evento_Participante_Divulgacao;

Select *
From Divulgacao;

CALL FContagemTipoDivulgacao;

CALL FDivulgacaoInfluencia;

CALL FParticipanteMaisGastaTotal(40);