package com.novagames.community.security.jwt;
import org.springframework.stereotype.Service;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import java.time.Instant;
import java.util.*;
@Service
public class JwtService {
    private final JwtDecoder decoder;
    public JwtService(JwtDecoder decoder) { this.decoder=decoder; }

    // El decoder verifica firma RS256, expiración e issuer antes de entregar los claims.
    public Jwt validateToken(String token) { return decoder.decode(token); }
    public UUID extractUserId(Jwt jwt) { return UUID.fromString(jwt.getSubject()); }
    public String extractUsername(Jwt jwt) { return jwt.getClaimAsString("username"); }
    public List<String> extractRoles(Jwt jwt) { return jwt.getClaimAsStringList("roles"); }
}
