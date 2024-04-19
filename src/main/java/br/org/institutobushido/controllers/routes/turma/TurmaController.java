package br.org.institutobushido.controllers.routes.turma;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mongodb.MongoException;

import br.org.institutobushido.controllers.dtos.turma.TurmaAlunoDTOResponse;
import br.org.institutobushido.controllers.dtos.turma.TurmaDTORequest;
import br.org.institutobushido.controllers.dtos.turma.TurmaDTOResponse;
import br.org.institutobushido.controllers.response.success.SuccessDeleteResponse;
import br.org.institutobushido.controllers.response.success.SuccessPostResponse;
import br.org.institutobushido.models.turma.Turma;
import br.org.institutobushido.services.turma.TurmaServiceInterface;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;

@RestController(value = "turma")
@RequestMapping("api/V1/turma")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class TurmaController {

    private final TurmaServiceInterface turmaService;

    public TurmaController(TurmaServiceInterface turmaService) {
        this.turmaService = turmaService;
    }

    @PostMapping("{emailAdmin}")
    public ResponseEntity<SuccessPostResponse> criarNovaTurma(@Valid() @RequestBody TurmaDTORequest turmaDTORequest,
            @PathVariable String emailAdmin) {
        String res = this.turmaService.criarNovaTurma(emailAdmin, turmaDTORequest);
        return ResponseEntity.ok().body(
                new SuccessPostResponse(turmaDTORequest.nome(), res,
                        Turma.class.getSimpleName()));
    }

    @DeleteMapping("{nomeTurma}/{emailAdmin}")
    public ResponseEntity<SuccessDeleteResponse> deletarTurma(@PathVariable String nomeTurma,
            @PathVariable String emailAdmin) {
        String res = this.turmaService.deletarTurma(emailAdmin, nomeTurma);
        return ResponseEntity.ok()
                .body(new SuccessDeleteResponse(nomeTurma, res, Turma.class.getSimpleName()));
    }

    @GetMapping
    public ResponseEntity<List<TurmaDTOResponse>> listarTurmas() {
        var turmas = this.turmaService.listarTurmas();
        return ResponseEntity.ok().body(turmas);
    }

    @GetMapping("{nomeTurma}")
    public ResponseEntity<TurmaDTOResponse> buscarTurmaPorNome(@PathVariable String nomeTurma) {
        var turma = this.turmaService.buscarTurmaPorNome(nomeTurma);
        return ResponseEntity.ok().body(turma);
    }

    @GetMapping("{nome}/alunos")
    public List<TurmaAlunoDTOResponse> listarAlunoPorTurma(@PathVariable String nome) {
        try {
            return this.turmaService.listarAlunosDaTurma(nome);
        } catch (Exception e) {
            throw new MongoException(e.getMessage());
        }

    }

}