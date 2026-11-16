package com.novagames.api.config;
import org.springframework.context.annotation.*;
import org.springframework.cloud.gateway.route.*;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.*;
import java.util.*;
@Configuration public class GatewayRoutes {
    @Bean RouteLocator routes(RouteLocatorBuilder b) {
        return b.routes().route("auth",r->r.path("/api/auth/**","/api/admin/usuarios","/api/admin/usuarios/**").uri("lb://auth-service")).route("catalog",r->r.path("/api/productos","/api/productos/**","/api/categorias","/api/plataformas").uri("lb://catalog-service")).route("commerce",r->r.path("/api/carrito","/api/carrito/**","/api/pedidos","/api/pedidos/**","/api/admin/pedidos","/api/admin/pedidos/**").uri("lb://commerce-service")).route("community",r->r.path("/api/comunidad","/api/comunidad/**").uri("lb://community-service")).route("support",r->r.path("/api/contacto","/api/admin/contacto","/api/admin/contacto/**").uri("lb://support-service")).build();
    }
    @Bean UrlBasedCorsConfigurationSource cors(@Value("${FRONTEND_ORIGIN:http://localhost:5173}") String origin) {
        var c=new CorsConfiguration();
        c.setAllowedOrigins(List.of(origin));
        c.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));
        c.setAllowedHeaders(List.of("Authorization","Content-Type","Idempotency-Key"));
        var s=new UrlBasedCorsConfigurationSource();
        s.registerCorsConfiguration("/**",c);
        return s;
    }
}
