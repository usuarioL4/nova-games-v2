package com.novagames.support.controller;
import com.novagames.support.dto.ContactDtos.*;
import com.novagames.support.service.ContactService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import jakarta.validation.Valid;
import java.util.*;
@RestController public class ContactController {
    private final ContactService service;
    public ContactController(ContactService s) {
        service=s;
    }
    @PostMapping("/api/contacto") @ResponseStatus(HttpStatus.CREATED) public Receipt create(@Valid @RequestBody Input d) {
        return service.create(d);
    }
    @GetMapping("/api/admin/contacto") @PreAuthorize("hasRole('ADMIN')") public List<View> list() {
        return service.list();
    }
    @PutMapping("/api/admin/contacto/{id}") @PreAuthorize("hasRole('ADMIN')") public View update(@PathVariable Long id,@Valid @RequestBody Status d) {
        return service.update(id,d);
    }
}
