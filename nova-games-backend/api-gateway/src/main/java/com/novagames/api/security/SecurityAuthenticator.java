package com.novagames.api.security;
import com.novagames.api.security.jwt.JwtService;
import org.springframework.stereotype.Component;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.*;
import org.springframework.security.oauth2.jwt.JwtException;
import reactor.core.publisher.Mono;
@Component public class SecurityAuthenticator implements ReactiveAuthenticationManager {
    private final JwtService jwtService;
    public SecurityAuthenticator(JwtService jwtService){this.jwtService=jwtService;}
    @Override public Mono<Authentication> authenticate(Authentication authentication){
        if(!(authentication instanceof BearerTokenAuthenticationToken bearer))return Mono.empty();
        return jwtService.validateToken(bearer.getToken()).map(jwt->{
            if(jwt.getSubject()==null || jwt.getExpiresAt()==null)throw new BadCredentialsException("Claims JWT inválidos");
            try { java.util.UUID.fromString(jwt.getSubject()); } catch(IllegalArgumentException ex){throw new BadCredentialsException("Claims JWT inválidos",ex);}
            var roles=jwt.getClaimAsStringList("roles");
            if(roles==null||roles.isEmpty()||roles.stream().anyMatch(r->!java.util.List.of("USER","ADMIN").contains(r))||jwt.getClaimAsString("username")==null)throw new BadCredentialsException("Claims JWT inválidos");
            return (Authentication)new JwtAuthenticationToken(jwt,roles.stream().map(r->new SimpleGrantedAuthority("ROLE_"+r)).toList(),jwt.getSubject());
        }).onErrorMap(JwtException.class,e->new BadCredentialsException("Token inválido o expirado",e));
    }
}
