package com.novagames.api.config;
import org.springframework.context.annotation.*;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.http.HttpMethod;
import com.novagames.api.security.SecurityAuthenticator;
import com.novagames.api.security.filter.JwtAuthenticationFilter;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import reactor.core.publisher.Mono;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.http.MediaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;
import java.time.Instant;
@Configuration public class GatewaySecurity {
    @Bean ReactiveJwtDecoder jwtDecoder(@Value("${JWK_URL:http://localhost:8081/.well-known/jwks.json}") String jwks,@Value("${JWT_ISSUER:novagames}") String issuer){
        var decoder=NimbusReactiveJwtDecoder.withJwkSetUri(jwks).build();
        decoder.setJwtValidator(JwtValidators.createDefaultWithIssuer(issuer));
        return decoder;
    }
    @Bean SecurityWebFilterChain security(ServerHttpSecurity http,ObjectMapper mapper,SecurityAuthenticator authenticator,org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource cors) {
        ServerAuthenticationEntryPoint entry=(x,t)->error(x,mapper,401,"UNAUTHORIZED","Token inválido o expirado; inicia sesión nuevamente");
        return http.csrf(ServerHttpSecurity.CsrfSpec::disable)
            .cors(c->c.configurationSource(cors))
            .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
            .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable).formLogin(ServerHttpSecurity.FormLoginSpec::disable)
            .authorizeExchange(a->a.pathMatchers(HttpMethod.OPTIONS,"/**").permitAll()
                .pathMatchers("/actuator/health","/api/auth/login","/api/auth/register").permitAll()
                .pathMatchers(HttpMethod.GET,"/api/productos","/api/productos/**","/api/categorias","/api/plataformas","/api/comunidad").permitAll()
                .pathMatchers(HttpMethod.POST,"/api/contacto").permitAll()
                .pathMatchers("/api/admin/**").hasRole("ADMIN").anyExchange().authenticated())
            .exceptionHandling(e->e.authenticationEntryPoint(entry).accessDeniedHandler((x,t)->error(x,mapper,403,"ACCESS_DENIED","No tienes permisos para esta acción")))
            .addFilterAt(new JwtAuthenticationFilter(authenticator,entry),SecurityWebFiltersOrder.AUTHENTICATION).build();
    }
    static Mono<Void> error(ServerWebExchange x,ObjectMapper mapper,int status,String code,String message) {
        try {
            var res=x.getResponse();
            res.setStatusCode(org.springframework.http.HttpStatusCode.valueOf(status));
            res.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            if(status==401)res.getHeaders().set("WWW-Authenticate","Bearer");
            byte[] bytes=mapper.writeValueAsBytes(Map.of("timestamp",Instant.now().toString(),"status",status,"code",code,"message",message,"path",x.getRequest().getPath().value(),"fieldErrors",Map.of()));
            return res.writeWith(Mono.just(res.bufferFactory().wrap(bytes)));
        }
        catch(Exception e) {
            return Mono.error(e);
        }
    }
}
