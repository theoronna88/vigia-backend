package br.com.ronna.vigia.dtos;

import br.com.ronna.vigia.enums.TipoPresenca;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatriculaFrequenciaDto {

    private UUID matriculaUnica;
    private LocalDate dataAula;
    private TipoPresenca presenca;
    private String observacoes;
}
