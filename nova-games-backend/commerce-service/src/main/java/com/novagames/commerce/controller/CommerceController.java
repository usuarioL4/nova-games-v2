package com.novagames.commerce.controller;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.novagames.common.exception.ApiError;
import com.novagames.commerce.dto.CommerceDtos.*;
import com.novagames.commerce.service.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.HttpStatus;
import jakarta.validation.Valid;
import java.util.*;
@Tag(name = "Comercio", description = "Carrito de compras y pedidos del usuario autenticado.")
@RestController public class CommerceController {
    private final CartService cart;
    private final PurchaseService purchases;
    public CommerceController(CartService c,PurchaseService p) {
        cart=c;
        purchases=p;
    }
    @Operation(summary = "Obtener el carrito", description = "Devuelve las líneas del carrito del usuario autenticado con precio y stock actualizados.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Líneas del carrito (puede estar vacío).", content = @Content(array = @ArraySchema(schema = @Schema(implementation = CartLine.class)))),
        @ApiResponse(responseCode = "401", description = "Token ausente, inválido o expirado (UNAUTHORIZED).", content = @Content(schema = @Schema(implementation = ApiError.class))),
        @ApiResponse(responseCode = "503", description = "El catálogo no está disponible (SERVICE_UNAVAILABLE).", content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping("/api/carrito") public List<CartLine> cart(@AuthenticationPrincipal Jwt jwt) {
        return cart.list(jwt.getSubject());
    }
    @Operation(summary = "Agregar o actualizar un producto del carrito", description = "Establece la cantidad de un producto en el carrito del usuario autenticado. Valida que exista y que haya stock suficiente.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Carrito actualizado; no devuelve contenido."),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida: campos que no cumplen las validaciones o parámetros mal formados (VALIDATION_ERROR / INVALID_REQUEST).", content = @Content(schema = @Schema(implementation = ApiError.class))),
        @ApiResponse(responseCode = "401", description = "Token ausente, inválido o expirado (UNAUTHORIZED).", content = @Content(schema = @Schema(implementation = ApiError.class))),
        @ApiResponse(responseCode = "404", description = "El producto no existe (PRODUCT_NOT_FOUND).", content = @Content(schema = @Schema(implementation = ApiError.class))),
        @ApiResponse(responseCode = "409", description = "No hay suficiente stock (INSUFFICIENT_STOCK).", content = @Content(schema = @Schema(implementation = ApiError.class))),
        @ApiResponse(responseCode = "503", description = "El catálogo no está disponible (SERVICE_UNAVAILABLE).", content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PutMapping("/api/carrito/items") @ResponseStatus(HttpStatus.NO_CONTENT) public void put(@AuthenticationPrincipal Jwt jwt,@Valid @RequestBody Line d) {
        cart.put(jwt.getSubject(),d);
    }
    @Operation(summary = "Quitar un producto del carrito", description = "Elimina del carrito del usuario autenticado la línea del producto indicado.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Producto quitado del carrito; no devuelve contenido."),
        @ApiResponse(responseCode = "400", description = "El id no es un número válido (INVALID_REQUEST).", content = @Content(schema = @Schema(implementation = ApiError.class))),
        @ApiResponse(responseCode = "401", description = "Token ausente, inválido o expirado (UNAUTHORIZED).", content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @DeleteMapping("/api/carrito/items/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@AuthenticationPrincipal Jwt jwt,@PathVariable Long id) {
        cart.delete(jwt.getSubject(),id);
    }
    @Operation(summary = "Confirmar una compra", description = "Crea un pedido a partir de los items enviados y descuenta el stock. Requiere la cabecera Idempotency-Key (UUID): reintentar con la misma clave y los mismos items devuelve el mismo pedido sin descontar dos veces.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Pedido confirmado.", content = @Content(schema = @Schema(implementation = OrderView.class))),
        @ApiResponse(responseCode = "400", description = "Items inválidos, cantidad superior a 1000 (INVALID_QUANTITY) o cabecera Idempotency-Key ausente o inválida.", content = @Content(schema = @Schema(implementation = ApiError.class))),
        @ApiResponse(responseCode = "401", description = "Token ausente, inválido o expirado (UNAUTHORIZED).", content = @Content(schema = @Schema(implementation = ApiError.class))),
        @ApiResponse(responseCode = "404", description = "Alguno de los productos no existe (PRODUCT_NOT_FOUND).", content = @Content(schema = @Schema(implementation = ApiError.class))),
        @ApiResponse(responseCode = "409", description = "Stock insuficiente (INSUFFICIENT_STOCK) o la clave ya se usó con otros datos (IDEMPOTENCY_CONFLICT).", content = @Content(schema = @Schema(implementation = ApiError.class))),
        @ApiResponse(responseCode = "503", description = "El catálogo no está disponible (SERVICE_UNAVAILABLE).", content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PostMapping("/api/pedidos") @ResponseStatus(HttpStatus.CREATED) public OrderView buy(@AuthenticationPrincipal Jwt jwt,@RequestHeader("Idempotency-Key") UUID id,@Valid @RequestBody Checkout d)throws Exception {
        return purchases.checkout(jwt.getSubject(),jwt.getTokenValue(),id,d);
    }
    @Operation(summary = "Listar mis pedidos", description = "Devuelve los pedidos del usuario autenticado, del más reciente al más antiguo.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado de pedidos (puede estar vacío).", content = @Content(array = @ArraySchema(schema = @Schema(implementation = OrderView.class)))),
        @ApiResponse(responseCode = "401", description = "Token ausente, inválido o expirado (UNAUTHORIZED).", content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping("/api/pedidos") public List<OrderView> orders(@AuthenticationPrincipal Jwt jwt) {
        return purchases.list(jwt.getSubject());
    }
    @Operation(summary = "Obtener un pedido", description = "Devuelve un pedido del usuario autenticado por su identificador.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pedido encontrado.", content = @Content(schema = @Schema(implementation = OrderView.class))),
        @ApiResponse(responseCode = "400", description = "El id no es un UUID válido (INVALID_REQUEST).", content = @Content(schema = @Schema(implementation = ApiError.class))),
        @ApiResponse(responseCode = "401", description = "Token ausente, inválido o expirado (UNAUTHORIZED).", content = @Content(schema = @Schema(implementation = ApiError.class))),
        @ApiResponse(responseCode = "404", description = "El pedido no existe o no pertenece al usuario (ORDER_NOT_FOUND).", content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping("/api/pedidos/{id}") public OrderView order(@AuthenticationPrincipal Jwt jwt,@PathVariable UUID id) {
        return purchases.get(jwt.getSubject(),id);
    }
    @Operation(summary = "Listar todos los pedidos", description = "Devuelve los pedidos de todos los usuarios, del más reciente al más antiguo. Requiere rol ADMIN.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado de pedidos.", content = @Content(array = @ArraySchema(schema = @Schema(implementation = OrderView.class)))),
        @ApiResponse(responseCode = "401", description = "Token ausente, inválido o expirado (UNAUTHORIZED).", content = @Content(schema = @Schema(implementation = ApiError.class))),
        @ApiResponse(responseCode = "403", description = "El usuario autenticado no tiene permisos para esta acción (ACCESS_DENIED).", content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping("/api/admin/pedidos") @PreAuthorize("hasRole('ADMIN')") public List<OrderView> all() {
        return purchases.all();
    }
}
