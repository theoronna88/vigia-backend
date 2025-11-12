package br.com.ronna.vigia.services.Impl;

import br.com.ronna.vigia.dtos.CursosDto;
import br.com.ronna.vigia.enums.CursoStatus;
import br.com.ronna.vigia.model.Curso;
import br.com.ronna.vigia.model.CursoValor;
import br.com.ronna.vigia.repository.CursosRepository;
import br.com.ronna.vigia.services.CursoValorServices;
import br.com.ronna.vigia.services.CursosServices;
import br.com.ronna.vigia.services.TurmasServices;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CursosServicesImpl implements CursosServices {

    private final CursoValorServices cursoValorServices;
    private CursosRepository cursosRepository;
    private TurmasServices turmasServices;

    public CursosServicesImpl(CursosRepository cursosRepository, TurmasServices turmasServices, CursoValorServices cursoValorServices) {
        this.cursosRepository = cursosRepository;
        this.turmasServices = turmasServices;
        this.cursoValorServices = cursoValorServices;
    }


    public List<CursosDto> listarTodos() {
        return cursosRepository.findAll().stream()
                .filter(curso -> !curso.isDeleted())
                .map(this::converterParaDto)
                .collect(Collectors.toList());
    }

    public Optional<CursosDto> buscarPorId(UUID id) {
        return cursosRepository.findById(id)
                .filter(curso -> !curso.isDeleted())
                .map(this::converterParaDto);
    }

    public CursosDto salvar(CursosDto cursosDto) {
        System.out.println("Salvando curso: " + cursosDto.toString());
        Curso curso = converterParaModel(cursosDto);
        System.out.println("Curso convertido: " + curso.toString());
        curso.setCargaHoraria(cursosDto.getCargaHoraria());
        curso.setDataCriacao(LocalDateTime.now());
        curso.setDataAtualizacao(LocalDateTime.now());
        curso.setStatus(CursoStatus.ATIVO);
        curso = cursosRepository.save(curso);
        return converterParaDto(curso);
    }

    public Optional<CursosDto> atualizar(UUID id, CursosDto cursosDto) {
        return cursosRepository.findById(id)
                .filter(curso -> !curso.isDeleted())
                .map(curso -> {
                    curso.setNome(cursosDto.getNome());
                    curso.setDescricao(cursosDto.getDescricao());
                    curso.setCargaHoraria(cursosDto.getCargaHoraria());
                    curso.setDataAtualizacao(LocalDateTime.now());
                    curso.setStatus(CursoStatus.ATIVO);
                    return converterParaDto(cursosRepository.save(curso));
                });
    }

    public boolean deletar(UUID id) {
        return cursosRepository.findById(id)
                .filter(curso -> !curso.isDeleted())
                .map(curso -> {
                    curso.setDeleted(true);
                    curso.setDataAtualizacao(LocalDateTime.now());
                    curso.setStatus(CursoStatus.INATIVO);
                    cursosRepository.save(curso);
                    return true;
                })
                .orElse(false);
    }

    @Override
    public List<Object> cursosLead() {
        return List.of();
    }


    private CursosDto converterParaDto(Curso curso) {
        CursosDto dto = new CursosDto();
        dto.setId(curso.getId());
        dto.setNome(curso.getNome());
        dto.setDescricao(curso.getDescricao());
        dto.setCargaHoraria(curso.getCargaHoraria());
        dto.setDeleted(curso.isDeleted());
        dto.setDataCriacao(curso.getDataCriacao());
        dto.setStatus(curso.getStatus());
        dto.setDataAtualizacao(curso.getDataAtualizacao());
        return dto;
    }

    private Curso converterParaModel(CursosDto dto) {
        Curso curso = new Curso();
        curso.setNome(dto.getNome());
        curso.setCargaHoraria(dto.getCargaHoraria());
        curso.setDescricao(dto.getDescricao());
        curso.setStatus(dto.getStatus());
        curso.setDeleted(false);
        return curso;
    }


}
