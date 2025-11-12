package br.com.ronna.vigia.repository;

import br.com.ronna.vigia.model.Aluno;
import br.com.ronna.vigia.model.MatriculaID;
import br.com.ronna.vigia.model.Matricula;
import br.com.ronna.vigia.model.Turma;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;
import java.util.UUID;

public interface MatriculaRepository extends ListCrudRepository<Matricula, MatriculaID> {
    List<Matricula> findByIdAluno(Aluno alunoId);
    List<Matricula> findByIdTurma(Turma turmaId);
    Matricula findByMatriculaUnica(UUID matriculaUnica);
}
