package com.novagames.catalog.controller;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.novagames.common.exception.ApiError;
import com.novagames.catalog.dto.CatalogDtos.*;
import com.novagames.catalog.service.*;
import com.novagames.common.exception.BusinessException;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.beans.factory.annotation.Value;
import jakarta.validation.Valid;
import java.util.*;
@Tag(name = "Catálogo", description = "Consulta y administración de videojuegos del catálogo.")
@RestController public class CatalogController {
    private final CatalogService service;
    private final ReservationService reservations;
    private final String internalKey;
    public CatalogController(CatalogService s,ReservationService r,@Value("${INTERNAL_KEY}") String k) {
        service=s;
        reservations=r;
        internalKey=k;
    }
    @Operation(summary = "Listar productos", description = "Devuelve los productos activos del catálogo ordenados por id. Permite filtrar opcionalmente por texto en el nombre (q), por categoría y por plataforma. Es un endpoint público.")
    @SecurityRequirements
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado de productos (puede estar vacío).", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProductView.class))))
    })
    @GetMapping("/api/productos") public List<ProductView> list(@RequestParam(required=false) String q,@RequestParam(required=false) String categoria,@RequestParam(required=false) String plataforma) {
        return service.list(q,categoria,plataforma);
    }
    @Operation(summary = "Obtener un producto por id", description = "Devuelve toda la información de un producto activo: nombre, categoría, precio, stock, imagen, descripción y plataformas. Es un endpoint público.")
    @SecurityRequirements
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Producto encontrado.", content = @Content(schema = @Schema(implementation = ProductView.class))),
        @ApiResponse(responseCode = "400", description = "El id no es un número válido (INVALID_REQUEST).", content = @Content(schema = @Schema(implementation = ApiError.class))),
        @ApiResponse(responseCode = "404", description = "No existe un producto activo con ese id (PRODUCT_NOT_FOUND).", content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping("/api/productos/{id}") public ProductView get(@PathVariable Long id) {
        return service.get(id);
    }
    @Operation(summary = "Listar categorías", description = "Devuelve, sin repetir y ordenadas alfabéticamente, las categorías de los productos activos. Es un endpoint público.")
    @SecurityRequirements
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado de categorías.", content = @Content(array = @ArraySchema(schema = @Schema(type = "string"))))
    })
    @GetMapping("/api/categorias") public List<String> categories() {
        return service.list(null,null,null).stream().map(ProductView::categoria).distinct().sorted().toList();
    }
    @Operation(summary = "Listar plataformas", description = "Devuelve, sin repetir y ordenadas alfabéticamente, las plataformas de los productos activos. Es un endpoint público.")
    @SecurityRequirements
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado de plataformas.", content = @Content(array = @ArraySchema(schema = @Schema(type = "string"))))
    })
    @GetMapping("/api/plataformas") public List<String> platforms() {
        return service.list(null,null,null).stream().flatMap(p->p.plataformas().stream()).distinct().sorted().toList();
    }
    @Operation(summary = "Crear un producto", description = "Registra un nuevo videojuego en el catálogo. Requiere rol ADMIN.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Producto creado correctamente.", content = @Content(schema = @Schema(implementation = ProductView.class))),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida: campos que no cumplen las validaciones o parámetros mal formados (VALIDATION_ERROR / INVALID_REQUEST).", content = @Content(schema = @Schema(implementation = ApiError.class))),
        @ApiResponse(responseCode = "401", description = "Token ausente, inválido o expirado (UNAUTHORIZED).", content = @Content(schema = @Schema(implementation = ApiError.class))),
        @ApiResponse(responseCode = "403", description = "El usuario autenticado no tiene permisos para esta acción (ACCESS_DENIED).", content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PostMapping("/api/productos") @ResponseStatus(HttpStatus.CREATED) @PreAuthorize("hasRole('ADMIN')") public ProductView create(@Valid @RequestBody ProductInput d) {
        return service.create(d);
    }
    @Operation(summary = "Actualizar un producto", description = "Reemplaza los datos de un producto existente y activo. Requiere rol ADMIN.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Producto actualizado correctamente.", content = @Content(schema = @Schema(implementation = ProductView.class))),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida: campos que no cumplen las validaciones o parámetros mal formados (VALIDATION_ERROR / INVALID_REQUEST).", content = @Content(schema = @Schema(implementation = ApiError.class))),
        @ApiResponse(responseCode = "401", description = "Token ausente, inválido o expirado (UNAUTHORIZED).", content = @Content(schema = @Schema(implementation = ApiError.class))),
        @ApiResponse(responseCode = "403", description = "El usuario autenticado no tiene permisos para esta acción (ACCESS_DENIED).", content = @Content(schema = @Schema(implementation = ApiError.class))),
        @ApiResponse(responseCode = "404", description = "No existe un producto activo con ese id (PRODUCT_NOT_FOUND).", content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PutMapping("/api/productos/{id}") @PreAuthorize("hasRole('ADMIN')") public ProductView update(@PathVariable Long id,@Valid @RequestBody ProductInput d) {
        return service.update(id,d);
    }
    @Operation(summary = "Eliminar un producto", description = "Desactiva un producto (eliminación lógica): deja de aparecer en el catálogo. Requiere rol ADMIN.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Producto eliminado correctamente; no devuelve contenido."),
        @ApiResponse(responseCode = "400", description = "El id no es un número válido (INVALID_REQUEST).", content = @Content(schema = @Schema(implementation = ApiError.class))),
        @ApiResponse(responseCode = "401", description = "Token ausente, inválido o expirado (UNAUTHORIZED).", content = @Content(schema = @Schema(implementation = ApiError.class))),
        @ApiResponse(responseCode = "403", description = "El usuario autenticado no tiene permisos para esta acción (ACCESS_DENIED).", content = @Content(schema = @Schema(implementation = ApiError.class))),
        @ApiResponse(responseCode = "404", description = "No existe un producto activo con ese id (PRODUCT_NOT_FOUND).", content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @DeleteMapping("/api/productos/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) @PreAuthorize("hasRole('ADMIN')") public void delete(@PathVariable Long id) {
        service.delete(id);
    }
    @Operation(summary = "Reservar stock (uso interno)", description = "Endpoint de uso interno entre servicios: descuenta el stock de los productos de un pedido de forma idempotente por pedidoId. Exige la cabecera X-Internal-Key y un usuario autenticado.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Stock reservado; devuelve el detalle y el total del pedido.", content = @Content(schema = @Schema(implementation = ReserveResult.class))),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida: campos que no cumplen las validaciones o parámetros mal formados (VALIDATION_ERROR / INVALID_REQUEST).", content = @Content(schema = @Schema(implementation = ApiError.class))),
        @ApiResponse(responseCode = "401", description = "Token ausente, inválido o expirado (UNAUTHORIZED).", content = @Content(schema = @Schema(implementation = ApiError.class))),
        @ApiResponse(responseCode = "403", description = "Clave interna incorrecta o usuario sin permisos (ACCESS_DENIED).", content = @Content(schema = @Schema(implementation = ApiError.class))),
        @ApiResponse(responseCode = "404", description = "Alguno de los productos no existe o no está disponible (PRODUCT_NOT_FOUND).", content = @Content(schema = @Schema(implementation = ApiError.class))),
        @ApiResponse(responseCode = "409", description = "Stock insuficiente (INSUFFICIENT_STOCK) o el pedidoId ya existe con otros datos (IDEMPOTENCY_CONFLICT).", content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PostMapping("/internal/reservas") @PreAuthorize("hasAnyRole('USER','ADMIN')") public ReserveResult reserve(@RequestHeader("X-Internal-Key") String key,@AuthenticationPrincipal Jwt jwt,@Valid @RequestBody ReserveRequest d)throws Exception {
        if(!java.security.MessageDigest.isEqual(internalKey.getBytes(java.nio.charset.StandardCharsets.UTF_8),key.getBytes(java.nio.charset.StandardCharsets.UTF_8)))throw new BusinessException(403,"ACCESS_DENIED","Acceso interno denegado");
        return reservations.reserve(jwt.getSubject(),d);
    }
}
