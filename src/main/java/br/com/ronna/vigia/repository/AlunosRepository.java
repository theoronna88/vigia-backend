package br.com.ronna.vigia.repository;

import br.com.ronna.vigia.model.Aluno;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.Optional;

@Repository
public interface AlunosRepository extends ListCrudRepository<Aluno, UUID> {
    Optional<Aluno> findByCpf(String cpf);
    Optional<Aluno> findByEmail(String email);
    boolean existsByCpf(String cpf);
    boolean existsByEmail(String email);

    List<Aluno> findAlunoByNomeContainingOrEmailOrCpfOrRgOrTelefoneOrSexoOrEstadoCivilOrCepOrBairroOrCidadeOrOptinOrDataNascimento(String nome, String email, String cpf, String rg, String telefone, String sexo, String estadoCivil, String cep, String bairro, String cidade, Boolean optin, LocalDate dataNascimento);
}