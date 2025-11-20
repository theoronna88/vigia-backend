package br.com.ronna.vigia.dtos;

import br.com.ronna.vigia.enums.StatusDocumento;
import br.com.ronna.vigia.enums.TipoDocumento;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatriculaDocumentoResponseDto {

    private UUID id;
    private UUID matriculaUnica;
    private TipoDocumento tipoDocumento;
    private StatusDocumento statusDocumento;
    private LocalDate dataEntrega;
    private LocalDate dataVencimento;
    private String observacoes;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
}
