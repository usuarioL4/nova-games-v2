package com.novagames.commerce.controller;
import com.novagames.commerce.dto.CommerceDtos.*;
import com.novagames.commerce.service.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.HttpStatus;
import jakarta.validation.Valid;
import java.util.*;
@RestController public class CommerceController {
    private final CartService cart;
    private final PurchaseService purchases;
    public CommerceController(CartService c,PurchaseService p) {
        cart=c;
        purchases=p;
    }
    @GetMapping("/api/carrito") public List<CartLine> cart(@AuthenticationPrincipal Jwt jwt) {
        return cart.list(jwt.getSubject());
    }
    @PutMapping("/api/carrito/items") @ResponseStatus(HttpStatus.NO_CONTENT) public void put(@AuthenticationPrincipal Jwt jwt,@Valid @RequestBody Line d) {
        cart.put(jwt.getSubject(),d);
    }
    @DeleteMapping("/api/carrito/items/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@AuthenticationPrincipal Jwt jwt,@PathVariable Long id) {
        cart.delete(jwt.getSubject(),id);
    }
    @PostMapping("/api/pedidos") @ResponseStatus(HttpStatus.CREATED) public OrderView buy(@AuthenticationPrincipal Jwt jwt,@RequestHeader("Idempotency-Key") UUID id,@Valid @RequestBody Checkout d)throws Exception {
        return purchases.checkout(jwt.getSubject(),jwt.getTokenValue(),id,d);
    }
    @GetMapping("/api/pedidos") public List<OrderView> orders(@AuthenticationPrincipal Jwt jwt) {
        return purchases.list(jwt.getSubject());
    }
    @GetMapping("/api/pedidos/{id}") public OrderView order(@AuthenticationPrincipal Jwt jwt,@PathVariable UUID id) {
        return purchases.get(jwt.getSubject(),id);
    }
    @GetMapping("/api/admin/pedidos") @PreAuthorize("hasRole('ADMIN')") public List<OrderView> all() {
        return purchases.all();
    }
}
