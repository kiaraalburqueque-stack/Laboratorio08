package com.tecsup.service;

import com.tecsup.model.Categoria;
import com.tecsup.repository.CategoriaRepository;
import com.tecsup.exception.RecursoNoEncontradoException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CategoriaService {
    private final CategoriaRepository repo;

    public CategoriaService(CategoriaRepository repo) { this.repo = repo; }

    @Transactional(readOnly = true)
    public List<Categoria> listar() { return repo.findAll(); }

    @Transactional(readOnly = true)
    public Categoria obtener(Long id) {
        return repo.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("Categoria", id));
    }

    public Categoria guardar(Categoria entidad) {
        entidad.setId(null);
        return repo.save(entidad);
    }

    public Categoria actualizar(Long id, Categoria cambios) {
        Categoria existente = obtener(id);
        existente.setNombre(cambios.getNombre());
        existente.setDescripcion(cambios.getDescripcion());
        return repo.save(existente);
    }

    public void eliminar(Long id) {
        obtener(id);
        repo.deleteById(id);
    }
}
