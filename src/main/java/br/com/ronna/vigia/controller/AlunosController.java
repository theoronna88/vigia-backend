package br.com.ronna.vigia.controller;

import br.com.ronna.vigia.dtos.AlunosDto;
import br.com.ronna.vigia.dtos.SearchDto;
import br.com.ronna.vigia.model.Aluno;
import br.com.ronna.vigia.services.Impl.AlunoServicesImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/alunos")
@CrossOrigin(origins = "*")
public class AlunosController {

    private AlunoServicesImpl alunosServices;

    public AlunosController(AlunoServicesImpl alunosServices) {
        this.alunosServices = alunosServices;
    }

    @GetMapping
    public ResponseEntity<List<Aluno>> getAllAlunos() {
        List<Aluno> alunos = alunosServices.findAllActive();
        return ResponseEntity.ok(alunos);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Aluno>> getAllAlunosIncludingDeleted() {
        List<Aluno> alunos = alunosServices.findAll();
        return ResponseEntity.ok(alunos);
    }

    @PostMapping("/search")
    public ResponseEntity<List<Aluno>> searchAlunosByFilter(@RequestBody SearchDto searchDto) {
        List<Aluno> alunos = alunosServices.searchByFilter(searchDto);
        return ResponseEntity.ok(alunos);
    }


    @GetMapping("/{id}")
    public ResponseEntity<Aluno> getAlunoById(@PathVariable UUID id) {
        return ResponseEntity.ok(alunosServices.findById(id).get());
    }

    @GetMapping("/lead")
    public ResponseEntity<Object> getLeadAlunos() {
        return ResponseEntity.status(HttpStatus.OK).body(alunosServices.findAlunosLead());
    }

    @PostMapping
    public ResponseEntity<Object> createAluno(@RequestBody AlunosDto alunoDto) {

        return ResponseEntity.status(HttpStatus.CREATED).body(alunosServices.save(alunoDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Aluno> updateAluno(@PathVariable UUID id, @RequestBody AlunosDto alunoDto) {
        return ResponseEntity.ok(alunosServices.update(id, alunoDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAluno(@PathVariable UUID id) {
        alunosServices.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}