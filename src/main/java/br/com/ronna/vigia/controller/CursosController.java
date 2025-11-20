package br.com.ronna.vigia.controller;

import br.com.ronna.vigia.dtos.CursosDto;
import br.com.ronna.vigia.services.CursosServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/cursos")
@CrossOrigin(origins = "*")
public class CursosController {

    private final CursosServices cursosServices;

    public CursosController(CursosServices cursosServices) {
        this.cursosServices = cursosServices;
    }

    @GetMapping
    public ResponseEntity<List<CursosDto>> listarTodos() {
        List<CursosDto> cursos = cursosServices.listarTodos();
        return ResponseEntity.ok(cursos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CursosDto> buscarPorId(@PathVariable UUID id) {
        return cursosServices.buscarPorId(id)
                .map(curso -> ResponseEntity.ok(curso))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/lead")
    public ResponseEntity<Object> cursosLead() {
        List<Object> listCursos = cursosServices.cursosLead();
        return ResponseEntity.status(HttpStatus.OK).body(listCursos);
    }

    @PostMapping
    public ResponseEntity<CursosDto> criar(@RequestBody CursosDto cursosDto) {
        try {
            CursosDto cursoSalvo = cursosServices.salvar(cursosDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(cursoSalvo);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<CursosDto> atualizar(@PathVariable UUID id, @RequestBody CursosDto cursosDto) {
        return cursosServices.atualizar(id, cursosDto)
                .map(curso -> ResponseEntity.ok(curso))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        if (cursosServices.deletar(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
