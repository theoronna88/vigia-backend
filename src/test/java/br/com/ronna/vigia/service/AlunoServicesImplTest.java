package br.com.ronna.vigia.service;

import br.com.ronna.vigia.dtos.AlunosDto;
import br.com.ronna.vigia.exceptions.AlreadyExistsException;
import br.com.ronna.vigia.model.Aluno;
import br.com.ronna.vigia.repository.AlunosRepository;
import br.com.ronna.vigia.services.Impl.AlunoServicesImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para AlunoServicesImpl - Prioridade 1
 * Foca nas validações críticas de duplicação de dados
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AlunoServicesImpl - Testes Críticos de Validação")
class AlunoServicesImplTest {

    @Mock
    private AlunosRepository alunosRepository;

    @InjectMocks
    private AlunoServicesImpl alunoServices;

    private AlunosDto alunoDto;

    @BeforeEach
    void setUp() {
        // Configurar dados de teste realistas
        alunoDto = new AlunosDto();
        alunoDto.setNome("Maria Santos");
        alunoDto.setCpf("987.654.321-00");
        alunoDto.setRg("12.345.678-9");
        alunoDto.setOrgaoEmissor("SSP");
        alunoDto.setNacionalidade("Brasileira");
        alunoDto.setNaturalidade("São Paulo");
        alunoDto.setNomeMae("Ana Santos");
        alunoDto.setNomePai("José Santos");
        alunoDto.setSexo("Feminino");
        alunoDto.setEstadoCivil("Solteira");
        alunoDto.setEscolaridade("Ensino Médio Completo");
        alunoDto.setProfissao("Estudante");
        alunoDto.setCep("01234-567");
        alunoDto.setEndereco("Rua das Flores");
        alunoDto.setNumero("123");
        alunoDto.setComplemento("Apto 45");
        alunoDto.setBairro("Centro");
        alunoDto.setCidade("São Paulo");
        alunoDto.setEstado("SP");
        alunoDto.setTelefone("(11) 98765-4321");
        alunoDto.setEmail("maria.santos@email.com");
        alunoDto.setDataNascimento(LocalDate.of(2000, 5, 15));
        alunoDto.setOptin(true);
    }

    /**
     * Teste 4: Validação de CPF Duplicado
     * Cenário: Tentar criar aluno com CPF já existente
     * Esperado: Lançar AlreadyExistsException
     * Lógica: AlunoServicesImpl linha 52-54
     */
    @Test
    @DisplayName("Deve lançar AlreadyExistsException quando CPF já existe")
    void shouldThrowAlreadyExistsException_WhenCPFExists() {
        // Arrange - Configurar CPF duplicado
        when(alunosRepository.existsByCpf(alunoDto.getCpf())).thenReturn(true);

        // Act & Assert
        AlreadyExistsException exception = assertThrows(
            AlreadyExistsException.class,
            () -> alunoServices.save(alunoDto),
            "Deveria lançar AlreadyExistsException quando CPF já está cadastrado"
        );

        assertEquals("CPF já cadastrado", exception.getMessage());
        verify(alunosRepository, times(1)).existsByCpf(alunoDto.getCpf());
        verify(alunosRepository, never()).existsByEmail(any());
        verify(alunosRepository, never()).save(any(Aluno.class));
    }

    /**
     * Teste Bonus: Validação de email duplicado
     * Garante que a validação de email também funciona
     */
    @Test
    @DisplayName("Deve lançar AlreadyExistsException quando email já existe")
    void shouldThrowAlreadyExistsException_WhenEmailExists() {
        // Arrange
        when(alunosRepository.existsByCpf(alunoDto.getCpf())).thenReturn(false);
        when(alunosRepository.existsByEmail(alunoDto.getEmail())).thenReturn(true);

        // Act & Assert
        AlreadyExistsException exception = assertThrows(
            AlreadyExistsException.class,
            () -> alunoServices.save(alunoDto),
            "Deveria lançar AlreadyExistsException quando email já está cadastrado"
        );

        assertEquals("Email já cadastrado", exception.getMessage());
        verify(alunosRepository, times(1)).existsByCpf(alunoDto.getCpf());
        verify(alunosRepository, times(1)).existsByEmail(alunoDto.getEmail());
        verify(alunosRepository, never()).save(any(Aluno.class));
    }

    /**
     * Teste Bonus: Validação de criação com sucesso
     * Garante que o fluxo normal de criação funciona
     */
    @Test
    @DisplayName("Deve criar aluno com sucesso quando não há duplicação")
    void shouldCreateStudentSuccessfully_WhenNoDuplication() {
        // Arrange
        when(alunosRepository.existsByCpf(alunoDto.getCpf())).thenReturn(false);
        when(alunosRepository.existsByEmail(alunoDto.getEmail())).thenReturn(false);
        when(alunosRepository.save(any(Aluno.class))).thenAnswer(invocation -> {
            Aluno aluno = invocation.getArgument(0);
            return aluno;
        });

        // Act
        Aluno resultado = alunoServices.save(alunoDto);

        // Assert
        assertNotNull(resultado);
        assertEquals(alunoDto.getNome(), resultado.getNome());
        assertEquals(alunoDto.getCpf(), resultado.getCpf());
        assertEquals(alunoDto.getEmail(), resultado.getEmail());
        assertFalse(resultado.isDeleted());
        assertTrue(resultado.isOptin());
        assertNotNull(resultado.getDataCriacao());
        assertNotNull(resultado.getDataAtualizacao());
        verify(alunosRepository, times(1)).save(any(Aluno.class));
    }

    /**
     * Teste adicional: Validação de CPF e Email duplicados simultaneamente
     */
    @Test
    @DisplayName("Deve validar CPF antes de email quando ambos estão duplicados")
    void shouldValidateCPFBeforeEmail_WhenBothAreDuplicated() {
        // Arrange - Simula situação onde CPF está duplicado
        when(alunosRepository.existsByCpf(alunoDto.getCpf())).thenReturn(true);

        // Act & Assert
        AlreadyExistsException exception = assertThrows(
            AlreadyExistsException.class,
            () -> alunoServices.save(alunoDto)
        );

        // Deve retornar erro de CPF primeiro (validação na ordem do código)
        assertEquals("CPF já cadastrado", exception.getMessage());
        verify(alunosRepository, times(1)).existsByCpf(alunoDto.getCpf());
        // Não deve chegar a verificar o email
        verify(alunosRepository, never()).existsByEmail(any());
    }
}
