package br.com.ronna.vigia.dtos;

import br.com.ronna.vigia.enums.MatriculaStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatriculaResponseDto {

    private UUID alunoId;
    private UUID turmaId;
    private UUID matriculaUnica;
    private LocalDate dataMatricula;
    private MatriculaStatus status;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
}
