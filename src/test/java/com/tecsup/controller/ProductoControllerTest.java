package com.tecsup.controller;

import com.tecsup.model.Producto;
import com.tecsup.repository.ProductoRepository;
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
class ProductoControllerTest {
    @Autowired private MockMvc mvc;
    @Autowired private ProductoRepository repo;
    private static final String URL = "/api/productos";
    private static final String JSON = "{\"nombre\":\"Monitor\",\"precio\":500,\"stock\":10}";
    private static final String CAMBIOS = "{\"nombre\":\"Laptop\",\"precio\":3000,\"stock\":5}";
    private static final String INVALIDO = "{\"nombre\":\"\",\"precio\":-1,\"stock\":-1}";

    private Long crear() { return repo.saveAndFlush(new Producto(null, "Monitor", 500, 10)).getId(); }

    @Test void guardar() throws Exception {
        mvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(JSON))
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"))
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.nombre").value("Monitor"));
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
            .andExpect(jsonPath("$.nombre").value("Laptop"));
        var actualizado = repo.findById(id).orElseThrow();
        assertEquals("Laptop", actualizado.getNombre());
        assertEquals(3000.0, actualizado.getPrecio());
        assertEquals(5, actualizado.getStock());
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
        assertEquals("Monitor", repo.findById(id).orElseThrow().getNombre());
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
