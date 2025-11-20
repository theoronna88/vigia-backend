package br.com.ronna.vigia.service;

import br.com.ronna.vigia.dtos.MatriculaDto;
import br.com.ronna.vigia.enums.MatriculaStatus;
import br.com.ronna.vigia.enums.TurmaStatus;
import br.com.ronna.vigia.exceptions.AlreadyExistsException;
import br.com.ronna.vigia.exceptions.MaxStudentsException;
import br.com.ronna.vigia.model.*;
import br.com.ronna.vigia.repository.*;
import br.com.ronna.vigia.services.Impl.MatriculaServicesImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para MatriculaServicesImpl - Prioridade 1
 * Foca nas validações críticas de regras de negócio
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("MatriculaServicesImpl - Testes Críticos de Validação")
class MatriculaServicesImplTest {

    @Mock
    private MatriculaRepository matriculaRepository;

    @Mock
    private AlunosRepository alunosRepository;

    @Mock
    private TurmasRepository turmasRepository;

    @Mock
    private MatriculaDocumentoRepository documentoRepository;

    @Mock
    private MatriculaFrequenciaRepository frequenciaRepository;

    @InjectMocks
    private MatriculaServicesImpl matriculaServices;

    private Aluno aluno;
    private Turma turma;
    private MatriculaDto matriculaDto;
    private Curso curso;

    @BeforeEach
    void setUp() {
        // Configurar dados de teste realistas
        curso = new Curso();
        curso.setId(UUID.randomUUID());
        curso.setNome("Curso de Java Avançado");
        curso.setDeleted(false);

        aluno = new Aluno();
        aluno.setId(UUID.randomUUID());
        aluno.setNome("João Silva");
        aluno.setCpf("123.456.789-00");
        aluno.setEmail("joao.silva@email.com");
        aluno.setDeleted(false);

        turma = new Turma();
        turma.setId(UUID.randomUUID());
        turma.setNome("Turma A - Manhã");
        turma.setCurso(curso);
        turma.setTurno("08:00 - 12:00");
        turma.setCapacidadeMaxima(30);
        turma.setNumeroMatriculados(0);
        turma.setStatus(TurmaStatus.ABERTA);
        turma.setDataInicio(LocalDate.now().plusDays(7));
        turma.setDataTermino(LocalDate.now().plusMonths(6));
        turma.setDeleted(false);

        matriculaDto = new MatriculaDto();
        matriculaDto.setAlunoId(aluno.getId());
        matriculaDto.setTurmaId(turma.getId());
        matriculaDto.setDataMatricula(LocalDate.now());
    }

    /**
     * Teste 1: Validação de Capacidade Máxima
     * Cenário: Tentar matricular aluno quando numeroMatriculados >= capacidadeMaxima
     * Esperado: Lançar MaxStudentsException
     * Lógica: MatriculaServicesImpl linha 72-74
     */
    @Test
    @DisplayName("Deve lançar MaxStudentsException quando turma atingir capacidade máxima")
    void shouldThrowMaxStudentsException_WhenTurmaIsFull() {
        // Arrange - Configurar turma lotada
        turma.setNumeroMatriculados(30); // Turma com 30 alunos (capacidade máxima)
        turma.setCapacidadeMaxima(30);

        when(alunosRepository.findById(aluno.getId())).thenReturn(Optional.of(aluno));
        when(turmasRepository.findById(turma.getId())).thenReturn(Optional.of(turma));
        when(matriculaRepository.findById(any(MatriculaID.class))).thenReturn(Optional.empty());

        // Act & Assert
        MaxStudentsException exception = assertThrows(
            MaxStudentsException.class,
            () -> matriculaServices.adicionarAlunoATurma(matriculaDto),
            "Deveria lançar MaxStudentsException quando a turma está lotada"
        );

        assertEquals("Erro: A turma atingiu o limite máximo de alunos.", exception.getMessage());
        verify(matriculaRepository, never()).save(any(Matricula.class));
    }

    /**
     * Teste 2: Validação de Conflito de Horários
     * Cenário: Tentar matricular aluno em turma com mesmo turno de outra matrícula ativa
     * Esperado: Lançar AlreadyExistsException
     * Lógica: MatriculaServicesImpl linha 77-83
     */
    @Test
    @DisplayName("Deve lançar AlreadyExistsException quando há conflito de horários")
    void shouldThrowAlreadyExistsException_WhenScheduleConflict() {
        // Arrange - Configurar matrícula existente em turma com mesmo horário
        Turma outraTurma = new Turma();
        outraTurma.setId(UUID.randomUUID());
        outraTurma.setNome("Turma B - Manhã");
        outraTurma.setCurso(curso);
        outraTurma.setTurno("08:00 - 12:00"); // Mesmo horário da turma principal
        outraTurma.setCapacidadeMaxima(30);
        outraTurma.setNumeroMatriculados(15);
        outraTurma.setStatus(TurmaStatus.ABERTA);
        outraTurma.setDeleted(false);

        // Criar matrícula existente
        MatriculaID matriculaIdExistente = new MatriculaID();
        matriculaIdExistente.setAluno(aluno);
        matriculaIdExistente.setTurma(outraTurma);

        Matricula matriculaExistente = new Matricula();
        matriculaExistente.setId(matriculaIdExistente);
        matriculaExistente.setStatus(MatriculaStatus.ATIVA);
        matriculaExistente.setDataMatricula(LocalDate.now().minusDays(30));

        List<Matricula> matriculasAtuais = new ArrayList<>();
        matriculasAtuais.add(matriculaExistente);

        when(alunosRepository.findById(aluno.getId())).thenReturn(Optional.of(aluno));
        when(turmasRepository.findById(turma.getId())).thenReturn(Optional.of(turma));
        when(matriculaRepository.findById(any(MatriculaID.class))).thenReturn(Optional.empty());
        when(matriculaRepository.findByIdAluno(aluno)).thenReturn(matriculasAtuais);

        // Act & Assert
        AlreadyExistsException exception = assertThrows(
            AlreadyExistsException.class,
            () -> matriculaServices.adicionarAlunoATurma(matriculaDto),
            "Deveria lançar AlreadyExistsException quando há conflito de horários"
        );

        assertTrue(exception.getMessage().contains("mesmo horário"));
        assertTrue(exception.getMessage().contains("08:00 - 12:00"));
        verify(matriculaRepository, never()).save(any(Matricula.class));
    }

    /**
     * Teste 3: Validação de Aluno Duplicado
     * Cenário: Tentar matricular aluno já matriculado na mesma turma
     * Esperado: Lançar AlreadyExistsException
     * Lógica: MatriculaServicesImpl linha 62-64
     */
    @Test
    @DisplayName("Deve lançar AlreadyExistsException quando aluno já está matriculado na turma")
    void shouldThrowAlreadyExistsException_WhenAlunoAlreadyEnrolled() {
        // Arrange - Configurar matrícula existente na mesma turma
        MatriculaID matriculaIdExistente = new MatriculaID();
        matriculaIdExistente.setAluno(aluno);
        matriculaIdExistente.setTurma(turma);

        Matricula matriculaExistente = new Matricula();
        matriculaExistente.setId(matriculaIdExistente);
        matriculaExistente.setStatus(MatriculaStatus.ATIVA);
        matriculaExistente.setMatriculaUnica(UUID.randomUUID());

        when(alunosRepository.findById(aluno.getId())).thenReturn(Optional.of(aluno));
        when(turmasRepository.findById(turma.getId())).thenReturn(Optional.of(turma));
        when(matriculaRepository.findById(any(MatriculaID.class))).thenReturn(Optional.of(matriculaExistente));

        // Act & Assert
        AlreadyExistsException exception = assertThrows(
            AlreadyExistsException.class,
            () -> matriculaServices.adicionarAlunoATurma(matriculaDto),
            "Deveria lançar AlreadyExistsException quando aluno já está matriculado"
        );

        assertEquals("Erro: O aluno já está matriculado nesta turma.", exception.getMessage());
        verify(matriculaRepository, never()).save(any(Matricula.class));
        verify(matriculaRepository, times(1)).findById(any(MatriculaID.class));
    }

    /**
     * Teste Bonus: Validação de matrícula com sucesso
     * Garante que o fluxo normal de matrícula funciona quando não há problemas
     */
    @Test
    @DisplayName("Deve matricular aluno com sucesso quando todas as validações passam")
    void shouldEnrollStudentSuccessfully_WhenAllValidationsPass() {
        // Arrange
        when(alunosRepository.findById(aluno.getId())).thenReturn(Optional.of(aluno));
        when(turmasRepository.findById(turma.getId())).thenReturn(Optional.of(turma));
        when(matriculaRepository.findById(any(MatriculaID.class))).thenReturn(Optional.empty());
        when(matriculaRepository.findByIdAluno(aluno)).thenReturn(new ArrayList<>());
        when(matriculaRepository.save(any(Matricula.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Matricula resultado = matriculaServices.adicionarAlunoATurma(matriculaDto);

        // Assert
        assertNotNull(resultado);
        assertEquals(MatriculaStatus.PENDENTE, resultado.getStatus());
        assertNotNull(resultado.getMatriculaUnica());
        verify(matriculaRepository, times(1)).save(any(Matricula.class));
    }
}
