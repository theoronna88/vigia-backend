package br.com.ronna.vigia.services;

import br.com.ronna.vigia.model.CursoValor;

import java.util.UUID;

public interface CursoValorServices {

    public void atribuirValorAoCurso(CursoValor cursoValor);

    public void expirarValoresAntigos(CursoValor cursoValor);

    CursoValor getValorAtualDoCurso(UUID cursoId);
}
