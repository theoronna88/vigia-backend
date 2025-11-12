package br.com.ronna.vigia.dtos;

import br.com.ronna.vigia.enums.MatriculaStatus;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class MatriculaDto {

    private UUID alunoId;
    private UUID turmaId;
    private MatriculaStatus status;
    private LocalDate dataMatricula;

}
