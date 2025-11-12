package br.com.ronna.vigia.services;

import br.com.ronna.vigia.dtos.AlunosDto;
import br.com.ronna.vigia.dtos.SearchDto;
import br.com.ronna.vigia.model.Aluno;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AlunoServices {

    public List<Aluno> findAll();

    public List<Aluno> findAllActive();

    public Optional<Aluno> findById(UUID id);

    public Aluno save(AlunosDto alunoDto);

    public Aluno update(UUID id, AlunosDto alunosDto);

    public void deleteById(UUID id);

    List<Aluno> findAlunosLead();

    List<Aluno> searchByFilter(SearchDto searchDto);
}
