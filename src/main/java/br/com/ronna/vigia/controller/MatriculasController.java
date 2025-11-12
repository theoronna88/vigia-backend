package br.com.ronna.vigia.controller;

import br.com.ronna.vigia.dtos.MatriculaDto;
import br.com.ronna.vigia.dtos.MatriculaStatusDto;
import br.com.ronna.vigia.model.Aluno;
import br.com.ronna.vigia.model.Matricula;
import br.com.ronna.vigia.model.Turma;
import br.com.ronna.vigia.services.MatriculaServices;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/matriculas")
@CrossOrigin(origins = "*")
@Log4j2
public class MatriculasController {

    // TODO: refatorar as exceptions para uma classe global de tratamento de erros

    private MatriculaServices service;

    public MatriculasController(MatriculaServices service) {
        this.service = service;
    }

    @PostMapping()
    public ResponseEntity<Matricula> adicionarAlunoATurma(@RequestBody MatriculaDto matriculaDto) {
        log.info("Recebendo requisição para adicionar aluno à turma: {}", matriculaDto);
        return ResponseEntity.status(201).body(service.adicionarAlunoATurma(matriculaDto));
    }

    @PatchMapping("/status")
    public ResponseEntity<Matricula> alterarStatusMatricula(@RequestBody MatriculaStatusDto matriculaStatusDto) {
        return ResponseEntity.ok(service.alterarStatusMatricula(matriculaStatusDto));
    }

    @GetMapping("/aluno/{alunoId}")
    public ResponseEntity<List<Matricula>> listarMatriculasPorAluno(@PathVariable UUID alunoId) {
        Aluno aluno = new Aluno();
        aluno.setId(alunoId);
        return ResponseEntity.ok(service.listarMatriculasPorAluno(aluno));
    }

    @GetMapping("/turma/{turmaId}")
    public ResponseEntity<List<Matricula>> listarMatriculasPorTurma(@PathVariable UUID turmaId) {
        Turma turma = new Turma();
        turma.setId(turmaId);
        return ResponseEntity.ok(service.listarMatriculasPorTurma(turma));
    }

    // TODO: implementar endpoint para remover registro de matrícula

    // TODO: implementar endpoint para controlar a documentação da matrícula (documentos entregues, pendentes, etc.)

    // TODO: implementar endpoint para controlar a frequência do aluno na turma (presenças, faltas, etc.)

    // TODO: implementar endpoint para controlar o desempenho do aluno na turma (notas, avaliações, etc.)

    // TODO: implementar endpoint para listar todas as matrículas com filtros (por data, por status, etc.)
}
