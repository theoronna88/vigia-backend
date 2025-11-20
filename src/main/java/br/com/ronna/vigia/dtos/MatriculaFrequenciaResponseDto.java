package br.com.ronna.vigia.dtos;

import br.com.ronna.vigia.enums.TipoPresenca;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatriculaFrequenciaResponseDto {

    private UUID id;
    private UUID matriculaUnica;
    private LocalDate dataAula;
    private TipoPresenca presenca;
    private String observacoes;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
}
