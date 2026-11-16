package com.novagames.commerce.security.config;
import com.novagames.commerce.security.filter.JwtAuthenticationFilter;
import com.novagames.commerce.security.SecurityAuthenticator;
import com.novagames.common.exception.ApiError;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.http.HttpMethod;
import jakarta.servlet.http.*;
import java.io.IOException;
@Configuration @EnableMethodSecurity
public class SecurityConfig {

    @Bean JwtDecoder jwtDecoder(@Value("${JWK_URL:http://localhost:8081/.well-known/jwks.json}") String jwks,
                                 @Value("${JWT_ISSUER:novagames}") String issuer) {
        var decoder=NimbusJwtDecoder.withJwkSetUri(jwks).build();
        decoder.setJwtValidator(JwtValidators.createDefaultWithIssuer(issuer));
        return decoder;
    }

    @Bean SecurityFilterChain security(HttpSecurity http,SecurityAuthenticator authenticator,ObjectMapper mapper) throws Exception {
        return http.csrf(c->c.disable())
            .sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .formLogin(f->f.disable()).httpBasic(b->b.disable())
            .authorizeHttpRequests(a->a
                .requestMatchers("/actuator/health","/swagger-ui/**","/swagger-ui.html","/v3/api-docs/**","/.well-known/jwks.json","/api/auth/login","/api/auth/register").permitAll()
                .requestMatchers(HttpMethod.GET,"/api/productos","/api/productos/**","/api/categorias","/api/plataformas","/api/comunidad").permitAll()
                .requestMatchers(HttpMethod.POST,"/api/contacto").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated())
            .exceptionHandling(e->e
                .authenticationEntryPoint((req,res,x)->error(mapper,req,res,401,"UNAUTHORIZED","Debes iniciar sesión o renovar tu token"))
                .accessDeniedHandler((req,res,x)->error(mapper,req,res,403,"ACCESS_DENIED","No tienes permisos para esta acción")))
            .addFilterBefore(new JwtAuthenticationFilter(authenticator,mapper),UsernamePasswordAuthenticationFilter.class)
            .build();
    }
    private void error(ObjectMapper mapper,HttpServletRequest req,HttpServletResponse res,int status,String code,String msg)throws IOException {
        res.setStatus(status); res.setContentType("application/json;charset=UTF-8");
        if(status==401)res.setHeader("WWW-Authenticate","Bearer");
        mapper.writeValue(res.getOutputStream(),ApiError.of(status,code,msg,req.getRequestURI()));
    }
}
