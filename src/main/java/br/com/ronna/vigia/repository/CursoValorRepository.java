package br.com.ronna.vigia.repository;

import br.com.ronna.vigia.model.Curso;
import br.com.ronna.vigia.model.CursoValor;
import org.springframework.data.repository.ListCrudRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface CursoValorRepository extends ListCrudRepository<CursoValor, UUID> {

    Optional<CursoValor> findCursoValorsByCursoAndFimVigenciaIsNull(Curso curso);
}
