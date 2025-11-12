package br.com.ronna.vigia.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class AlunosDto {

    @NotBlank(message = "O nome não pode estar em branco")
    private String nome;

    @NotBlank(message = "O CPF não pode estar em branco")
    @Size(min = 11, max = 14, message = "CPF deve ter entre 11 e 14 caracteres")
    private String cpf;

    @NotBlank(message = "O RG não pode estar em branco")
    private String rg;

    @NotBlank(message = "O órgão emissor não pode estar em branco")
    private String orgaoEmissor;

    @NotBlank
    private String nacionalidade;

    @NotBlank
    private String naturalidade;

    @NotBlank(message = "O nome da mãe não pode estar em branco")
    private String nomeMae;

    @NotBlank(message = "O nome do pai não pode estar em branco")
    private String nomePai;

    @NotBlank
    private String sexo;

    @NotBlank
    private String estadoCivil;

    @NotBlank
    private String escolaridade;

    @NotBlank
    private String profissao;

    @NotBlank
    private String cep;

    @NotBlank
    private String endereco;

    @NotBlank(message = "O número do endereço não pode estar em branco")
    private String numero;

    private String complemento;

    @NotBlank
    private String bairro;

    @NotBlank
    private String cidade;

    @NotBlank
    private String estado;

    @NotBlank(message = "O telefone não pode estar em branco")
    private String telefone;

    @NotBlank(message = "O email não pode estar em branco")
    @Email(message = "Formato de e-mail inválido")
    private String email;

    @NotNull(message = "A data de nascimento não pode ser nula")
    @Past(message = "A data de nascimento deve ser no passado")
    private LocalDate dataNascimento;

    @NotNull(message = "O campo 'optin' deve ser informado")
    private Boolean optin;
}