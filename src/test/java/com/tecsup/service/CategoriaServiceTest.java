package com.tecsup.service;

import com.tecsup.model.Categoria;
import com.tecsup.repository.CategoriaRepository;
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
class CategoriaServiceTest {
    @Mock private CategoriaRepository repo;
    @InjectMocks private CategoriaService service;

    @Test void listar() {
        when(repo.findAll()).thenReturn(List.of(new Categoria(1L, "Tecnologia", "Equipos")));
        var resultado = service.listar();
        assertEquals(1, resultado.size());
        assertEquals("Tecnologia", resultado.get(0).getNombre());
        verify(repo).findAll();
    }
    @Test void guardar() {
        var entidad = new Categoria(null, "Tecnologia", "Equipos");
        when(repo.save(entidad)).thenReturn(entidad);
        assertSame(entidad, service.guardar(entidad));
        verify(repo).save(entidad);
    }
    @Test void obtener() {
        var entidad = new Categoria(1L, "Tecnologia", "Equipos");
        when(repo.findById(1L)).thenReturn(Optional.of(entidad));
        assertSame(entidad, service.obtener(1L));
    }
    @Test void obtenerInexistente() {
        when(repo.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RecursoNoEncontradoException.class, () -> service.obtener(99L));
    }
    @Test void actualizar() {
        var existente = new Categoria(1L, "Tecnologia", "Equipos");
        var cambios = new Categoria(null, "Oficina", "Utiles");
        when(repo.findById(1L)).thenReturn(Optional.of(existente));
        when(repo.save(existente)).thenReturn(existente);
        var resultado = service.actualizar(1L, cambios);
        assertEquals(1L, resultado.getId());
        assertEquals(cambios.getNombre(), resultado.getNombre());
        assertEquals(cambios.getDescripcion(), resultado.getDescripcion());
        verify(repo).save(existente);
    }
    @Test void actualizarInexistente() {
        when(repo.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RecursoNoEncontradoException.class,
            () -> service.actualizar(99L, new Categoria(null, "Oficina", "Utiles")));
        verify(repo, never()).save(any());
    }
    @Test void eliminar() {
        when(repo.findById(1L)).thenReturn(Optional.of(new Categoria(1L, "Tecnologia", "Equipos")));
        service.eliminar(1L);
        verify(repo).deleteById(1L);
    }
    @Test void eliminarInexistente() {
        when(repo.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RecursoNoEncontradoException.class, () -> service.eliminar(99L));
        verify(repo, never()).deleteById(anyLong());
    }
}
