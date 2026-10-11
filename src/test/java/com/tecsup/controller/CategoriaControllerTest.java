package com.tecsup.controller;

import com.tecsup.model.Categoria;
import com.tecsup.repository.CategoriaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CategoriaControllerTest {
    @Autowired private MockMvc mvc;
    @Autowired private CategoriaRepository repo;
    private static final String URL = "/api/categorias";
    private static final String JSON = "{\"nombre\":\"Tecnologia\",\"descripcion\":\"Equipos\"}";
    private static final String CAMBIOS = "{\"nombre\":\"Oficina\",\"descripcion\":\"Utiles\"}";
    private static final String INVALIDO = "{\"nombre\":\"\",\"descripcion\":\"\"}";

    private Long crear() { return repo.saveAndFlush(new Categoria(null, "Tecnologia", "Equipos")).getId(); }

    @Test void guardar() throws Exception {
        mvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(JSON))
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"))
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.nombre").value("Tecnologia"));
        assertEquals(1, repo.count());
    }
    @Test void listar() throws Exception {
        crear();
        mvc.perform(get(URL)).andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1));
    }
    @Test void obtener() throws Exception {
        Long id = crear();
        mvc.perform(get(URL + "/" + id)).andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(id.intValue()));
    }
    @Test void actualizar() throws Exception {
        Long id = crear();
        mvc.perform(put(URL + "/" + id).contentType(MediaType.APPLICATION_JSON).content(CAMBIOS))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(id.intValue()))
            .andExpect(jsonPath("$.nombre").value("Oficina"));
        var actualizado = repo.findById(id).orElseThrow();
        assertEquals("Oficina", actualizado.getNombre());
        assertEquals("Utiles", actualizado.getDescripcion());
    }
    @Test void eliminar() throws Exception {
        Long id = crear();
        mvc.perform(delete(URL + "/" + id)).andExpect(status().isNoContent());
        assertFalse(repo.existsById(id));
        mvc.perform(get(URL + "/" + id)).andExpect(status().isNotFound());
    }
    @Test void obtenerInexistente() throws Exception {
        mvc.perform(get(URL + "/999")).andExpect(status().isNotFound())
            .andExpect(jsonPath("$.mensaje").exists());
    }
    @Test void actualizarInexistente() throws Exception {
        mvc.perform(put(URL + "/999").contentType(MediaType.APPLICATION_JSON).content(CAMBIOS))
            .andExpect(status().isNotFound());
    }
    @Test void eliminarInexistente() throws Exception {
        mvc.perform(delete(URL + "/999")).andExpect(status().isNotFound());
    }
    @Test void rechazarDatosInvalidos() throws Exception {
        mvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(INVALIDO))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errores.nombre").exists());
        assertEquals(0, repo.count());
    }
    @Test void rechazarActualizacionInvalida() throws Exception {
        Long id = crear();
        mvc.perform(put(URL + "/" + id).contentType(MediaType.APPLICATION_JSON).content(INVALIDO))
            .andExpect(status().isBadRequest());
        assertEquals("Tecnologia", repo.findById(id).orElseThrow().getNombre());
    }
    @Test void rechazarCamposAusentes() throws Exception {
        mvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content("{}"))
            .andExpect(status().isBadRequest());
    }
    @Test void rechazarJsonMalformado() throws Exception {
        mvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content("{"))
            .andExpect(status().isBadRequest());
    }
}
