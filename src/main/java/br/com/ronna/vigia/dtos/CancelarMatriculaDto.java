package br.com.ronna.vigia.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CancelarMatriculaDto {

    private UUID matriculaUnica;
    private String motivoCancelamento;
}
