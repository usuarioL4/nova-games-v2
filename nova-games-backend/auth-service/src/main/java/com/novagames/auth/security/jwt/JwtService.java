package com.novagames.auth.security.jwt;
import org.springframework.stereotype.Service;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import java.time.Instant;
import java.util.*;
@Service
public class JwtService {
    private final JwtDecoder decoder;

    private final JwtEncoder encoder;
    private final String issuer;
    public JwtService(JwtDecoder decoder, JwtEncoder encoder, @Value("${JWT_ISSUER:novagames}") String issuer) {
        this.decoder=decoder; this.encoder=encoder; this.issuer=issuer;
    }
    public String generateToken(UUID userId, String username, String role) {
        Instant now=Instant.now();
        var claims=JwtClaimsSet.builder().issuer(issuer).subject(userId.toString())
            .issuedAt(now).expiresAt(now.plusSeconds(3600)).claim("id",userId.toString())
            .claim("username",username).claim("role",role).claim("roles",List.of(role)).build();
        return encoder.encode(JwtEncoderParameters.from(JwsHeader.with(SignatureAlgorithm.RS256).build(),claims)).getTokenValue();
    }

    // El decoder verifica firma RS256, expiración e issuer antes de entregar los claims.
    public Jwt validateToken(String token) { return decoder.decode(token); }
    public UUID extractUserId(Jwt jwt) { return UUID.fromString(jwt.getSubject()); }
    public String extractUsername(Jwt jwt) { return jwt.getClaimAsString("username"); }
    public List<String> extractRoles(Jwt jwt) { return jwt.getClaimAsStringList("roles"); }
}
