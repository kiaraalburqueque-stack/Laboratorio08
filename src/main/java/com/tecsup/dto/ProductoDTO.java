package com.tecsup.dto;

import jakarta.validation.constraints.*;

public class ProductoDTO {
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100)
    private String nombre;
    @NotNull
    @Positive(message = "El precio debe ser mayor que cero")
    private Double precio;
    @NotNull
    @PositiveOrZero(message = "El stock no puede ser negativo")
    private Integer stock;
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public Double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
}
