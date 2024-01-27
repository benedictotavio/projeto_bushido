package br.org.institutobushido.services.aluno;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.mongodb.MongoException;

import br.org.institutobushido.dtos.aluno.AlunoDTORequest;
import br.org.institutobushido.dtos.aluno.AlunoDTOResponse;
import br.org.institutobushido.dtos.aluno.objects.responsavel.ResponsavelDTORequest;
import br.org.institutobushido.dtos.aluno.objects.responsavel.ResponsavelDTOResponse;
import br.org.institutobushido.mappers.DadosEscolaresMapper;
import br.org.institutobushido.mappers.DadosSociaisMapper;
import br.org.institutobushido.mappers.EnderecoMapper;
import br.org.institutobushido.mappers.GraduacaoMapper;
import br.org.institutobushido.mappers.HistoricoSaudeMapper;
import br.org.institutobushido.mappers.ResponsavelMapper;
import br.org.institutobushido.model.aluno.Aluno;
import br.org.institutobushido.model.aluno.objects.Faltas;
import br.org.institutobushido.model.aluno.objects.Responsavel;
import br.org.institutobushido.repositories.AlunoRepositorio;

@Service
public class AlunoServices implements AlunoServicesInterface {

    @Autowired
    private AlunoRepositorio alunoRepositorio;
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public AlunoDTOResponse adicionarAluno(AlunoDTORequest alunoDTORequest) {

        Optional<Aluno> alunoEncontrado = alunoRepositorio.findByRg(alunoDTORequest.rg());

        if (!alunoEncontrado.isPresent()) {
            Aluno aluno = new Aluno();
            aluno.setGenero(alunoDTORequest.genero());
            aluno.setDataNascimento(alunoDTORequest.dataNascimento());
            aluno.setNome(alunoDTORequest.nome());
            aluno.setRg(alunoDTORequest.rg());
            aluno.setGraduacao(GraduacaoMapper.mapToGraduacao(alunoDTORequest.graduacao()));
            aluno.setResponsaveis(ResponsavelMapper.mapToResponsaveis(alunoDTORequest.responsaveis()));
            aluno.setEndereco(EnderecoMapper.mapToEndereco(alunoDTORequest.endereco()));
            aluno.setDadosSociais(DadosSociaisMapper.mapToDadosSociais(alunoDTORequest.dadosSociais()));
            aluno.setDadosEscolares(DadosEscolaresMapper.mapToDadosEscolares(alunoDTORequest.dadosEscolares()));
            aluno.setHistoricoSaude(HistoricoSaudeMapper.mapToHistoricoSaude(alunoDTORequest.historicoSaude()));
            Aluno novoAluno = alunoRepositorio.save(aluno);

            return AlunoDTOResponse.builder()
                    .withNome(novoAluno.getNome())
                    .withGenero(novoAluno.getGenero())
                    .withDataNascimento(novoAluno.getDataNascimento())
                    .withDataPreenchimento(novoAluno.getDataPreenchimento())
                    .withRg(novoAluno.getRg())
                    .withResponsaveis(ResponsavelMapper.mapToResponsaveisDTOResponse(novoAluno.getResponsaveis()))
                    .withEndereco(EnderecoMapper.mapToEnderecoDTOResponse(novoAluno.getEndereco()))
                    .withDadosSociais(DadosSociaisMapper.mapToDadosSociaisDTOResponse(novoAluno.getDadosSociais()))
                    .withDadosEscolares(
                            DadosEscolaresMapper.mapToDadosEscolaresDTOResponse(novoAluno.getDadosEscolares()))
                    .withGraduacao(GraduacaoMapper.mapToGraduacaoDTOResponse(novoAluno.getGraduacao()))
                    .withHistoricoSaude(
                            HistoricoSaudeMapper.mapToHistoricoSaudeDTOResponse(novoAluno.getHistoricoSaude()))
                    .build();
        }

        throw new MongoException("O Aluno com o rg " + alunoDTORequest.rg() + " ja esta cadastrado!");
    }

    @Override
    public AlunoDTOResponse buscarAlunoPorRg(String rg) {
        Aluno alunoEncontrado = encontrarAlunoPorRg(rg);

        return AlunoDTOResponse.builder()
                .withNome(alunoEncontrado.getNome())
                .withGenero(alunoEncontrado.getGenero())
                .withDataNascimento(alunoEncontrado.getDataNascimento())
                .withDadosSociais(DadosSociaisMapper.mapToDadosSociaisDTOResponse(alunoEncontrado.getDadosSociais()))
                .withDataPreenchimento(alunoEncontrado.getDataPreenchimento())
                .withRg(alunoEncontrado.getRg())
                .withResponsaveis(ResponsavelMapper.mapToResponsaveisDTOResponse(alunoEncontrado.getResponsaveis()))
                .withEndereco(EnderecoMapper.mapToEnderecoDTOResponse(alunoEncontrado.getEndereco()))
                .withDadosEscolares(
                        DadosEscolaresMapper.mapToDadosEscolaresDTOResponse(alunoEncontrado.getDadosEscolares()))
                .withGraduacao(GraduacaoMapper.mapToGraduacaoDTOResponse(alunoEncontrado.getGraduacao()))
                .withHistoricoSaude(
                        HistoricoSaudeMapper.mapToHistoricoSaudeDTOResponse(alunoEncontrado.getHistoricoSaude()))
                .build();
    }

    @Override
    public ResponsavelDTOResponse adicionarResponsavel(String rg, ResponsavelDTORequest responsavelDTORequest) {
        Aluno aluno = encontrarAlunoPorRg(rg);
        Optional<Responsavel> responsavel = encontrarResponsavelPorCpf(aluno, responsavelDTORequest.cpf());
        if (aluno.getResponsaveis().size() < 5 && responsavel.isEmpty()) {
            Query query = new Query();
            query.addCriteria(Criteria.where("rg").is(aluno.getRg()));
            Update update = new Update().push("responsaveis", responsavelDTORequest);
            mongoTemplate.updateFirst(query, update, Aluno.class);
            return ResponsavelDTOResponse.builder().withCpf(responsavelDTORequest.cpf())
                    .withEmail(responsavelDTORequest.email()).withFiliacao(responsavelDTORequest.filiacao().toString())
                    .withNome(responsavelDTORequest.nome())
                    .withTelefone(responsavelDTORequest.telefone()).build();
        }
        throw new MongoException("Não foi possivel adicionar esse responsavel");
    }

    @Override
    public boolean removerResponsavel(String rg, String cpf) {
        Aluno aluno = encontrarAlunoPorRg(rg);
        Optional<Responsavel> responsavel = encontrarResponsavelPorCpf(aluno, cpf);
        if (responsavel.isPresent() && aluno.getResponsaveis().size() > 1) {
            Query query = new Query();
            query.addCriteria(Criteria.where("rg").is(aluno.getRg()));
            Update update = new Update().pull("responsaveis", Query.query(Criteria.where("cpf").is(cpf)));
            mongoTemplate.updateFirst(query, update, Aluno.class);
            return true;
        }
        throw new MongoException("O aluno deve ter pelo menos 1 responsavel!");
    }

    @Override
    public String adicionarFaltaDoAluno(String rg, Faltas falta, Date novaFalta) {
        Aluno aluno = encontrarAlunoPorRg(rg);

        if (!aluno.getGraduacao().isStatus()) {
            throw new MongoException("O Aluno esta inativo. Pois o mesmo se encontra com mais de 5 faltas");
        }

        boolean faltasDoAluno = checarSeFaltaEstaRegistrada(aluno, falta.getData());

        if (faltasDoAluno) {
            throw new MongoException("Ja existe um registro de falta nessa data");
        }

        Faltas novaFaltaAdicionada = new Faltas(falta.getMotivo(), falta.getObservacao());

        Query query = new Query();
        query.addCriteria(Criteria.where("rg").is(aluno.getRg()));
        Update update = new Update().addToSet("graduacao.faltas", novaFaltaAdicionada);
        mongoTemplate.updateFirst(query, update, Aluno.class);
        return String.valueOf(aluno.getGraduacao().getFaltas().size() + 1);
    }

    @Override
    public String adicionarFaltaDoAluno(String rg, Faltas falta) {
        Aluno aluno = encontrarAlunoPorRg(rg);

        if (!aluno.getGraduacao().isStatus()) {
            throw new MongoException("O Aluno esta inativo. Pois o mesmo se encontra com mais de 5 faltas");
        }

        boolean faltasDoAluno = checarSeFaltaEstaRegistrada(aluno, falta.getData());

        if (faltasDoAluno) {
            throw new MongoException("Ja existe um registro de falta nessa data");
        }

        Faltas novaFalta = new Faltas(falta.getMotivo(), falta.getObservacao());

        Query query = new Query();
        query.addCriteria(Criteria.where("rg").is(aluno.getRg()));
        Update update = new Update().addToSet("graduacao.faltas", novaFalta);
        mongoTemplate.updateFirst(query, update, Aluno.class);
        return String.valueOf(aluno.getGraduacao().getFaltas().size() + 1);
    }

    @Override
    public String retirarFaltaDoAluno(String rg, String faltasId) {
        Aluno aluno = encontrarAlunoPorRg(rg);

        Faltas faltasDoAluno = encontrarFaltasDoAluno(aluno, faltasId);

        Query query = new Query();
        query.addCriteria(Criteria.where("rg").is(aluno.getRg()));
        Update update = new Update().pull("graduacao.faltas", faltasDoAluno);
        mongoTemplate.updateFirst(query, update, Aluno.class);
        return String.valueOf(aluno.getGraduacao().getFaltas().size() - 1);
    }

    protected Aluno encontrarAlunoPorRg(String rgAluno) {
        return alunoRepositorio.findByRg(rgAluno)
                .orElseThrow(() -> new MongoException("Rg: " + rgAluno + " não encontrado"));
    }

    protected Optional<Responsavel> encontrarResponsavelPorCpf(Aluno aluno, String cpf) {
        return aluno.getResponsaveis().stream()
                .filter(responsaveis -> responsaveis.getCpf().equals(cpf))
                .findFirst();
    }

    protected Faltas encontrarFaltasDoAluno(Aluno aluno, String faltasId) {
        Optional<Faltas> faltaEncontrada = aluno.getGraduacao().getFaltas().stream()
                .filter(falta -> falta.getData().equals(faltasId)).findFirst();

        return faltaEncontrada
                .orElseThrow(() -> new MongoException("Falta com id " + faltasId + " não foi encontrada."));
    }

    protected Optional<Faltas> encontrarFaltasDoAlunoPelaData(Aluno aluno, String data) {

        if (!checarSeFaltaEstaRegistrada(aluno, data)) {
            throw new MongoException("Falta" + data + "não encontrada!");
        }

        return aluno.getGraduacao().getFaltas().stream().filter(falta -> falta.getData().equals(data)).findFirst();
    }

    protected boolean checarSeFaltaEstaRegistrada(Aluno aluno, String data) {

        if (aluno.getGraduacao().getFaltas().isEmpty()) {
            return false;
        }

        return aluno.getGraduacao().getFaltas().stream().anyMatch(falta -> falta.getData().equals(data));
    }

}
