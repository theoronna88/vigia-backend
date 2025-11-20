package br.com.ronna.vigia.services.Impl;

import br.com.ronna.vigia.dtos.AlunoResponseDto;
import br.com.ronna.vigia.dtos.AlunosDto;
import br.com.ronna.vigia.dtos.SearchDto;
import br.com.ronna.vigia.exceptions.NotFoundException;
import br.com.ronna.vigia.model.Aluno;
import br.com.ronna.vigia.repository.AlunosRepository;
import br.com.ronna.vigia.services.AlunoServices;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AlunoServicesImpl implements AlunoServices {

    private AlunosRepository alunosRepository;

    public AlunoServicesImpl(AlunosRepository alunosRepository) {
        this.alunosRepository = alunosRepository;
    }

    @Override
    public List<Aluno> findAll() {
        return alunosRepository.findAll();
    }

    @Override
    public List<Aluno> findAllActive() {
        return alunosRepository.findAll().stream()
                .filter(aluno -> !aluno.isDeleted())
                .toList();
    }

    @Override
    public Optional<Aluno> findById(UUID id) {
        var aluno = alunosRepository.findById(id);
        if (!aluno.isPresent()) {
            throw new RuntimeException("Erro: Aluno não encontrado");
        }
        return aluno;
    }

    @Override
    public Aluno save(AlunosDto alunoDto) {
        if (alunosRepository.existsByCpf(alunoDto.getCpf())) {
            throw new RuntimeException("CPF já cadastrado");
        }
        if (alunosRepository.existsByEmail(alunoDto.getEmail())) {
            throw new RuntimeException("Email já cadastrado");
        }

        var aluno = new Aluno();
        BeanUtils.copyProperties(alunoDto, aluno);
        aluno.setDataCriacao(LocalDateTime.now());
        aluno.setDataAtualizacao(LocalDateTime.now());
        aluno.setDeleted(false);
        aluno.setOptin(true);
        return alunosRepository.save(aluno);
    }

    @Override
    public Aluno update(UUID id, AlunosDto alunoDto) {
        Optional<Aluno> existingAluno = alunosRepository.findById(id);
        if (existingAluno.isEmpty()) {
            throw new NotFoundException("Erro: Aluno não encontrado");
        }

        Aluno alunoToUpdate = existingAluno.get();
        BeanUtils.copyProperties(alunoDto, alunoToUpdate);
        alunoToUpdate.setDataAtualizacao(LocalDateTime.now());
        return alunosRepository.save(alunoToUpdate);
    }

    @Override
    public void deleteById(UUID id) {
        if (!alunosRepository.existsById(id)) {
            throw new NotFoundException("Erro: Aluno não encontrado");
        }
        alunosRepository.findById(id).ifPresent(aluno -> {
            aluno.setDeleted(true);
            aluno.setDataAtualizacao(LocalDateTime.now());
            alunosRepository.save(aluno);
        });
    }

    @Override
    public List<Aluno> findAlunosLead() {
        // TODO: Implementar método para buscar alunos que devem aparecer no lead
        return null;
    }

    @Override
    public List<Aluno> searchByFilter(SearchDto searchDto) {
        return alunosRepository.findAlunoByNomeContainingOrEmailOrCpfOrRgOrTelefoneOrSexoOrEstadoCivilOrCepOrBairroOrCidadeOrOptinOrDataNascimento(
                searchDto.getNome(),
                searchDto.getEmail(),
                searchDto.getCpf(),
                searchDto.getRg(),
                searchDto.getTelefone(),
                searchDto.getSexo(),
                searchDto.getEstadoCivil(),
                searchDto.getCep(),
                searchDto.getBairro(),
                searchDto.getCidade(),
                searchDto.getOptin(),
                searchDto.getDataNascimento()
        );
    }

    @Override
    public List<AlunoResponseDto> findAllActiveDto() {
        return findAllActive().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<AlunoResponseDto> findAllDto() {
        return findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<AlunoResponseDto> searchByFilterDto(SearchDto searchDto) {
        return searchByFilter(searchDto).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public AlunoResponseDto findByIdDto(UUID id) {
        return findById(id)
                .map(this::convertToDto)
                .orElseThrow(() -> new NotFoundException("Erro: Aluno não encontrado"));
    }

    @Override
    public AlunoResponseDto saveDto(AlunosDto alunoDto) {
        return convertToDto(save(alunoDto));
    }

    @Override
    public AlunoResponseDto updateDto(UUID id, AlunosDto alunosDto) {
        return convertToDto(update(id, alunosDto));
    }

    @Override
    public List<AlunoResponseDto> findAlunosLeadDto() {
        List<Aluno> alunos = findAlunosLead();
        if (alunos == null) {
            return null;
        }
        return alunos.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private AlunoResponseDto convertToDto(Aluno aluno) {
        AlunoResponseDto dto = new AlunoResponseDto();
        dto.setId(aluno.getId());
        dto.setNome(aluno.getNome());
        dto.setCpf(aluno.getCpf());
        dto.setRg(aluno.getRg());
        dto.setOrgaoEmissor(aluno.getOrgaoEmissor());
        dto.setNacionalidade(aluno.getNacionalidade());
        dto.setNaturalidade(aluno.getNaturalidade());
        dto.setNomeMae(aluno.getNomeMae());
        dto.setNomePai(aluno.getNomePai());
        dto.setSexo(aluno.getSexo());
        dto.setEstadoCivil(aluno.getEstadoCivil());
        dto.setEscolaridade(aluno.getEscolaridade());
        dto.setProfissao(aluno.getProfissao());
        dto.setCep(aluno.getCep());
        dto.setEndereco(aluno.getEndereco());
        dto.setNumero(aluno.getNumero());
        dto.setComplemento(aluno.getComplemento());
        dto.setBairro(aluno.getBairro());
        dto.setCidade(aluno.getCidade());
        dto.setEstado(aluno.getEstado());
        dto.setTelefone(aluno.getTelefone());
        dto.setEmail(aluno.getEmail());
        dto.setDataNascimento(aluno.getDataNascimento());
        dto.setDataCriacao(aluno.getDataCriacao());
        dto.setDataAtualizacao(aluno.getDataAtualizacao());
        dto.setOptin(aluno.isOptin());
        return dto;
    }


}
