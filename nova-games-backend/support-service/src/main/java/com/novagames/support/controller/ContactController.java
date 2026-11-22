package com.novagames.support.controller;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.novagames.common.exception.ApiError;
import com.novagames.support.dto.ContactDtos.*;
import com.novagames.support.service.ContactService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import jakarta.validation.Valid;
import java.util.*;
@Tag(name = "Contacto", description = "Mensajes de contacto enviados por los usuarios y su gestión.")
@RestController public class ContactController {
    private final ContactService service;
    public ContactController(ContactService s) {
        service=s;
    }
    @Operation(summary = "Enviar un mensaje de contacto", description = "Registra un mensaje de contacto (nombre, correo, asunto y mensaje) y devuelve un comprobante. Es un endpoint público.")
    @SecurityRequirements
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Mensaje recibido correctamente.", content = @Content(schema = @Schema(implementation = Receipt.class))),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida: campos que no cumplen las validaciones o parámetros mal formados (VALIDATION_ERROR / INVALID_REQUEST).", content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PostMapping("/api/contacto") @ResponseStatus(HttpStatus.CREATED) public Receipt create(@Valid @RequestBody Input d) {
        return service.create(d);
    }
    @Operation(summary = "Listar mensajes de contacto", description = "Devuelve todos los mensajes de contacto recibidos. Requiere rol ADMIN.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado de mensajes.", content = @Content(array = @ArraySchema(schema = @Schema(implementation = View.class)))),
        @ApiResponse(responseCode = "401", description = "Token ausente, inválido o expirado (UNAUTHORIZED).", content = @Content(schema = @Schema(implementation = ApiError.class))),
        @ApiResponse(responseCode = "403", description = "El usuario autenticado no tiene permisos para esta acción (ACCESS_DENIED).", content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping("/api/admin/contacto") @PreAuthorize("hasRole('ADMIN')") public List<View> list() {
        return service.list();
    }
    @Operation(summary = "Marcar un mensaje como resuelto o pendiente", description = "Actualiza el estado (resuelto) de un mensaje de contacto. Requiere rol ADMIN.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Mensaje actualizado.", content = @Content(schema = @Schema(implementation = View.class))),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida: campos que no cumplen las validaciones o parámetros mal formados (VALIDATION_ERROR / INVALID_REQUEST).", content = @Content(schema = @Schema(implementation = ApiError.class))),
        @ApiResponse(responseCode = "401", description = "Token ausente, inválido o expirado (UNAUTHORIZED).", content = @Content(schema = @Schema(implementation = ApiError.class))),
        @ApiResponse(responseCode = "403", description = "El usuario autenticado no tiene permisos para esta acción (ACCESS_DENIED).", content = @Content(schema = @Schema(implementation = ApiError.class))),
        @ApiResponse(responseCode = "404", description = "El mensaje no existe (MESSAGE_NOT_FOUND).", content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PutMapping("/api/admin/contacto/{id}") @PreAuthorize("hasRole('ADMIN')") public View update(@PathVariable Long id,@Valid @RequestBody Status d) {
        return service.update(id,d);
    }
}
