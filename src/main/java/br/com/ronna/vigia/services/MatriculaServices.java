package br.com.ronna.vigia.services;

import br.com.ronna.vigia.dtos.MatriculaDto;
import br.com.ronna.vigia.dtos.MatriculaResponseDto;
import br.com.ronna.vigia.dtos.MatriculaStatusDto;
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
}
