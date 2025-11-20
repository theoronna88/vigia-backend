package br.com.ronna.vigia.controller;

import br.com.ronna.vigia.dtos.AlunoResponseDto;
import br.com.ronna.vigia.dtos.AlunosDto;
import br.com.ronna.vigia.dtos.SearchDto;
import br.com.ronna.vigia.services.AlunoServices;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/alunos")
@CrossOrigin(origins = "*")
public class AlunosController {

    private final AlunoServices alunosServices;

    public AlunosController(AlunoServices alunosServices) {
        this.alunosServices = alunosServices;
    }

    @GetMapping
    public ResponseEntity<List<AlunoResponseDto>> getAllAlunos() {
        List<AlunoResponseDto> alunos = alunosServices.findAllActiveDto();
        return ResponseEntity.ok(alunos);
    }

    @GetMapping("/all")
    public ResponseEntity<List<AlunoResponseDto>> getAllAlunosIncludingDeleted() {
        List<AlunoResponseDto> alunos = alunosServices.findAllDto();
        return ResponseEntity.ok(alunos);
    }

    @PostMapping("/search")
    public ResponseEntity<List<AlunoResponseDto>> searchAlunosByFilter(@RequestBody SearchDto searchDto) {
        List<AlunoResponseDto> alunos = alunosServices.searchByFilterDto(searchDto);
        return ResponseEntity.ok(alunos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlunoResponseDto> getAlunoById(@PathVariable UUID id) {
        return ResponseEntity.ok(alunosServices.findByIdDto(id));
    }

    @GetMapping("/lead")
    public ResponseEntity<List<AlunoResponseDto>> getLeadAlunos() {
        return ResponseEntity.ok(alunosServices.findAlunosLeadDto());
    }

    @PostMapping
    public ResponseEntity<AlunoResponseDto> createAluno(@RequestBody AlunosDto alunoDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(alunosServices.saveDto(alunoDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AlunoResponseDto> updateAluno(@PathVariable UUID id, @RequestBody AlunosDto alunoDto) {
        return ResponseEntity.ok(alunosServices.updateDto(id, alunoDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAluno(@PathVariable UUID id) {
        alunosServices.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}