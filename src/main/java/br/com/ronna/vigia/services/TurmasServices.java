package br.com.ronna.vigia.services;

import br.com.ronna.vigia.dtos.TurmasDto;
import br.com.ronna.vigia.enums.TurmaStatus;
import br.com.ronna.vigia.model.Turma;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TurmasServices {

    public List<TurmasDto> listarTodas();

    public Optional<TurmasDto> buscarPorId(UUID id);

    public Optional<TurmasDto> salvar(TurmasDto turmasDto);

    public Optional<TurmasDto> atualizar(UUID id, TurmasDto turmasDto);

    public Optional<TurmasDto> atualizarStatus(UUID id, TurmaStatus status);

    public boolean deletar(UUID id);

    public Turma converterParaModel(TurmasDto dto);

    public TurmasDto converterParaDto(Turma model);

    boolean atualizarExibicaoNoLead(UUID turmaId, UUID alunoId, boolean exibirNoLead);
}
