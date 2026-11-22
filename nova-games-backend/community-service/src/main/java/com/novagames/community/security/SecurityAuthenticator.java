package com.novagames.community.security;
import com.novagames.community.security.jwt.JwtService;
import org.springframework.stereotype.Component;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.oauth2.jwt.JwtException;
import java.util.List;
@Component
public class SecurityAuthenticator {
    private final JwtService jwtService;
    public SecurityAuthenticator(JwtService jwtService) { this.jwtService=jwtService; }
    public Authentication authenticate(String token) {
        try {
            var jwt=jwtService.validateToken(token);
            if(jwt.getSubject()==null || jwt.getExpiresAt()==null)throw new BadCredentialsException("Claims JWT inválidos");
            jwtService.extractUserId(jwt);
            var roles=jwtService.extractRoles(jwt);
            if(roles==null || roles.isEmpty() || roles.stream().anyMatch(r->!List.of("USER","ADMIN").contains(r)) || jwtService.extractUsername(jwt)==null)
                throw new BadCredentialsException("Claims JWT inválidos");
            var authorities=roles.stream().map(r->new SimpleGrantedAuthority("ROLE_"+r)).toList();
            // Se conserva Jwt como principal para @AuthenticationPrincipal Jwt.
            return new JwtAuthenticationToken(jwt,authorities,jwt.getSubject());
        } catch(JwtException | IllegalArgumentException e) {
            throw new BadCredentialsException("Token inválido o expirado",e);
        }
    }
}
