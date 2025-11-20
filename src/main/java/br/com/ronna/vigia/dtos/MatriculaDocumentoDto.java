package br.com.ronna.vigia.dtos;

import br.com.ronna.vigia.enums.StatusDocumento;
import br.com.ronna.vigia.enums.TipoDocumento;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatriculaDocumentoDto {

    private UUID matriculaUnica;
    private TipoDocumento tipoDocumento;
    private StatusDocumento statusDocumento;
    private LocalDate dataEntrega;
    private LocalDate dataVencimento;
    private String observacoes;
}
