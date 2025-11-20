package br.com.ronna.vigia.services;

import br.com.ronna.vigia.dtos.*;
import br.com.ronna.vigia.model.Aluno;
import br.com.ronna.vigia.model.Matricula;
import br.com.ronna.vigia.model.Turma;

import java.util.List;
import java.util.UUID;

public interface MatriculaServices {
    public Matricula adicionarAlunoATurma(MatriculaDto matriculaDto);
    public List<Matricula> listarMatriculasPorAluno(Aluno aluno);
    public List<Matricula> listarMatriculasPorTurma(Turma turma);
    public Matricula alterarStatusMatricula(MatriculaStatusDto matriculaStatusDto);

    public MatriculaResponseDto adicionarAlunoATurmaDto(MatriculaDto matriculaDto);
    public List<MatriculaResponseDto> listarMatriculasPorAlunoDto(UUID alunoId);
    public List<MatriculaResponseDto> listarMatriculasPorTurmaDto(UUID turmaId);
    public MatriculaResponseDto alterarStatusMatriculaDto(MatriculaStatusDto matriculaStatusDto);

    // Cancelamento de matrícula
    public MatriculaResponseDto cancelarMatricula(CancelarMatriculaDto cancelarMatriculaDto);

    // Gestão de documentos
    public MatriculaDocumentoResponseDto registrarDocumento(MatriculaDocumentoDto documentoDto);
    public MatriculaDocumentoResponseDto atualizarDocumento(UUID documentoId, MatriculaDocumentoDto documentoDto);
    public List<MatriculaDocumentoResponseDto> listarDocumentosPorMatricula(UUID matriculaUnica);
    public void removerDocumento(UUID documentoId);

    // Gestão de frequência
    public MatriculaFrequenciaResponseDto registrarFrequencia(MatriculaFrequenciaDto frequenciaDto);
    public MatriculaFrequenciaResponseDto atualizarFrequencia(UUID frequenciaId, MatriculaFrequenciaDto frequenciaDto);
    public List<MatriculaFrequenciaResponseDto> listarFrequenciasPorMatricula(UUID matriculaUnica);
    public void removerFrequencia(UUID frequenciaId);
}
