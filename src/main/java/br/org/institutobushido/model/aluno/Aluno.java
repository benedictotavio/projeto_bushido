package br.org.institutobushido.model.aluno;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import br.org.institutobushido.enums.aluno.Genero;
import br.org.institutobushido.model.aluno.dados_escolares.DadosEscolares;
import br.org.institutobushido.model.aluno.dados_sociais.DadosSociais;
import br.org.institutobushido.model.aluno.endereco.Endereco;
import br.org.institutobushido.model.aluno.graduacao.Graduacao;
import br.org.institutobushido.model.aluno.historico_de_saude.HistoricoSaude;
import br.org.institutobushido.model.aluno.responsaveis.Responsavel;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "alunos")
public class Aluno {
    private String nome;
    private Date dataNascimento;
    private Genero genero;

    @Indexed(unique = true, background = true)
    private String rg;

    private DadosEscolares dadosEscolares;
    private Date dataPreenchimento = new Date();
    private Endereco endereco;
    private DadosSociais dadosSociais;
    private List<Responsavel> responsaveis = new ArrayList<>();
    private Graduacao graduacao;
    private HistoricoSaude historicoSaude;
}
