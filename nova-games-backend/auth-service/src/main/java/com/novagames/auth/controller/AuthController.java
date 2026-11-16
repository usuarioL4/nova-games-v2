package com.novagames.auth.controller;
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
@RestController public class AuthController {
    private final AuthService service;
    private final RSAKey key;
    public AuthController(AuthService s,RSAKey k) {
        service=s;
        key=k;
    }
    @PostMapping("/api/auth/register") @ResponseStatus(HttpStatus.CREATED) public UserView register(@Valid @RequestBody Register r) {
        return service.register(r);
    }
    @PostMapping("/api/auth/login") public Token login(@Valid @RequestBody Login r) {
        return service.login(r);
    }
    @GetMapping("/api/auth/me") public UserView me(@AuthenticationPrincipal Jwt jwt) {
        return service.me(jwt.getSubject());
    }
    @GetMapping("/api/admin/usuarios") @PreAuthorize("hasRole('ADMIN')") public List<UserView> users() {
        return service.users();
    }
    @GetMapping("/.well-known/jwks.json") public Map<String,Object> jwks() {
        return new JWKSet(key.toPublicJWK()).toJSONObject();
    }
}
