package br.com.ronna.vigia.dtos;

import br.com.ronna.vigia.model.Curso;
import br.com.ronna.vigia.model.Turma;
import lombok.Data;

import java.util.List;

@Data
public class CursoLead {

    private Curso curso;

    private List<Turma> turmas;
}
