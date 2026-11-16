package com.novagames.catalog.controller;
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
@RestController public class CatalogController {
    private final CatalogService service;
    private final ReservationService reservations;
    private final String internalKey;
    public CatalogController(CatalogService s,ReservationService r,@Value("${INTERNAL_KEY}") String k) {
        service=s;
        reservations=r;
        internalKey=k;
    }
    @GetMapping("/api/productos") public List<ProductView> list(@RequestParam(required=false) String q,@RequestParam(required=false) String categoria,@RequestParam(required=false) String plataforma) {
        return service.list(q,categoria,plataforma);
    }
    @GetMapping("/api/productos/{id}") public ProductView get(@PathVariable Long id) {
        return service.get(id);
    }
    @GetMapping("/api/categorias") public List<String> categories() {
        return service.list(null,null,null).stream().map(ProductView::categoria).distinct().sorted().toList();
    }
    @GetMapping("/api/plataformas") public List<String> platforms() {
        return service.list(null,null,null).stream().flatMap(p->p.plataformas().stream()).distinct().sorted().toList();
    }
    @PostMapping("/api/productos") @ResponseStatus(HttpStatus.CREATED) @PreAuthorize("hasRole('ADMIN')") public ProductView create(@Valid @RequestBody ProductInput d) {
        return service.create(d);
    }
    @PutMapping("/api/productos/{id}") @PreAuthorize("hasRole('ADMIN')") public ProductView update(@PathVariable Long id,@Valid @RequestBody ProductInput d) {
        return service.update(id,d);
    }
    @DeleteMapping("/api/productos/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) @PreAuthorize("hasRole('ADMIN')") public void delete(@PathVariable Long id) {
        service.delete(id);
    }
    @PostMapping("/internal/reservas") @PreAuthorize("hasAnyRole('USER','ADMIN')") public ReserveResult reserve(@RequestHeader("X-Internal-Key") String key,@AuthenticationPrincipal Jwt jwt,@Valid @RequestBody ReserveRequest d)throws Exception {
        if(!java.security.MessageDigest.isEqual(internalKey.getBytes(java.nio.charset.StandardCharsets.UTF_8),key.getBytes(java.nio.charset.StandardCharsets.UTF_8)))throw new BusinessException(403,"ACCESS_DENIED","Acceso interno denegado");
        return reservations.reserve(jwt.getSubject(),d);
    }
}
