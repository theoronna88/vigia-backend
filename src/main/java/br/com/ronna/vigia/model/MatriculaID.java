package br.com.ronna.vigia.model;

import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Embeddable
public class MatriculaID {

    @ManyToOne
    @JoinColumn(name = "matricula_aluno_id")
    private Aluno aluno;

    @ManyToOne
    @JoinColumn(name = "matricula_turma_id")
    private Turma turma;

    public Aluno getAluno() {
        return aluno;
    }

    public void setAluno(Aluno aluno) {
        this.aluno = aluno;
    }

    public Turma getTurma() {
        return turma;
    }

    public void setTurma(Turma turma) {
        this.turma = turma;
    }



    @Override
    public String toString() {
        return "MatriculaID{" +
                "aluno=" + aluno +
                ", turma=" + turma +
                '}';
    }
}
