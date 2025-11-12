package br.com.ronna.vigia.model;

import br.com.ronna.vigia.enums.MatriculaStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tbl_matriculas")
public class Matricula {

    @Id
    private MatriculaID id;

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao;

    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "matricula_unica", nullable = false, unique = true, columnDefinition = "CHAR(36)")
    private UUID matriculaUnica;

    private LocalDate dataMatricula;

    @Enumerated(EnumType.STRING)
    private MatriculaStatus status;

    private LocalDateTime dataAtualizacao;

    public MatriculaID getId() {
        return id;
    }

    public void setId(MatriculaID id) {
        this.id = id;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public UUID getMatriculaUnica() {
        return matriculaUnica;
    }

    public LocalDate getDataMatricula() {
        return dataMatricula;
    }

    public void setDataMatricula(LocalDate dataMatricula) {
        this.dataMatricula = dataMatricula;
    }

    public MatriculaStatus getStatus() {
        return status;
    }

    public void setStatus(MatriculaStatus status) {
        this.status = status;
    }

    public LocalDateTime getDataAtualizacao() {
        return dataAtualizacao;
    }

    public void setDataAtualizacao(LocalDateTime dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }

    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "matricula_unica", updatable = false, nullable = false, columnDefinition = "CHAR(36)")
    public void setMatriculaUnica(UUID matriculaUnica) {
        this.matriculaUnica = matriculaUnica;
    }

    @Override
    public String toString() {
        return "Matricula{" +
                "id=" + id.toString() +
                ", dataCriacao=" + dataCriacao +
                ", matriculaUnica=" + matriculaUnica +
                '}';
    }
}
