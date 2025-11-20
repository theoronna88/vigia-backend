package br.com.ronna.vigia.services.Impl;

import br.com.ronna.vigia.dtos.TurmasDto;
import br.com.ronna.vigia.enums.TurmaStatus;
import br.com.ronna.vigia.exceptions.ConflictException;
import br.com.ronna.vigia.exceptions.NotFoundException;
import br.com.ronna.vigia.model.Turma;
import br.com.ronna.vigia.repository.AlunosRepository;
import br.com.ronna.vigia.repository.CursosRepository;
import br.com.ronna.vigia.repository.MatriculaRepository;
import br.com.ronna.vigia.repository.TurmasRepository;
import br.com.ronna.vigia.services.TurmasServices;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TurmasServicesImpl implements TurmasServices {

    private final TurmasRepository turmasRepository;
    private final CursosRepository cursosRepository;
    private final MatriculaRepository matriculaRepository;

    public TurmasServicesImpl(TurmasRepository turmasRepository, CursosRepository cursosRepository, MatriculaRepository matriculaRepository) {
        this.turmasRepository = turmasRepository;
        this.cursosRepository = cursosRepository;
        this.matriculaRepository = matriculaRepository;
    }


    public List<TurmasDto> listarTodas() {
        return turmasRepository.findByDeletedFalse().stream()
                .map(this::converterParaDto)
                .collect(Collectors.toList());
    }

    public Optional<TurmasDto> buscarPorId(UUID id) {
        var t = turmasRepository.findById(id)
                .filter(turma -> !turma.isDeleted())
                .orElseThrow(() -> new NotFoundException("Erro: Turma não encontrada para o ID especificado."));
        return t != null ? Optional.of(converterParaDto(t)) : Optional.empty();
    }

    public Optional<TurmasDto> salvar(TurmasDto turmasDto) {
        var cursoOpt = cursosRepository.findById(turmasDto.getCursoId());
        if (cursoOpt.isEmpty() || cursoOpt.get().isDeleted()) {
            throw new NotFoundException("Erro: Curso não encontrado para o ID especificado.");
        }
        Turma turma = new Turma();
        turma.setNome(turmasDto.getNome());
        turma.setTurno(turmasDto.getTurno());
        turma.setDataInicio(turmasDto.getDataInicio());
        turma.setDataTermino(turmasDto.getDataTermino());
        turma.setCapacidadeMaxima(turmasDto.getCapacidadeMaxima());
        turma.setInformadoInicio(turmasDto.isInformadoInicio());
        turma.setInformadoTermino(turmasDto.isInformadoTermino());
        turma.setCurso(cursoOpt.get());
        turma.setDataCriacao(LocalDate.now());
        turma.setDataAtualizacao(LocalDate.now());
        turma.setNumeroMatriculados(0);
        turma.setDeleted(false);
        turma.setStatus(TurmaStatus.ABERTA);
        Turma turmaSalva = turmasRepository.save(turma);
        System.out.println("Turma salva: " + turmaSalva);
        return Optional.of(converterParaDto(turmaSalva));
    }

    public Optional<TurmasDto> atualizar(UUID id, TurmasDto turmasDto) {
        var turmaAtualizar = turmasRepository.findById(id)
                .filter(turma -> !turma.isDeleted())
                .orElseThrow(() -> new NotFoundException("Erro: Turma não encontrada para o ID especificado."));

        var cursoOpt = cursosRepository.findById(turmasDto.getCursoId())
                .filter(curso -> !curso.isDeleted())
                .orElseThrow(() -> new NotFoundException("Erro: Curso não encontrado para o ID especificado."));


        turmaAtualizar.setId(turmasDto.getId());
        turmaAtualizar.setNome(turmasDto.getNome());
        turmaAtualizar.setTurno(turmasDto.getTurno());
        turmaAtualizar.setDataInicio(turmasDto.getDataInicio());
        turmaAtualizar.setDataTermino(turmasDto.getDataTermino());
        turmaAtualizar.setCapacidadeMaxima(turmasDto.getCapacidadeMaxima());
        turmaAtualizar.setCurso(cursoOpt);
        turmaAtualizar.setDataAtualizacao(LocalDate.now());
        turmaAtualizar.setNumeroMatriculados(turmasDto.getNumeroMatriculados());
        turmaAtualizar.setDeleted(false);

        return Optional.of(converterParaDto(turmasRepository.save(turmaAtualizar)));
    }

    @Override
    public Optional<TurmasDto> atualizarStatus(UUID id, TurmaStatus status) {
        var turmaOpt = turmasRepository.findById(id);
        if (!turmaOpt.isPresent()){
            throw new NotFoundException("Erro: Turma não encontrada para o ID especificado.");
        }
        turmaOpt.get().setStatus(status);
        turmasRepository.save(turmaOpt.get());
        return Optional.of(converterParaDto(turmaOpt.get()));
    }

    public boolean deletar(UUID id) {
        var t = turmasRepository.findById(id)
                .filter(turma -> !turma.isDeleted())
                .orElseThrow(() -> new NotFoundException("Erro: Turma não encontrada para o ID especificado."));

        if (t != null) {
            // Validar se existem matrículas ativas nesta turma
            var matriculasAtivas = matriculaRepository.findByIdTurma(t);
            if (!matriculasAtivas.isEmpty()) {
                throw new ConflictException("Não é possível deletar a turma pois existem " +
                    matriculasAtivas.size() + " matrícula(s) ativa(s) vinculada(s) a ela.");
            }

            t.setDeleted(true);
            t.setStatus(TurmaStatus.CANCELADA);
            turmasRepository.save(t);
            return true;
        }
        return false;
    }

    public TurmasDto converterParaDto(Turma turma) {
        TurmasDto dto = new TurmasDto("Erro: Nenhuma turma encontrada para o curso especificado.");
        dto.setId(turma.getId());
        dto.setNome(turma.getNome());
        dto.setCursoId(turma.getCurso().getId());
        dto.setTurno(turma.getTurno());
        dto.setDataInicio(turma.getDataInicio());
        dto.setDataTermino(turma.getDataTermino());
        dto.setCapacidadeMaxima(turma.getCapacidadeMaxima());
        dto.setNumeroMatriculados(turma.getNumeroMatriculados());
        dto.setInformadoInicio(turma.isInformadoInicio());
        dto.setInformadoTermino(turma.isInformadoTermino());
        dto.setStatus(turma.getStatus());
        return dto;
    }

    @Override
    public boolean atualizarExibicaoNoLead(UUID turmaId, UUID alunoId, boolean exibirNoLead) {
        return true;
       /* return turmaAlunoRepository.findByTurmaIdAndAlunoId(turmaId, alunoId)
                .map(turmaAluno -> {
                    turmaAluno.setExibirNoLead(exibirNoLead);
                    turmaAlunoRepository.save(turmaAluno);
                    return true;
                })
                .orElse(false);
        */
    }

    public Turma converterParaModel(TurmasDto dto) {
        Turma turma = new Turma();
        turma.setId(dto.getId());
        turma.setNome(dto.getNome());
        turma.setTurno(dto.getTurno());
        turma.setDataInicio(dto.getDataInicio());
        turma.setDataTermino(dto.getDataTermino());
        turma.setCapacidadeMaxima(dto.getCapacidadeMaxima());
        turma.setInformadoInicio(dto.isInformadoInicio());
        turma.setInformadoTermino(dto.isInformadoTermino());
        turma.setNumeroMatriculados(dto.getNumeroMatriculados());
        turma.setDeleted(false);
        turma.setStatus(dto.getStatus());
        return turma;
    }
}
