package br.com.ronna.vigia.controller;

import br.com.ronna.vigia.dtos.*;
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

    @DeleteMapping("/{matriculaUnica}/cancelar")
    public ResponseEntity<MatriculaResponseDto> cancelarMatricula(
            @PathVariable UUID matriculaUnica,
            @RequestBody CancelarMatriculaDto cancelarMatriculaDto) {
        log.info("Recebendo requisição para cancelar matrícula: {}", matriculaUnica);
        cancelarMatriculaDto.setMatriculaUnica(matriculaUnica);
        return ResponseEntity.ok(service.cancelarMatricula(cancelarMatriculaDto));
    }

    @PostMapping("/{matriculaUnica}/documentos")
    public ResponseEntity<MatriculaDocumentoResponseDto> registrarDocumento(
            @PathVariable UUID matriculaUnica,
            @RequestBody MatriculaDocumentoDto documentoDto) {
        log.info("Registrando documento para matrícula: {}", matriculaUnica);
        documentoDto.setMatriculaUnica(matriculaUnica);
        return ResponseEntity.status(201).body(service.registrarDocumento(documentoDto));
    }

    @PutMapping("/documentos/{documentoId}")
    public ResponseEntity<MatriculaDocumentoResponseDto> atualizarDocumento(
            @PathVariable UUID documentoId,
            @RequestBody MatriculaDocumentoDto documentoDto) {
        log.info("Atualizando documento: {}", documentoId);
        return ResponseEntity.ok(service.atualizarDocumento(documentoId, documentoDto));
    }

    @GetMapping("/{matriculaUnica}/documentos")
    public ResponseEntity<List<MatriculaDocumentoResponseDto>> listarDocumentosPorMatricula(
            @PathVariable UUID matriculaUnica) {
        return ResponseEntity.ok(service.listarDocumentosPorMatricula(matriculaUnica));
    }

    @DeleteMapping("/documentos/{documentoId}")
    public ResponseEntity<Void> removerDocumento(@PathVariable UUID documentoId) {
        log.info("Removendo documento: {}", documentoId);
        service.removerDocumento(documentoId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{matriculaUnica}/frequencias")
    public ResponseEntity<MatriculaFrequenciaResponseDto> registrarFrequencia(
            @PathVariable UUID matriculaUnica,
            @RequestBody MatriculaFrequenciaDto frequenciaDto) {
        log.info("Registrando frequência para matrícula: {}", matriculaUnica);
        frequenciaDto.setMatriculaUnica(matriculaUnica);
        return ResponseEntity.status(201).body(service.registrarFrequencia(frequenciaDto));
    }

    @PutMapping("/frequencias/{frequenciaId}")
    public ResponseEntity<MatriculaFrequenciaResponseDto> atualizarFrequencia(
            @PathVariable UUID frequenciaId,
            @RequestBody MatriculaFrequenciaDto frequenciaDto) {
        log.info("Atualizando frequência: {}", frequenciaId);
        return ResponseEntity.ok(service.atualizarFrequencia(frequenciaId, frequenciaDto));
    }

    @GetMapping("/{matriculaUnica}/frequencias")
    public ResponseEntity<List<MatriculaFrequenciaResponseDto>> listarFrequenciasPorMatricula(
            @PathVariable UUID matriculaUnica) {
        return ResponseEntity.ok(service.listarFrequenciasPorMatricula(matriculaUnica));
    }

    @DeleteMapping("/frequencias/{frequenciaId}")
    public ResponseEntity<Void> removerFrequencia(@PathVariable UUID frequenciaId) {
        log.info("Removendo frequência: {}", frequenciaId);
        service.removerFrequencia(frequenciaId);
        return ResponseEntity.noContent().build();
    }

    // TODO: implementar endpoint para controlar o desempenho do aluno na turma (notas, avaliações, etc.)
}
