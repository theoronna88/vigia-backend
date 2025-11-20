package br.com.ronna.vigia.repository;

import br.com.ronna.vigia.enums.StatusDocumento;
import br.com.ronna.vigia.model.Matricula;
import br.com.ronna.vigia.model.MatriculaDocumento;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;
import java.util.UUID;

public interface MatriculaDocumentoRepository extends ListCrudRepository<MatriculaDocumento, UUID> {

    List<MatriculaDocumento> findByMatricula(Matricula matricula);

    List<MatriculaDocumento> findByMatriculaAndStatusDocumento(Matricula matricula, StatusDocumento status);
}
