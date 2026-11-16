package com.novagames.catalog.dto;
import java.math.BigDecimal;
import java.util.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
public class CatalogDtos {
    public record ProductInput(@NotBlank @Size(max=200) String nombre,@NotBlank @Size(max=100) String categoria,@NotNull @PositiveOrZero @Digits(integer=12,fraction=0) BigDecimal precio,@NotNull @PositiveOrZero Integer stock,@NotBlank @Size(max=500) String imagen,@NotBlank @Size(max=2000) String descripcion,@NotEmpty Set<@NotBlank String> plataformas) {
    }
    public record ProductView(Long id,String nombre,String categoria,BigDecimal precio,int stock,String imagen,String descripcion,Set<String> plataformas) {
    }
    public record Line(@NotNull @Positive Long productoId,@NotNull @Min(1) @Max(1000) Integer cantidad) {
    }
    public record ReserveRequest(@NotNull UUID pedidoId,@NotEmpty @Size(max=50) List<@Valid Line> items) {
    }
    public record ReservedLine(Long productoId,String nombre,BigDecimal precio,int cantidad,BigDecimal subtotal) {
    }
    public record ReserveResult(UUID pedidoId,List<ReservedLine> items,BigDecimal total) {
    }
}
