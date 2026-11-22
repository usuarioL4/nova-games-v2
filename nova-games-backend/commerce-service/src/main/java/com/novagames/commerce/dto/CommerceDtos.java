package com.novagames.commerce.dto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.*;
public class CommerceDtos {
    public record Line(@NotNull @Positive Long productoId,@NotNull @Min(1) @Max(1000) Integer cantidad) {
    }
    public record Checkout(@NotEmpty @Size(max=50) List<@Valid Line> items) {
    }
    public record Product(Long id,String nombre,BigDecimal precio,int stock,String imagen) {
    }
    public record CartLine(Long productoId,String nombre,BigDecimal precio,int cantidad,int stock,String imagen) {
    }
    public record ReservedLine(Long productoId,String nombre,BigDecimal precio,int cantidad,BigDecimal subtotal) {
    }
    public record ReserveRequest(UUID pedidoId,List<Line> items) {
    }
    public record ReserveResult(UUID pedidoId,List<ReservedLine> items,BigDecimal total) {
    }
    public record OrderView(UUID id,String estado,List<ReservedLine> items,BigDecimal total,java.time.Instant fecha) {
    }
}
