package br.com.ronna.vigia.dtos;

import br.com.ronna.vigia.enums.MatriculaStatus;
import lombok.Data;

import java.util.UUID;

@Data
public class MatriculaStatusDto {
    private MatriculaStatus status;
    private UUID matriculaUnica;
}
