package br.org.institutobushido.controllers.routes.aluno;

import java.net.URI;
import br.org.institutobushido.controllers.dtos.aluno.graduacao.GraduacaoDTOResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import br.org.institutobushido.controllers.dtos.aluno.AlunoDTORequest;
import br.org.institutobushido.controllers.dtos.aluno.AlunoDTOResponse;
import br.org.institutobushido.controllers.dtos.aluno.UpdateAlunoDTORequest;
import br.org.institutobushido.controllers.dtos.aluno.graduacao.faltas.FaltaDTORequest;
import br.org.institutobushido.controllers.dtos.aluno.responsavel.ResponsavelDTORequest;
import br.org.institutobushido.controllers.dtos.aluno.responsavel.ResponsavelDTOResponse;
import br.org.institutobushido.controllers.response.success.SuccessDeleteResponse;
import br.org.institutobushido.controllers.response.success.SuccessPostResponse;
import br.org.institutobushido.controllers.response.success.SuccessPutResponse;
import br.org.institutobushido.model.aluno.Aluno;
import br.org.institutobushido.model.aluno.graduacao.Graduacao;
import br.org.institutobushido.model.aluno.graduacao.falta.Falta;
import br.org.institutobushido.model.aluno.historico_de_saude.HistoricoSaude;
import br.org.institutobushido.model.aluno.responsaveis.Responsavel;
import br.org.institutobushido.services.aluno.AlunoServicesInterface;
import jakarta.validation.Valid;

@RestController(value = "aluno")
@RequestMapping("api/V1/aluno")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AlunoControllers {

        private final AlunoServicesInterface alunoServices;

        public AlunoControllers(AlunoServicesInterface alunoServices) {
                this.alunoServices = alunoServices;
        }

        @GetMapping()
        ResponseEntity<AlunoDTOResponse> buscarAlunoPorRg(@RequestParam(name = "rg") String rg) {
                AlunoDTOResponse alunoEncontrado = alunoServices.buscarAluno(rg);
                return ResponseEntity.ok().body(alunoEncontrado);
        }

        @PostMapping()
        ResponseEntity<SuccessPostResponse> adicionarAluno(@Valid() @RequestBody AlunoDTORequest alunoDTORequest) {
                AlunoDTOResponse novoAluno = alunoServices.adicionarAluno(alunoDTORequest);
                URI location = ServletUriComponentsBuilder.fromCurrentRequest().build().toUri();
                return ResponseEntity.created(location)
                                .body(new SuccessPostResponse(novoAluno.rg(), "Aluno adicionado com sucesso",
                                                Aluno.class.getSimpleName()));
        }

        @PutMapping("{id}")
        public ResponseEntity<SuccessPutResponse> editarAluno(@PathVariable String id,
                        @RequestBody UpdateAlunoDTORequest aluno) {
                String alunoEditado = this.alunoServices.editarAlunoPorRg(id, aluno);
                return ResponseEntity.ok().body(
                                new SuccessPutResponse(id, alunoEditado, Aluno.class.getSimpleName()));
        }

        @PostMapping("responsavel/{rg}")
        public ResponseEntity<SuccessPostResponse> adicionarResponsavel(@PathVariable String rg,
                        @Valid @RequestBody ResponsavelDTORequest responsavelDTORequest) {
                ResponsavelDTOResponse responsavel = alunoServices.adicionarResponsavel(rg, responsavelDTORequest);
                return ResponseEntity.ok().body(new SuccessPostResponse(responsavel.cpf(),
                                "Responsável adicionado com sucesso", Responsavel.class.getSimpleName()));
        }

        @DeleteMapping("responsavel/{rg}")
        public ResponseEntity<SuccessDeleteResponse> removerResponsavel(@PathVariable String rg,
                        @RequestParam(name = "cpf") String cpf) {
                String res = alunoServices.removerResponsavel(rg, cpf);
                return ResponseEntity.ok().body(
                                new SuccessDeleteResponse(res, "Responsável removido com sucesso",
                                                Responsavel.class.getSimpleName()));
        }

        @PostMapping("falta/{rg}/{data}")
        public ResponseEntity<SuccessPostResponse> adicionarFaltaAoAluno(@Valid @RequestBody FaltaDTORequest faltas,
                        @PathVariable String rg, @PathVariable long data) {
                String res = alunoServices.adicionarFaltaDoAluno(rg, faltas, data);
                return ResponseEntity.ok()
                                .body(new SuccessPostResponse(res, "Falta adicionada", Falta.class.getSimpleName()));
        }

        @DeleteMapping("falta/{rg}")
        public ResponseEntity<SuccessDeleteResponse> retirarFaltaAoAluno(@RequestParam(name = "data") String data,
                        @PathVariable String rg) {
                String res = alunoServices.retirarFaltaDoAluno(rg, data);
                return ResponseEntity.ok()
                                .body(new SuccessDeleteResponse(res, "Falta retirada com sucesso",
                                                Falta.class.getSimpleName()));
        }

        @PostMapping("deficiencia/{rg}")
        public ResponseEntity<SuccessPostResponse> adicionarDeficiencia(@PathVariable String rg,
                        @RequestParam(name = "deficiencia") String deficiencia) {
                String res = alunoServices.adicionarDeficiencia(rg, deficiencia);
                return ResponseEntity.ok().body(new SuccessPostResponse(res, "Deficiência adicionada",
                                HistoricoSaude.class.getSimpleName()));
        }

        @DeleteMapping("deficiencia/{rg}")
        public ResponseEntity<SuccessDeleteResponse> removerDeficiencia(@PathVariable String rg,
                        @RequestParam(name = "deficiencia") String deficiencia) {
                String res = alunoServices.removerDeficiencia(rg, deficiencia);
                return ResponseEntity.ok()
                                .body(new SuccessDeleteResponse(res,
                                                "Deficiência " + deficiencia + " foi removida com sucesso.",
                                                HistoricoSaude.class.getSimpleName()));
        }

        @PostMapping("acompanhamentoSaude/{rg}")
        public ResponseEntity<SuccessPostResponse> adicionarAcompanhamentoSaude(@PathVariable String rg,
                        @RequestParam(name = "acompanhamento") String acompanhamento) {
                String res = alunoServices.adicionarAcompanhamentoSaude(rg, acompanhamento);
                return ResponseEntity.ok()
                                .body(new SuccessPostResponse(res,
                                                "Acompanhamento " + acompanhamento + " foi adicionado com sucesso.",
                                                HistoricoSaude.class.getSimpleName()));
        }

        @PostMapping("graduacao/{rg}/aprovar")
        public ResponseEntity<SuccessPostResponse> aprovarAluno(@PathVariable String rg) {
                GraduacaoDTOResponse res = alunoServices.aprovarAluno(rg);
                return ResponseEntity.ok()
                                .body(new SuccessPostResponse(String.valueOf(res.kyu()),
                                                "Graduação concluída com sucesso.", Graduacao.class.getSimpleName()));
        }

        @PostMapping("graduacao/{rg}/reprovar")
        public ResponseEntity<SuccessPostResponse> reprovarAluno(@PathVariable String rg) {
                GraduacaoDTOResponse res = alunoServices.reprovarAluno(rg);
                return ResponseEntity.ok()
                                .body(new SuccessPostResponse(String.valueOf(res.kyu()),
                                                "Graduação concluída com sucesso.", Graduacao.class.getSimpleName()));
        }

        @DeleteMapping("acompanhamentoSaude/{rg}")
        public ResponseEntity<SuccessDeleteResponse> removerAcompanhamentoSaude(@PathVariable String rg,
                        @RequestParam(name = "acompanhamento") String acompanhamento) {
                String res = alunoServices.removerAcompanhamentoSaude(rg, acompanhamento);
                return ResponseEntity.ok().body(
                                new SuccessDeleteResponse(res,
                                                "Acamponhamento " + acompanhamento + " foi removido com sucesso."));
        }
}
