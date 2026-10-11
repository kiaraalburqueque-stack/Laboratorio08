package com.tecsup.service;

import com.tecsup.model.Producto;
import com.tecsup.repository.ProductoRepository;
import com.tecsup.exception.RecursoNoEncontradoException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {
    @Mock private ProductoRepository repo;
    @InjectMocks private ProductoService service;

    @Test void listar() {
        when(repo.findAll()).thenReturn(List.of(new Producto(1L, "Monitor", 500, 10)));
        var resultado = service.listar();
        assertEquals(1, resultado.size());
        assertEquals("Monitor", resultado.get(0).getNombre());
        verify(repo).findAll();
    }
    @Test void guardar() {
        var entidad = new Producto(null, "Monitor", 500, 10);
        when(repo.save(entidad)).thenReturn(entidad);
        assertSame(entidad, service.guardar(entidad));
        verify(repo).save(entidad);
    }
    @Test void obtener() {
        var entidad = new Producto(1L, "Monitor", 500, 10);
        when(repo.findById(1L)).thenReturn(Optional.of(entidad));
        assertSame(entidad, service.obtener(1L));
    }
    @Test void obtenerInexistente() {
        when(repo.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RecursoNoEncontradoException.class, () -> service.obtener(99L));
    }
    @Test void actualizar() {
        var existente = new Producto(1L, "Monitor", 500, 10);
        var cambios = new Producto(null, "Laptop", 3000, 5);
        when(repo.findById(1L)).thenReturn(Optional.of(existente));
        when(repo.save(existente)).thenReturn(existente);
        var resultado = service.actualizar(1L, cambios);
        assertEquals(1L, resultado.getId());
        assertEquals(cambios.getNombre(), resultado.getNombre());
        assertEquals(cambios.getPrecio(), resultado.getPrecio());
        assertEquals(cambios.getStock(), resultado.getStock());
        verify(repo).save(existente);
    }
    @Test void actualizarInexistente() {
        when(repo.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RecursoNoEncontradoException.class,
            () -> service.actualizar(99L, new Producto(null, "Laptop", 3000, 5)));
        verify(repo, never()).save(any());
    }
    @Test void eliminar() {
        when(repo.findById(1L)).thenReturn(Optional.of(new Producto(1L, "Monitor", 500, 10)));
        service.eliminar(1L);
        verify(repo).deleteById(1L);
    }
    @Test void eliminarInexistente() {
        when(repo.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RecursoNoEncontradoException.class, () -> service.eliminar(99L));
        verify(repo, never()).deleteById(anyLong());
    }
}
