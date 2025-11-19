package br.com.ronna.vigia.controller;

import br.com.ronna.vigia.model.CursoValor;
import br.com.ronna.vigia.services.CursoValorServices;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/curso-valor")
public class CursoValorController {

    private final CursoValorServices cursoValorService;

    public CursoValorController(CursoValorServices cursoValorService) {
        this.cursoValorService = cursoValorService;
    }

    @PostMapping("{cursoId}/valor")
    public ResponseEntity<Object> atribuirValorAoCurso(@PathVariable UUID cursoId, @Valid @RequestBody CursoValor cursoValor) {
        cursoValorService.atribuirValorAoCurso(cursoValor);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{cursoId}")
    public ResponseEntity<CursoValor> getValorAtualDoCurso(@PathVariable UUID cursoId) {
        return ResponseEntity.ok(cursoValorService.getValorAtualDoCurso(cursoId));
    }

}
