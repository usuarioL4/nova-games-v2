package com.novagames.commerce.client;
import com.novagames.commerce.dto.CommerceDtos.*;
import com.novagames.common.exception.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.*;
import org.springframework.beans.factory.annotation.Value;
import com.fasterxml.jackson.databind.ObjectMapper;
@Component public class CatalogClient {
    private final RestClient client;
    private final String key;
    private final ObjectMapper json;
    public CatalogClient(RestClient.Builder builder,@Value("${INTERNAL_KEY}") String key,ObjectMapper json) {
        client=builder.baseUrl("http://catalog-service").build();
        this.key=key;
        this.json=json;
    }
    public Product product(Long id) {
        try {
            return client.get().uri("/api/productos/{id}",id).retrieve().body(Product.class);
        }
        catch(RestClientException e) {
            throw translate(e);
        }
    }
    public ReserveResult reserve(ReserveRequest request,String token) {
        try {
            return client.post().uri("/internal/reservas").header("Authorization","Bearer "+token).header("X-Internal-Key",key).body(request).retrieve().body(ReserveResult.class);
        }
        catch(RestClientException e) {
            throw translate(e);
        }
    }
    private BusinessException translate(RestClientException e) {
        if(e instanceof RestClientResponseException response && response.getStatusCode().is4xxClientError()) {
            try {
                var error=json.readValue(response.getResponseBodyAsString(),ApiError.class);
                return new BusinessException(error.status(),error.code(),error.message());
            }
            catch(Exception ignored) {
            }
        }
        return new BusinessException(503,"SERVICE_UNAVAILABLE","El catálogo no está disponible. Reintenta la operación.");
    }
}
