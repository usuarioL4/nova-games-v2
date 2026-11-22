package com.novagames.api.security.filter;
import com.novagames.api.security.SecurityAuthenticator;
import org.springframework.security.web.server.authentication.*;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.security.oauth2.server.resource.web.server.authentication.ServerBearerTokenAuthenticationConverter;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
// WebFlux: equivalente reactivo de OncePerRequestFilter; se inserta en la cadena de seguridad.
public class JwtAuthenticationFilter extends AuthenticationWebFilter {
    public JwtAuthenticationFilter(SecurityAuthenticator authenticator,ServerAuthenticationEntryPoint entryPoint){
        super(authenticator);
        setServerAuthenticationConverter(new ServerBearerTokenAuthenticationConverter());
        setSecurityContextRepository(NoOpServerSecurityContextRepository.getInstance());
        setAuthenticationFailureHandler(new ServerAuthenticationEntryPointFailureHandler(entryPoint));
    }
}
