package br.com.ronna.vigia.repository;

import br.com.ronna.vigia.model.Aluno;
import br.com.ronna.vigia.model.Turma;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Repository
public interface TurmasRepository extends ListCrudRepository<Turma, UUID> {

    List<Turma> findByDeletedFalse();

    @Query("SELECT t FROM Turma t WHERE t.curso.id = :cursoId AND t.deleted = false")
    List<Turma> findByCursoIdAndDeletedFalse(@Param("cursoId") UUID cursoId);

    Collection<Turma> findByCursoIdAndDeletedFalseAndDataTerminoBefore(UUID cursoId, LocalDate parse);
}
