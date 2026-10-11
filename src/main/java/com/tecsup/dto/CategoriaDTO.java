package com.tecsup.dto;

import jakarta.validation.constraints.*;

public class CategoriaDTO {
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100)
    private String nombre;
    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 255)
    private String descripcion;
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}
