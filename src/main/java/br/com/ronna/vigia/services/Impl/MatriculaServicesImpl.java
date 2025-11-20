package br.com.ronna.vigia.services.Impl;

import br.com.ronna.vigia.dtos.MatriculaDto;
import br.com.ronna.vigia.dtos.MatriculaResponseDto;
import br.com.ronna.vigia.dtos.MatriculaStatusDto;
import br.com.ronna.vigia.enums.MatriculaStatus;
import br.com.ronna.vigia.enums.TurmaStatus;
import br.com.ronna.vigia.exceptions.AlreadyExistsException;
import br.com.ronna.vigia.exceptions.MaxStudentsException;
import br.com.ronna.vigia.exceptions.NotFoundException;
import br.com.ronna.vigia.model.Aluno;
import br.com.ronna.vigia.model.Matricula;
import br.com.ronna.vigia.model.MatriculaID;
import br.com.ronna.vigia.model.Turma;
import br.com.ronna.vigia.repository.AlunosRepository;
import br.com.ronna.vigia.repository.MatriculaRepository;
import br.com.ronna.vigia.repository.TurmasRepository;
import br.com.ronna.vigia.services.MatriculaServices;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MatriculaServicesImpl implements MatriculaServices {

    //TODO: adicionar verificação de pré-requisitos antes de matricular o aluno na turma
    //TODO: implementar lógica para lidar com turmas lotadas
    //TODO: adicionar verificação de conflito de horários entre turmas

    private final MatriculaRepository repo;
    private final AlunosRepository alunoRepository;
    private final TurmasRepository turmaRepository;

    public MatriculaServicesImpl(MatriculaRepository repo, AlunosRepository alunoRepository, TurmasRepository turmaRepository) {
        this.alunoRepository = alunoRepository;
        this.turmaRepository = turmaRepository;
        this.repo = repo;
    }

    @Override
    public Matricula adicionarAlunoATurma(MatriculaDto matriculaDto) {
        var matriculaModel = new Matricula();
        var aluno = alunoRepository.findById(matriculaDto.getAlunoId())
                .orElseThrow(() -> new NotFoundException("Aluno não encontrado com o ID: " + matriculaDto.getAlunoId()));
        var turma = turmaRepository.findById(matriculaDto.getTurmaId())
                .orElseThrow(() -> new NotFoundException("Turma não encontrada com o ID: " + matriculaDto.getTurmaId()));

        var matriculaId = new MatriculaID();
        matriculaId.setAluno(aluno);
        matriculaId.setTurma(turma);

        repo.findById(matriculaId).ifPresent(existingMatricula -> {
            throw new AlreadyExistsException("Erro: O aluno já está matriculado nesta turma.");
        });

        //Verificando se a turma está ativa
        if (turma.getStatus().equals(TurmaStatus.CANCELADA) || turma.getStatus().equals(TurmaStatus.FINALIZADA)) {
            throw new NotFoundException("Erro: Não é possível matricular alunos em turmas que estão " + turma.getStatus().toString().toLowerCase() + ".");
        }

        //Verificando se a turma atingiu o limite de alunos
        if (turma.getNumeroMatriculados() + 1 > turma.getCapacidadeMaxima()) {
            throw new MaxStudentsException("Erro: A turma atingiu o limite máximo de alunos.");
        }

        //Verificando se aluno já está matriculado em outra turma no mesmo horário
        List<Matricula> matriculasAtuais = repo.findByIdAluno(aluno);
        for (Matricula m : matriculasAtuais) {
            //Turno está com o formato de string e é um horário: 08:00 - 14:00
            if (m.getId().getTurma().getTurno().equals(turma.getTurno())) {
                throw new AlreadyExistsException("Erro: O aluno já está matriculado em outra turma no mesmo horário: " + turma.getTurno());
            }
        }

        turma.getDataInicio();
        turma.getDataTermino();
        turma.getTurno();



        matriculaModel.setId(matriculaId);
        matriculaModel.setStatus(MatriculaStatus.PENDENTE);
        matriculaModel.setDataMatricula(matriculaDto.getDataMatricula());
        matriculaModel.setDataCriacao(LocalDateTime.now());
        matriculaModel.setMatriculaUnica(UUID.randomUUID());
        return repo.save(matriculaModel);
    }

    @Override
    public List<Matricula> listarMatriculasPorAluno(Aluno aluno) {
        var listMatriculas = repo.findByIdAluno(aluno);
        if (listMatriculas.isEmpty()) {
            throw new NotFoundException("Nenhuma matrícula encontrada para o aluno com ID: " + aluno.getId());
        }
        return listMatriculas;
    }

    @Override
    public List<Matricula> listarMatriculasPorTurma(Turma turma) {
        var listMatriculas = repo.findByIdTurma(turma);
        if (listMatriculas.isEmpty()) {
            throw new NotFoundException("Nenhuma matrícula encontrada para a turma com ID: " + turma.getId());
        }
        return listMatriculas;
    }

    @Override
    public Matricula alterarStatusMatricula(MatriculaStatusDto matriculaStatusDto) {
        Matricula matricula = repo.findByMatriculaUnica(matriculaStatusDto.getMatriculaUnica());
        if (matricula == null) {
            throw new NotFoundException("Erro: Matrícula não encontrada com o ID: " + matriculaStatusDto.getMatriculaUnica());
        }
        matricula.setStatus(matriculaStatusDto.getStatus());
        matricula.setDataAtualizacao(LocalDateTime.now());
        return repo.save(matricula);
    }

    @Override
    public MatriculaResponseDto adicionarAlunoATurmaDto(MatriculaDto matriculaDto) {
        return convertToDto(adicionarAlunoATurma(matriculaDto));
    }

    @Override
    public List<MatriculaResponseDto> listarMatriculasPorAlunoDto(UUID alunoId) {
        Aluno aluno = alunoRepository.findById(alunoId)
                .orElseThrow(() -> new NotFoundException("Aluno não encontrado com o ID: " + alunoId));
        return listarMatriculasPorAluno(aluno).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<MatriculaResponseDto> listarMatriculasPorTurmaDto(UUID turmaId) {
        Turma turma = turmaRepository.findById(turmaId)
                .orElseThrow(() -> new NotFoundException("Turma não encontrada com o ID: " + turmaId));
        return listarMatriculasPorTurma(turma).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public MatriculaResponseDto alterarStatusMatriculaDto(MatriculaStatusDto matriculaStatusDto) {
        return convertToDto(alterarStatusMatricula(matriculaStatusDto));
    }

    private MatriculaResponseDto convertToDto(Matricula matricula) {
        MatriculaResponseDto dto = new MatriculaResponseDto();
        dto.setAlunoId(matricula.getId().getAluno().getId());
        dto.setTurmaId(matricula.getId().getTurma().getId());
        dto.setMatriculaUnica(matricula.getMatriculaUnica());
        dto.setDataMatricula(matricula.getDataMatricula());
        dto.setStatus(matricula.getStatus());
        dto.setDataCriacao(matricula.getDataCriacao());
        dto.setDataAtualizacao(matricula.getDataAtualizacao());
        return dto;
    }
}
