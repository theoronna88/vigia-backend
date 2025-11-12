package br.com.ronna.vigia.services;

import br.com.ronna.vigia.dtos.MatriculaDto;
import br.com.ronna.vigia.dtos.MatriculaStatusDto;
import br.com.ronna.vigia.model.Aluno;
import br.com.ronna.vigia.model.Matricula;
import br.com.ronna.vigia.model.Turma;

import java.util.List;

public interface MatriculaServices {
    public Matricula adicionarAlunoATurma(MatriculaDto matriculaDto);
    public List<Matricula> listarMatriculasPorAluno(Aluno aluno);
    public List<Matricula> listarMatriculasPorTurma(Turma turma);
    public Matricula alterarStatusMatricula(MatriculaStatusDto matriculaStatusDto);
}
