package br.com.ronna.vigia.controller;

import br.com.ronna.vigia.dtos.TurmasDto;
import br.com.ronna.vigia.enums.TurmaStatus;
import br.com.ronna.vigia.services.TurmasServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/turmas")
@CrossOrigin(origins = "*")
public class TurmasController {

    private TurmasServices turmasServices;

    public TurmasController(TurmasServices turmasServices) {
        this.turmasServices = turmasServices;
    }

    @GetMapping
    public ResponseEntity<List<TurmasDto>> listarTodas() {
        List<TurmasDto> turmas = turmasServices.listarTodas();
        return ResponseEntity.status(HttpStatus.OK).body(turmas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> buscarPorId(@PathVariable UUID id) {
        var turma = turmasServices.buscarPorId(id);
        if (!turma.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Erro: Turma não encontrada.");
        }
        return ResponseEntity.status(HttpStatus.OK).body(turma);
    }



    @PostMapping
    public ResponseEntity<TurmasDto> criar(@RequestBody TurmasDto turmasDto) {
        return turmasServices.salvar(turmasDto)
                .map(turma -> ResponseEntity.status(HttpStatus.CREATED).body(turma))
                .orElse(ResponseEntity.badRequest().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<TurmasDto> atualizar(@PathVariable UUID id, @RequestBody TurmasDto turmasDto) {
        return turmasServices.atualizar(id, turmasDto)
                .map(turma -> ResponseEntity.ok(turma))
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Object> atualizarStatusTurma(
            @PathVariable UUID id,
            @RequestParam TurmaStatus status
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(turmasServices.atualizarStatus(id, status));
    }

    @PatchMapping("/{turmaId}/aluno/{alunoId}/lead")
    public ResponseEntity<Object> atualizarExibicaoLead(
            @PathVariable UUID turmaId,
            @PathVariable UUID alunoId,
            @RequestParam boolean exibirNoLead
    ){
        if (turmasServices.atualizarExibicaoNoLead(turmaId, alunoId, exibirNoLead)) {
            return ResponseEntity.status(HttpStatus.OK).body("Exibição no lead atualizada com sucesso.");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Erro: Turma ou aluno não encontrado.");
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        if (turmasServices.deletar(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
