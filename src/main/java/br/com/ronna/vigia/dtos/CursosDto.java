package br.com.ronna.vigia.dtos;

import br.com.ronna.vigia.enums.CursoStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class CursosDto {

    private UUID id;
    private String nome;
    private String descricao;
    private boolean deleted;
    private Double cargaHoraria;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
    private CursoStatus status;
    private Double valor;
}
