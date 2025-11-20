package br.com.ronna.vigia.repository;

import br.com.ronna.vigia.enums.TipoPresenca;
import br.com.ronna.vigia.model.Matricula;
import br.com.ronna.vigia.model.MatriculaFrequencia;
import org.springframework.data.repository.ListCrudRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MatriculaFrequenciaRepository extends ListCrudRepository<MatriculaFrequencia, UUID> {

    List<MatriculaFrequencia> findByMatricula(Matricula matricula);

    List<MatriculaFrequencia> findByMatriculaAndPresenca(Matricula matricula, TipoPresenca presenca);

    Optional<MatriculaFrequencia> findByMatriculaAndDataAula(Matricula matricula, LocalDate dataAula);
}
