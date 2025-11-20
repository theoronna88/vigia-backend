package br.com.ronna.vigia.controller;

import br.com.ronna.vigia.dtos.TurmasDto;
import br.com.ronna.vigia.enums.MatriculaStatus;
import br.com.ronna.vigia.enums.TurmaStatus;
import br.com.ronna.vigia.model.*;
import br.com.ronna.vigia.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes de integração para TurmasController - Prioridade 1
 * Foca nas validações críticas de deleção de turmas
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("TurmasController - Testes de Integração")
class TurmasControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TurmasRepository turmasRepository;

    @Autowired
    private CursosRepository cursosRepository;

    @Autowired
    private AlunosRepository alunosRepository;

    @Autowired
    private MatriculaRepository matriculaRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Curso curso;
    private Turma turma;
    private Aluno aluno;

    @BeforeEach
    void setUp() {
        // Limpar dados antes de cada teste
        matriculaRepository.deleteAll();
        turmasRepository.deleteAll();
        alunosRepository.deleteAll();
        cursosRepository.deleteAll();

        // Criar curso
        curso = new Curso();
        curso.setNome("Curso de Teste");
        curso.setDescricao("Descrição do curso de teste");
        curso.setCargaHoraria(40.0);
        curso.setDeleted(false);
        curso.setDataCriacao(LocalDateTime.now());
        curso.setDataAtualizacao(LocalDateTime.now());
        curso = cursosRepository.save(curso);

        // Criar turma
        turma = new Turma();
        turma.setNome("Turma Teste");
        turma.setCurso(curso);
        turma.setTurno("08:00 - 12:00");
        turma.setCapacidadeMaxima(30);
        turma.setNumeroMatriculados(0);
        turma.setStatus(TurmaStatus.ABERTA);
        turma.setDataInicio(LocalDate.now().plusDays(7));
        turma.setDataTermino(LocalDate.now().plusMonths(6));
        turma.setDeleted(false);
        turma.setInformadoInicio(false);
        turma.setInformadoTermino(false);
        turma.setDataCriacao(LocalDate.now());
        turma.setDataAtualizacao(LocalDate.now());
        turma = turmasRepository.save(turma);

        // Criar aluno
        aluno = new Aluno();
        aluno.setNome("Aluno Teste");
        aluno.setCpf("123.456.789-00");
        aluno.setRg("12.345.678-9");
        aluno.setOrgaoEmissor("SSP");
        aluno.setNacionalidade("Brasileira");
        aluno.setNaturalidade("São Paulo");
        aluno.setNomeMae("Mãe Teste");
        aluno.setNomePai("Pai Teste");
        aluno.setSexo("Masculino");
        aluno.setEstadoCivil("Solteiro");
        aluno.setEscolaridade("Ensino Médio");
        aluno.setProfissao("Estudante");
        aluno.setCep("01234-567");
        aluno.setEndereco("Rua Teste");
        aluno.setNumero("123");
        aluno.setComplemento("Apto 1");
        aluno.setBairro("Bairro Teste");
        aluno.setCidade("São Paulo");
        aluno.setEstado("SP");
        aluno.setTelefone("(11) 98765-4321");
        aluno.setEmail("aluno.teste@email.com");
        aluno.setDataNascimento(LocalDate.of(2000, 1, 1));
        aluno.setDataCriacao(LocalDateTime.now());
        aluno.setDataAtualizacao(LocalDateTime.now());
        aluno.setOptin(true);
        aluno.setDeleted(false);
        aluno = alunosRepository.save(aluno);
    }

    /**
     * Teste 5: Deleção de Turma com Alunos
     * Cenário: Tentar deletar turma que possui matrículas ativas
     * Esperado: 409 Conflict
     */
    @Test
    @DisplayName("Deve retornar 409 Conflict ao tentar deletar turma com matrículas ativas")
    void shouldReturn409_WhenDeletingTurmaWithActiveEnrollments() throws Exception {
        // Arrange - Criar matrícula ativa na turma
        MatriculaID matriculaId = new MatriculaID();
        matriculaId.setAluno(aluno);
        matriculaId.setTurma(turma);

        Matricula matricula = new Matricula();
        matricula.setId(matriculaId);
        matricula.setMatriculaUnica(UUID.randomUUID());
        matricula.setDataMatricula(LocalDate.now());
        matricula.setStatus(MatriculaStatus.ATIVA);
        matricula.setDataCriacao(LocalDateTime.now());
        matriculaRepository.save(matricula);

        // Act & Assert - Tentar deletar turma
        mockMvc.perform(delete("/api/turmas/{id}", turma.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message").value(containsString("Não é possível deletar a turma")))
                .andExpect(jsonPath("$.message").value(containsString("matrícula(s) ativa(s)")));

        // Verificar que a turma não foi deletada
        Turma turmaVerificacao = turmasRepository.findById(turma.getId()).orElse(null);
        assert turmaVerificacao != null;
        assert !turmaVerificacao.isDeleted();
        assert turmaVerificacao.getStatus() == TurmaStatus.ABERTA;
    }

    /**
     * Teste Bonus: Validação de deleção com sucesso quando não há matrículas
     */
    @Test
    @DisplayName("Deve deletar turma com sucesso quando não há matrículas ativas")
    void shouldDeleteTurmaSuccessfully_WhenNoActiveEnrollments() throws Exception {
        // Act & Assert - Deletar turma sem matrículas
        mockMvc.perform(delete("/api/turmas/{id}", turma.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        // Verificar que a turma foi marcada como deletada
        Turma turmaVerificacao = turmasRepository.findById(turma.getId()).orElse(null);
        assert turmaVerificacao != null;
        assert turmaVerificacao.isDeleted();
        assert turmaVerificacao.getStatus() == TurmaStatus.CANCELADA;
    }

    /**
     * Teste Bonus: Validação de múltiplas matrículas
     */
    @Test
    @DisplayName("Deve retornar 409 Conflict com contagem correta de matrículas")
    void shouldReturn409WithCorrectEnrollmentCount_WhenMultipleEnrollments() throws Exception {
        // Arrange - Criar múltiplas matrículas
        MatriculaID matriculaId1 = new MatriculaID();
        matriculaId1.setAluno(aluno);
        matriculaId1.setTurma(turma);

        Matricula matricula1 = new Matricula();
        matricula1.setId(matriculaId1);
        matricula1.setMatriculaUnica(UUID.randomUUID());
        matricula1.setDataMatricula(LocalDate.now());
        matricula1.setStatus(MatriculaStatus.ATIVA);
        matricula1.setDataCriacao(LocalDateTime.now());
        matriculaRepository.save(matricula1);

        // Criar segundo aluno
        Aluno aluno2 = new Aluno();
        aluno2.setNome("Aluno Teste 2");
        aluno2.setCpf("987.654.321-00");
        aluno2.setRg("98.765.432-1");
        aluno2.setOrgaoEmissor("SSP");
        aluno2.setNacionalidade("Brasileira");
        aluno2.setNaturalidade("Rio de Janeiro");
        aluno2.setNomeMae("Mãe Teste 2");
        aluno2.setNomePai("Pai Teste 2");
        aluno2.setSexo("Feminino");
        aluno2.setEstadoCivil("Solteira");
        aluno2.setEscolaridade("Ensino Médio");
        aluno2.setProfissao("Estudante");
        aluno2.setCep("98765-432");
        aluno2.setEndereco("Rua Teste 2");
        aluno2.setNumero("456");
        aluno2.setComplemento("Apto 2");
        aluno2.setBairro("Bairro Teste 2");
        aluno2.setCidade("Rio de Janeiro");
        aluno2.setEstado("RJ");
        aluno2.setTelefone("(21) 91234-5678");
        aluno2.setEmail("aluno.teste2@email.com");
        aluno2.setDataNascimento(LocalDate.of(2001, 5, 15));
        aluno2.setDataCriacao(LocalDateTime.now());
        aluno2.setDataAtualizacao(LocalDateTime.now());
        aluno2.setOptin(true);
        aluno2.setDeleted(false);
        aluno2 = alunosRepository.save(aluno2);

        // Criar segunda matrícula
        MatriculaID matriculaId2 = new MatriculaID();
        matriculaId2.setAluno(aluno2);
        matriculaId2.setTurma(turma);

        Matricula matricula2 = new Matricula();
        matricula2.setId(matriculaId2);
        matricula2.setMatriculaUnica(UUID.randomUUID());
        matricula2.setDataMatricula(LocalDate.now());
        matricula2.setStatus(MatriculaStatus.PENDENTE);
        matricula2.setDataCriacao(LocalDateTime.now());
        matriculaRepository.save(matricula2);

        // Act & Assert - Tentar deletar turma com 2 matrículas
        mockMvc.perform(delete("/api/turmas/{id}", turma.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value(containsString("2 matrícula(s) ativa(s)")));
    }

    /**
     * Teste Bonus: Validação de turma não encontrada
     */
    @Test
    @DisplayName("Deve retornar 404 Not Found ao tentar deletar turma inexistente")
    void shouldReturn404_WhenDeletingNonexistentTurma() throws Exception {
        // Arrange - UUID aleatório que não existe
        UUID turmaIdInexistente = UUID.randomUUID();

        // Act & Assert
        mockMvc.perform(delete("/api/turmas/{id}", turmaIdInexistente)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"));
    }
}
