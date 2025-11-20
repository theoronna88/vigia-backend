package br.com.ronna.vigia.controller;

import br.com.ronna.vigia.dtos.MatriculaDto;
import br.com.ronna.vigia.dtos.MatriculaResponseDto;
import br.com.ronna.vigia.dtos.MatriculaStatusDto;
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

    private final MatriculaServices service;

    public MatriculasController(MatriculaServices service) {
        this.service = service;
    }

    @PostMapping()
    public ResponseEntity<MatriculaResponseDto> adicionarAlunoATurma(@RequestBody MatriculaDto matriculaDto) {
        log.info("Recebendo requisição para adicionar aluno à turma: {}", matriculaDto);
        return ResponseEntity.status(201).body(service.adicionarAlunoATurmaDto(matriculaDto));
    }

    @PatchMapping("/status")
    public ResponseEntity<MatriculaResponseDto> alterarStatusMatricula(@RequestBody MatriculaStatusDto matriculaStatusDto) {
        return ResponseEntity.ok(service.alterarStatusMatriculaDto(matriculaStatusDto));
    }

    @GetMapping("/aluno/{alunoId}")
    public ResponseEntity<List<MatriculaResponseDto>> listarMatriculasPorAluno(@PathVariable UUID alunoId) {
        return ResponseEntity.ok(service.listarMatriculasPorAlunoDto(alunoId));
    }

    @GetMapping("/turma/{turmaId}")
    public ResponseEntity<List<MatriculaResponseDto>> listarMatriculasPorTurma(@PathVariable UUID turmaId) {
        return ResponseEntity.ok(service.listarMatriculasPorTurmaDto(turmaId));
    }

    // TODO: implementar endpoint para remover registro de matrícula

    // TODO: implementar endpoint para controlar a documentação da matrícula (documentos entregues, pendentes, etc.)

    // TODO: implementar endpoint para controlar a frequência do aluno na turma (presenças, faltas, etc.)

    // TODO: implementar endpoint para controlar o desempenho do aluno na turma (notas, avaliações, etc.)

    // TODO: implementar endpoint para listar todas as matrículas com filtros (por data, por status, etc.)
}
