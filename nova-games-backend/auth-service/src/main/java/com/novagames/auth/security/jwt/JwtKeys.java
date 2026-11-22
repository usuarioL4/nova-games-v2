package com.novagames.auth.security.jwt;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.beans.factory.annotation.Value;
import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
@Configuration public class JwtKeys {
    @Bean RSAKey rsaKey(@Value("${DATA_DIR:./data}") String dir)throws Exception {
        Path folder=Path.of(dir);
        Files.createDirectories(folder);
        Path priv=folder.resolve("jwt-private.der"),pub=folder.resolve("jwt-public.der");
        KeyFactory factory=KeyFactory.getInstance("RSA");
        if(!Files.exists(priv)&&!Files.exists(pub)) {
            var gen=KeyPairGenerator.getInstance("RSA");
            gen.initialize(2048);
            var pair=gen.generateKeyPair();
            Files.write(priv,pair.getPrivate().getEncoded(),StandardOpenOption.CREATE_NEW);
            Files.write(pub,pair.getPublic().getEncoded(),StandardOpenOption.CREATE_NEW);
        }
        return new RSAKey.Builder((RSAPublicKey)factory.generatePublic(new X509EncodedKeySpec(Files.readAllBytes(pub)))).privateKey((RSAPrivateKey)factory.generatePrivate(new PKCS8EncodedKeySpec(Files.readAllBytes(priv)))).keyID("nova-local-rsa").build();
    }
    @Bean JwtEncoder encoder(RSAKey key) {
        return new NimbusJwtEncoder(new ImmutableJWKSet<>(new JWKSet(key)));
    }
    @Bean JwtDecoder decoder(RSAKey key,@Value("${JWT_ISSUER:novagames}") String issuer)throws Exception {
        var d=NimbusJwtDecoder.withPublicKey(key.toRSAPublicKey()).build();
        d.setJwtValidator(JwtValidators.createDefaultWithIssuer(issuer));
        return d;
    }
    @Bean org.springframework.security.crypto.password.PasswordEncoder passwordEncoder() {
        return new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
    }
}
