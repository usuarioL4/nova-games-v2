package com.novagames.community.controller;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.novagames.common.exception.ApiError;
import com.novagames.community.dto.CommunityDtos.*;
import com.novagames.community.service.CommunityService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import java.util.*;
@Tag(name = "Comunidad", description = "Comentarios de la comunidad de jugadores.")
@RestController @RequestMapping("/api/comunidad") public class CommunityController {
    private final CommunityService service;
    public CommunityController(CommunityService s) {
        service=s;
    }
    @Operation(summary = "Listar comentarios", description = "Devuelve los comentarios publicados por la comunidad. Es un endpoint público.")
    @SecurityRequirements
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado de comentarios (puede estar vacío).", content = @Content(array = @ArraySchema(schema = @Schema(implementation = View.class))))
    })
    @GetMapping public List<View> list() {
        return service.list();
    }
    @Operation(summary = "Publicar un comentario", description = "Publica un comentario a nombre del usuario autenticado.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Comentario publicado.", content = @Content(schema = @Schema(implementation = View.class))),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida: campos que no cumplen las validaciones o parámetros mal formados (VALIDATION_ERROR / INVALID_REQUEST).", content = @Content(schema = @Schema(implementation = ApiError.class))),
        @ApiResponse(responseCode = "401", description = "Token ausente, inválido o expirado (UNAUTHORIZED).", content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public View create(@AuthenticationPrincipal Jwt jwt,@Valid @RequestBody Input d) {
        return service.create(jwt.getSubject(),jwt.getClaimAsString("username"),d);
    }
    @Operation(summary = "Eliminar un comentario", description = "Elimina un comentario. Solo puede hacerlo su autor o un usuario con rol ADMIN.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Comentario eliminado; no devuelve contenido."),
        @ApiResponse(responseCode = "400", description = "El id no es un número válido (INVALID_REQUEST).", content = @Content(schema = @Schema(implementation = ApiError.class))),
        @ApiResponse(responseCode = "401", description = "Token ausente, inválido o expirado (UNAUTHORIZED).", content = @Content(schema = @Schema(implementation = ApiError.class))),
        @ApiResponse(responseCode = "403", description = "No eres el autor del comentario ni administrador (ACCESS_DENIED).", content = @Content(schema = @Schema(implementation = ApiError.class))),
        @ApiResponse(responseCode = "404", description = "El comentario no existe (COMMENT_NOT_FOUND).", content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable Long id,@AuthenticationPrincipal Jwt jwt) {
        service.delete(id,jwt.getSubject(),jwt.getClaimAsStringList("roles").contains("ADMIN"));
    }
}
