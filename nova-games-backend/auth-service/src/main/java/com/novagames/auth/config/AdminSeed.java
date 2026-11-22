package com.novagames.auth.config;
import com.novagames.auth.entity.UserAccount;
import com.novagames.auth.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
@Configuration public class AdminSeed {
    @Bean CommandLineRunner admin(UserRepository repo,PasswordEncoder enc,@Value("${ADMIN_EMAIL:}") String email,@Value("${ADMIN_PASSWORD:}") String password) {
        return args-> {
            if(email.isBlank()||password.isBlank())return;
            if(password.length()<8)throw new IllegalStateException("ADMIN_PASSWORD debe tener al menos 8 caracteres");
            String normalized=email.trim().toLowerCase(java.util.Locale.ROOT);
            if(repo.findByEmail(normalized).isEmpty()) {
                UserAccount u=new UserAccount();
                u.username="nova-admin";
                u.email=normalized;
                u.passwordHash=enc.encode(password);
                u.role="ADMIN";
                u.ageConfirmed=true;
                repo.save(u);
            }
        }
        ;
    }
}
