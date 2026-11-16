package com.novagames.api.exception;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.server.*;
import reactor.core.publisher.Mono;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatusCode;
import java.util.*;
import java.time.Instant;
import org.slf4j.*;
@Component @Order(-2) public class GatewayExceptionHandler implements ErrorWebExceptionHandler {
    private final ObjectMapper mapper;
    private final Logger log=LoggerFactory.getLogger(getClass());
    public GatewayExceptionHandler(ObjectMapper m) {
        mapper=m;
    }
    public Mono<Void> handle(ServerWebExchange x,Throwable e) {
        if(x.getResponse().isCommitted())return Mono.error(e);
        int status=e instanceof ResponseStatusException r?r.getStatusCode().value():503;
        String code=status==404?"NOT_FOUND":status==503?"SERVICE_UNAVAILABLE":"GATEWAY_ERROR";
        String msg=status==404?"Ruta no encontrada":status==503?"Servicio no disponible, intenta nuevamente":"No se pudo procesar la solicitud";
        log.warn("Gateway {} {}",status,x.getRequest().getPath(),e);
        try {
            var r=x.getResponse();
            r.setStatusCode(HttpStatusCode.valueOf(status));
            r.getHeaders().setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
            return r.writeWith(Mono.just(r.bufferFactory().wrap(mapper.writeValueAsBytes(Map.of("timestamp",Instant.now().toString(),"status",status,"code",code,"message",msg,"path",x.getRequest().getPath().value(),"fieldErrors",Map.of())))));
        }
        catch(Exception ex) {
            return Mono.error(ex);
        }
    }
}
