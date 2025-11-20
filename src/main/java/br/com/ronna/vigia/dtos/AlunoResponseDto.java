package br.com.ronna.vigia.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlunoResponseDto {

    private UUID id;
    private String nome;
    private String cpf;
    private String rg;
    private String orgaoEmissor;
    private String nacionalidade;
    private String naturalidade;
    private String nomeMae;
    private String nomePai;
    private String sexo;
    private String estadoCivil;
    private String escolaridade;
    private String profissao;
    private String cep;
    private String endereco;
    private String numero;
    private String complemento;
    private String bairro;
    private String cidade;
    private String estado;
    private String telefone;
    private String email;
    private LocalDate dataNascimento;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
    private boolean optin;
}
