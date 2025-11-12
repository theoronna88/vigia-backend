package br.com.ronna.vigia.repository;

import br.com.ronna.vigia.model.Curso;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CursosRepository extends ListCrudRepository<Curso, UUID> {
}
