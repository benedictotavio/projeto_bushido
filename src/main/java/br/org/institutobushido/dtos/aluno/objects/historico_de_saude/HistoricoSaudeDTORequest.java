package br.org.institutobushido.dtos.aluno.objects.historico_de_saude;

import java.util.List;

import br.org.institutobushido.dtos.aluno.objects.historico_de_saude.informacoes_de_saude.alergia.AlergiaDTORequest;
import br.org.institutobushido.dtos.aluno.objects.historico_de_saude.informacoes_de_saude.cirurgia.CirurgiaDTORequest;
import br.org.institutobushido.dtos.aluno.objects.historico_de_saude.informacoes_de_saude.doenca_cronica.DoencaCronicaDTORequest;
import br.org.institutobushido.dtos.aluno.objects.historico_de_saude.informacoes_de_saude.uso_medicamento_continuo.UsoMedicamentoContinuoDTORequest;

public record HistoricoSaudeDTORequest(UsoMedicamentoContinuoDTORequest usoMedicamentoContinuo,
                AlergiaDTORequest alergia,
                CirurgiaDTORequest cirurgia, DoencaCronicaDTORequest doencaCronica, List<String> deficiencia,
                List<String> acompanhamentoSaude) {
}