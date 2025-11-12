package br.com.ronna.vigia.services;

import br.com.ronna.vigia.dtos.CursosDto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CursosServices {


    public List<CursosDto> listarTodos();

    public Optional<CursosDto> buscarPorId(UUID id);

    public CursosDto salvar(CursosDto cursosDto);

    public Optional<CursosDto> atualizar(UUID id, CursosDto cursosDto);

    public boolean deletar(UUID id);

    public Object cursosLead();


}
