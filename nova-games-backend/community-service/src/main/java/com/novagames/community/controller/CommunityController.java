package com.novagames.community.controller;
import com.novagames.community.dto.CommunityDtos.*;
import com.novagames.community.service.CommunityService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import java.util.*;
@RestController @RequestMapping("/api/comunidad") public class CommunityController {
    private final CommunityService service;
    public CommunityController(CommunityService s) {
        service=s;
    }
    @GetMapping public List<View> list() {
        return service.list();
    }
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public View create(@AuthenticationPrincipal Jwt jwt,@Valid @RequestBody Input d) {
        return service.create(jwt.getSubject(),jwt.getClaimAsString("username"),d);
    }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable Long id,@AuthenticationPrincipal Jwt jwt) {
        service.delete(id,jwt.getSubject(),jwt.getClaimAsStringList("roles").contains("ADMIN"));
    }
}
