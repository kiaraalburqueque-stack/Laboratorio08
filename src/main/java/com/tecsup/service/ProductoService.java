package com.tecsup.service;

import com.tecsup.model.Producto;
import com.tecsup.repository.ProductoRepository;
import com.tecsup.exception.RecursoNoEncontradoException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProductoService {
    private final ProductoRepository repo;

    public ProductoService(ProductoRepository repo) { this.repo = repo; }

    @Transactional(readOnly = true)
    public List<Producto> listar() { return repo.findAll(); }

    @Transactional(readOnly = true)
    public Producto obtener(Long id) {
        return repo.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("Producto", id));
    }

    public Producto guardar(Producto entidad) {
        entidad.setId(null);
        return repo.save(entidad);
    }

    public Producto actualizar(Long id, Producto cambios) {
        Producto existente = obtener(id);
        existente.setNombre(cambios.getNombre());
        existente.setPrecio(cambios.getPrecio());
        existente.setStock(cambios.getStock());
        return repo.save(existente);
    }

    public void eliminar(Long id) {
        obtener(id);
        repo.deleteById(id);
    }
}
