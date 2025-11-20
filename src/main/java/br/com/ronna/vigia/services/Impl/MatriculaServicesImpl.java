package br.com.ronna.vigia.services.Impl;

import br.com.ronna.vigia.dtos.*;
import br.com.ronna.vigia.enums.MatriculaStatus;
import br.com.ronna.vigia.enums.StatusDocumento;
import br.com.ronna.vigia.enums.TurmaStatus;
import br.com.ronna.vigia.exceptions.AlreadyExistsException;
import br.com.ronna.vigia.exceptions.MaxStudentsException;
import br.com.ronna.vigia.exceptions.NotFoundException;
import br.com.ronna.vigia.model.*;
import br.com.ronna.vigia.repository.AlunosRepository;
import br.com.ronna.vigia.repository.MatriculaDocumentoRepository;
import br.com.ronna.vigia.repository.MatriculaFrequenciaRepository;
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
    private final MatriculaDocumentoRepository documentoRepository;
    private final MatriculaFrequenciaRepository frequenciaRepository;

    public MatriculaServicesImpl(MatriculaRepository repo,
                                 AlunosRepository alunoRepository,
                                 TurmasRepository turmaRepository,
                                 MatriculaDocumentoRepository documentoRepository,
                                 MatriculaFrequenciaRepository frequenciaRepository) {
        this.alunoRepository = alunoRepository;
        this.turmaRepository = turmaRepository;
        this.repo = repo;
        this.documentoRepository = documentoRepository;
        this.frequenciaRepository = frequenciaRepository;
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

    @Override
    public MatriculaResponseDto cancelarMatricula(CancelarMatriculaDto cancelarMatriculaDto) {
        Matricula matricula = repo.findByMatriculaUnica(cancelarMatriculaDto.getMatriculaUnica());

        if (matricula == null) {
            throw new NotFoundException("Matrícula não encontrada com o ID: " + cancelarMatriculaDto.getMatriculaUnica());
        }

        if (matricula.getStatus() == MatriculaStatus.CANCELADA) {
            throw new AlreadyExistsException("A matrícula já está cancelada.");
        }

        if (matricula.getStatus() == MatriculaStatus.CONCLUIDA) {
            throw new IllegalStateException("Não é possível cancelar uma matrícula já concluída.");
        }

        matricula.setStatus(MatriculaStatus.CANCELADA);
        matricula.setMotivoCancelamento(cancelarMatriculaDto.getMotivoCancelamento());
        matricula.setDataCancelamento(LocalDateTime.now());
        matricula.setDataAtualizacao(LocalDateTime.now());

        return convertToDto(repo.save(matricula));
    }

    @Override
    public MatriculaDocumentoResponseDto registrarDocumento(MatriculaDocumentoDto documentoDto) {
        Matricula matricula = repo.findByMatriculaUnica(documentoDto.getMatriculaUnica());

        if (matricula == null) {
            throw new NotFoundException("Matrícula não encontrada com o ID: " + documentoDto.getMatriculaUnica());
        }

        MatriculaDocumento documento = new MatriculaDocumento();
        documento.setMatricula(matricula);
        documento.setTipoDocumento(documentoDto.getTipoDocumento());
        documento.setStatusDocumento(documentoDto.getStatusDocumento() != null ?
                documentoDto.getStatusDocumento() : StatusDocumento.PENDENTE);
        documento.setDataEntrega(documentoDto.getDataEntrega());
        documento.setDataVencimento(documentoDto.getDataVencimento());
        documento.setObservacoes(documentoDto.getObservacoes());
        documento.setDataCriacao(LocalDateTime.now());

        MatriculaDocumento saved = documentoRepository.save(documento);
        return convertDocumentoToDto(saved);
    }

    @Override
    public MatriculaDocumentoResponseDto atualizarDocumento(UUID documentoId, MatriculaDocumentoDto documentoDto) {
        MatriculaDocumento documento = documentoRepository.findById(documentoId)
                .orElseThrow(() -> new NotFoundException("Documento não encontrado com o ID: " + documentoId));

        if (documentoDto.getStatusDocumento() != null) {
            documento.setStatusDocumento(documentoDto.getStatusDocumento());
        }
        if (documentoDto.getDataEntrega() != null) {
            documento.setDataEntrega(documentoDto.getDataEntrega());
        }
        if (documentoDto.getDataVencimento() != null) {
            documento.setDataVencimento(documentoDto.getDataVencimento());
        }
        if (documentoDto.getObservacoes() != null) {
            documento.setObservacoes(documentoDto.getObservacoes());
        }
        documento.setDataAtualizacao(LocalDateTime.now());

        MatriculaDocumento updated = documentoRepository.save(documento);
        return convertDocumentoToDto(updated);
    }

    @Override
    public List<MatriculaDocumentoResponseDto> listarDocumentosPorMatricula(UUID matriculaUnica) {
        Matricula matricula = repo.findByMatriculaUnica(matriculaUnica);

        if (matricula == null) {
            throw new NotFoundException("Matrícula não encontrada com o ID: " + matriculaUnica);
        }

        List<MatriculaDocumento> documentos = documentoRepository.findByMatricula(matricula);
        return documentos.stream()
                .map(this::convertDocumentoToDto)
                .collect(Collectors.toList());
    }

    @Override
    public void removerDocumento(UUID documentoId) {
        MatriculaDocumento documento = documentoRepository.findById(documentoId)
                .orElseThrow(() -> new NotFoundException("Documento não encontrado com o ID: " + documentoId));
        documentoRepository.delete(documento);
    }

    @Override
    public MatriculaFrequenciaResponseDto registrarFrequencia(MatriculaFrequenciaDto frequenciaDto) {
        Matricula matricula = repo.findByMatriculaUnica(frequenciaDto.getMatriculaUnica());

        if (matricula == null) {
            throw new NotFoundException("Matrícula não encontrada com o ID: " + frequenciaDto.getMatriculaUnica());
        }

        // Verifica se já existe uma frequência para esta data
        var frequenciaExistente = frequenciaRepository.findByMatriculaAndDataAula(
                matricula, frequenciaDto.getDataAula());

        if (frequenciaExistente.isPresent()) {
            throw new AlreadyExistsException("Já existe um registro de frequência para esta data.");
        }

        MatriculaFrequencia frequencia = new MatriculaFrequencia();
        frequencia.setMatricula(matricula);
        frequencia.setDataAula(frequenciaDto.getDataAula());
        frequencia.setPresenca(frequenciaDto.getPresenca());
        frequencia.setObservacoes(frequenciaDto.getObservacoes());
        frequencia.setDataCriacao(LocalDateTime.now());

        MatriculaFrequencia saved = frequenciaRepository.save(frequencia);
        return convertFrequenciaToDto(saved);
    }

    @Override
    public MatriculaFrequenciaResponseDto atualizarFrequencia(UUID frequenciaId, MatriculaFrequenciaDto frequenciaDto) {
        MatriculaFrequencia frequencia = frequenciaRepository.findById(frequenciaId)
                .orElseThrow(() -> new NotFoundException("Registro de frequência não encontrado com o ID: " + frequenciaId));

        if (frequenciaDto.getPresenca() != null) {
            frequencia.setPresenca(frequenciaDto.getPresenca());
        }
        if (frequenciaDto.getObservacoes() != null) {
            frequencia.setObservacoes(frequenciaDto.getObservacoes());
        }
        frequencia.setDataAtualizacao(LocalDateTime.now());

        MatriculaFrequencia updated = frequenciaRepository.save(frequencia);
        return convertFrequenciaToDto(updated);
    }

    @Override
    public List<MatriculaFrequenciaResponseDto> listarFrequenciasPorMatricula(UUID matriculaUnica) {
        Matricula matricula = repo.findByMatriculaUnica(matriculaUnica);

        if (matricula == null) {
            throw new NotFoundException("Matrícula não encontrada com o ID: " + matriculaUnica);
        }

        List<MatriculaFrequencia> frequencias = frequenciaRepository.findByMatricula(matricula);
        return frequencias.stream()
                .map(this::convertFrequenciaToDto)
                .collect(Collectors.toList());
    }

    @Override
    public void removerFrequencia(UUID frequenciaId) {
        MatriculaFrequencia frequencia = frequenciaRepository.findById(frequenciaId)
                .orElseThrow(() -> new NotFoundException("Registro de frequência não encontrado com o ID: " + frequenciaId));
        frequenciaRepository.delete(frequencia);
    }

    private MatriculaDocumentoResponseDto convertDocumentoToDto(MatriculaDocumento documento) {
        MatriculaDocumentoResponseDto dto = new MatriculaDocumentoResponseDto();
        dto.setId(documento.getId());
        dto.setMatriculaUnica(documento.getMatricula().getMatriculaUnica());
        dto.setTipoDocumento(documento.getTipoDocumento());
        dto.setStatusDocumento(documento.getStatusDocumento());
        dto.setDataEntrega(documento.getDataEntrega());
        dto.setDataVencimento(documento.getDataVencimento());
        dto.setObservacoes(documento.getObservacoes());
        dto.setDataCriacao(documento.getDataCriacao());
        dto.setDataAtualizacao(documento.getDataAtualizacao());
        return dto;
    }

    private MatriculaFrequenciaResponseDto convertFrequenciaToDto(MatriculaFrequencia frequencia) {
        MatriculaFrequenciaResponseDto dto = new MatriculaFrequenciaResponseDto();
        dto.setId(frequencia.getId());
        dto.setMatriculaUnica(frequencia.getMatricula().getMatriculaUnica());
        dto.setDataAula(frequencia.getDataAula());
        dto.setPresenca(frequencia.getPresenca());
        dto.setObservacoes(frequencia.getObservacoes());
        dto.setDataCriacao(frequencia.getDataCriacao());
        dto.setDataAtualizacao(frequencia.getDataAtualizacao());
        return dto;
    }
}
