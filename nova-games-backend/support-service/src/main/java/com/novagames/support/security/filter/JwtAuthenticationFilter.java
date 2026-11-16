package com.novagames.support.security.filter;
import com.novagames.support.security.SecurityAuthenticator;
import com.novagames.common.exception.ApiError;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.AuthenticationException;
import java.io.IOException;
// Se instancia en SecurityConfig: evita registrarlo dos veces como filtro servlet.
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final SecurityAuthenticator authenticator;
    private final ObjectMapper mapper;
    public JwtAuthenticationFilter(SecurityAuthenticator authenticator,ObjectMapper mapper) {
        this.authenticator=authenticator; this.mapper=mapper;
    }
    @Override protected void doFilterInternal(HttpServletRequest request,HttpServletResponse response,FilterChain chain) throws ServletException,IOException {
        String header=request.getHeader("Authorization");
        if(header!=null && header.regionMatches(true,0,"Bearer ",0,7)) {
            try {
                var context=SecurityContextHolder.createEmptyContext();
                context.setAuthentication(authenticator.authenticate(header.substring(7).trim()));
                SecurityContextHolder.setContext(context);
            } catch(AuthenticationException ex) {
                SecurityContextHolder.clearContext();
                response.setStatus(401); response.setContentType("application/json;charset=UTF-8");
                response.setHeader("WWW-Authenticate","Bearer");
                mapper.writeValue(response.getOutputStream(),ApiError.of(401,"UNAUTHORIZED","Token inválido o expirado",request.getRequestURI()));
                return;
            }
        }
        chain.doFilter(request,response);
    }
}
