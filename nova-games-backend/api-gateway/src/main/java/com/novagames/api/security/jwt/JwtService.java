package com.novagames.api.security.jwt;
import org.springframework.stereotype.Service;
import org.springframework.security.oauth2.jwt.*;
import reactor.core.publisher.Mono;
@Service public class JwtService {
    private final ReactiveJwtDecoder decoder;
    public JwtService(ReactiveJwtDecoder decoder){this.decoder=decoder;}
    public Mono<Jwt> validateToken(String token){return decoder.decode(token);}
}
