package br.com.ronna.vigia.model;

import br.com.ronna.vigia.enums.TurmaStatus;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "tbl_turmas")
@Data
public class Turma {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "CHAR(36)")
    private UUID id;

    @Column(nullable = false)
    private String nome;

    @ManyToOne
    @JoinColumn(name = "curso_id", nullable = false)
    private Curso curso;

    @Column(nullable = false)
    private String turno;

    @Column(nullable = false)
    private LocalDate dataInicio;

    @Column(nullable = false)
    private LocalDate dataTermino;

    @Column(nullable = false)
    private boolean deleted = false;

    @Column(nullable = false)
    private LocalDate dataCriacao;

    @Column(nullable = false)
    private LocalDate dataAtualizacao;

    @Column(nullable = false)
    private int capacidadeMaxima;

    @Column(nullable = false)
    private int numeroMatriculados;

    @Column(nullable = false)
    private boolean informadoInicio = false;

    @Column(nullable = false)
    private boolean informadoTermino = false;

    @Enumerated(EnumType.STRING)
    private TurmaStatus status;

}
