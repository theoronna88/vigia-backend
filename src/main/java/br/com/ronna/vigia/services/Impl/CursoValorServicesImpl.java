package br.com.ronna.vigia.services.Impl;

import br.com.ronna.vigia.enums.CursoValorStatus;
import br.com.ronna.vigia.exceptions.NotFoundException;
import br.com.ronna.vigia.model.CursoValor;
import br.com.ronna.vigia.repository.CursoValorRepository;
import br.com.ronna.vigia.repository.CursosRepository;
import br.com.ronna.vigia.services.CursoValorServices;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static org.springframework.util.ClassUtils.isPresent;

@Service
public class CursoValorServicesImpl implements CursoValorServices {

    private final CursoValorRepository repo;
    private final CursosRepository cursoRepo;

    public CursoValorServicesImpl(CursoValorRepository repo, CursosRepository cursoRepo) {
        this.repo = repo;
        this.cursoRepo = cursoRepo;
    }

    @Override
    public void atribuirValorAoCurso(CursoValor cursoValor) {
        if (!cursoRepo.existsById(cursoValor.getCurso().getId())) {
            throw new NotFoundException("Erro: Curso não encontrado");
        }

        System.out.println("Atribuindo valor ao curso: " + cursoValor.getCurso().getNome() +
                " com valor: " + cursoValor.getValor() +
                " a partir de: " + cursoValor.getInicioVigencia());

        // Expirar valores antigos
        expirarValoresAntigos(cursoValor);

        // Salvar novo valor
        cursoValor.setStatus(CursoValorStatus.ATIVO);

        System.out.println("atribuindo valores: " + cursoValor.toString());
        repo.save(cursoValor);
    }

    @Override
    public void expirarValoresAntigos(CursoValor cursoValor) {
        // Recebe o novo valor, e coloca todos os outros valores com fimVigencia como a data atual
        var valorAtual = repo.findCursoValorsByCursoAndFimVigenciaIsNull(cursoValor.getCurso());
        if (valorAtual.isPresent()) {
            var valor = valorAtual.get();
            valor.setFimVigencia(cursoValor.getInicioVigencia().minusDays(1));
            valor.setStatus(CursoValorStatus.INATIVO);
            repo.save(valor);
        }
    }

    @Override
    public CursoValor getValorAtualDoCurso(UUID cursoId) {
        var curso = cursoRepo.findById(cursoId);
        if (!curso.isPresent()) {
            throw new NotFoundException("Erro: Curso não encontrado");
        }

        var valorAtual = repo.findCursoValorsByCursoAndFimVigenciaIsNull(curso.get());
        if (!valorAtual.isPresent()) {

            var valorZero = new CursoValor();
            valorZero.setCurso(curso.get());
            valorZero.setValor(0.0);
            return valorZero;
        }
        return valorAtual.get();
    }
}
