package com.novagames.auth.controller;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.novagames.common.exception.ApiError;
import com.novagames.auth.dto.AuthDtos.*;
import com.novagames.auth.service.AuthService;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.*;
@Tag(name = "Autenticación", description = "Registro, inicio de sesión y consulta de usuarios.")
@RestController public class AuthController {
    private final AuthService service;
    private final RSAKey key;
    public AuthController(AuthService s,RSAKey k) {
        service=s;
        key=k;
    }
    @Operation(summary = "Registrar un usuario", description = "Crea una cuenta con rol USER. Valida que las contraseñas coincidan, que se confirme la mayoría de edad y que correo, nombre de usuario y teléfono no estén registrados. Solo se aceptan correos Duoc o Gmail. Es un endpoint público.")
    @SecurityRequirements
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Usuario registrado correctamente.", content = @Content(schema = @Schema(implementation = UserView.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos: validaciones de campos, contraseñas distintas (PASSWORD_MISMATCH), contraseña demasiado larga (INVALID_PASSWORD) o dominio de correo no permitido (INVALID_EMAIL_DOMAIN).", content = @Content(schema = @Schema(implementation = ApiError.class))),
        @ApiResponse(responseCode = "409", description = "El correo, el nombre de usuario o el teléfono ya existen (EMAIL_ALREADY_EXISTS / USERNAME_ALREADY_EXISTS / PHONE_ALREADY_EXISTS).", content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PostMapping("/api/auth/register") @ResponseStatus(HttpStatus.CREATED) public UserView register(@Valid @RequestBody Register r) {
        return service.register(r);
    }
    @Operation(summary = "Iniciar sesión", description = "Valida las credenciales y devuelve un token JWT (accessToken) junto con los datos del usuario. Es un endpoint público.")
    @SecurityRequirements
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Inicio de sesión correcto.", content = @Content(schema = @Schema(implementation = Token.class))),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida: campos que no cumplen las validaciones o parámetros mal formados (VALIDATION_ERROR / INVALID_REQUEST).", content = @Content(schema = @Schema(implementation = ApiError.class))),
        @ApiResponse(responseCode = "401", description = "Correo o contraseña incorrectos (INVALID_CREDENTIALS).", content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PostMapping("/api/auth/login") public Token login(@Valid @RequestBody Login r) {
        return service.login(r);
    }
    @Operation(summary = "Obtener el usuario autenticado", description = "Devuelve los datos del usuario dueño del token JWT enviado.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Datos del usuario autenticado.", content = @Content(schema = @Schema(implementation = UserView.class))),
        @ApiResponse(responseCode = "401", description = "Token ausente, inválido o expirado (UNAUTHORIZED).", content = @Content(schema = @Schema(implementation = ApiError.class))),
        @ApiResponse(responseCode = "404", description = "El usuario del token ya no existe (USER_NOT_FOUND).", content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping("/api/auth/me") public UserView me(@AuthenticationPrincipal Jwt jwt) {
        return service.me(jwt.getSubject());
    }
    @Operation(summary = "Listar usuarios", description = "Devuelve todos los usuarios registrados. Requiere rol ADMIN.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado de usuarios.", content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserView.class)))),
        @ApiResponse(responseCode = "401", description = "Token ausente, inválido o expirado (UNAUTHORIZED).", content = @Content(schema = @Schema(implementation = ApiError.class))),
        @ApiResponse(responseCode = "403", description = "El usuario autenticado no tiene permisos para esta acción (ACCESS_DENIED).", content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping("/api/admin/usuarios") @PreAuthorize("hasRole('ADMIN')") public List<UserView> users() {
        return service.users();
    }
    @Operation(summary = "Obtener las claves públicas JWK", description = "Publica el conjunto de claves públicas (JWKS) que usan el gateway y los demás servicios para validar los tokens JWT. Es un endpoint público.")
    @SecurityRequirements
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Conjunto de claves públicas en formato JWKS.", content = @Content(schema = @Schema(type = "object")))
    })
    @GetMapping("/.well-known/jwks.json") public Map<String,Object> jwks() {
        return new JWKSet(key.toPublicJWK()).toJSONObject();
    }
}
