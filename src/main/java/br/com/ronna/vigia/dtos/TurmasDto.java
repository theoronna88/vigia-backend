package br.com.ronna.vigia.dtos;

import br.com.ronna.vigia.enums.TurmaStatus;
import br.com.ronna.vigia.model.Aluno;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
public class TurmasDto {

        private UUID id;
        private String nome;
        private UUID cursoId;
        private String turno;
        private LocalDate dataInicio;
        private LocalDate dataTermino;
        private int capacidadeMaxima;
        private int numeroMatriculados;
        private boolean informadoInicio;
        private boolean informadoTermino;
        private List<Aluno> alunos;
        private TurmaStatus status;


        public TurmasDto() {
        }

        public TurmasDto(String mensagem) {
            this.nome = mensagem;
        }
}
