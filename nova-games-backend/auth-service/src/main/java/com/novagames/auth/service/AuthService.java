package com.novagames.auth.service;
import com.novagames.auth.dto.AuthDtos.*;
import com.novagames.auth.entity.UserAccount;
import com.novagames.auth.repository.UserRepository;
import com.novagames.auth.mapper.UserMapper;
import com.novagames.common.exception.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.novagames.auth.security.jwt.JwtService;
import java.util.*;
@Service public class AuthService {
    private final UserRepository repo;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;
    public AuthService(UserRepository repo,PasswordEncoder encoder,JwtService jwtService) {
        this.repo=repo;
        this.encoder=encoder;
        this.jwtService=jwtService;
    }
    public UserView register(Register r) {
        String email=r.email().trim().toLowerCase(Locale.ROOT);
        if(!r.password().equals(r.confirmPassword()))throw new BusinessException(400,"PASSWORD_MISMATCH","Las contraseñas no coinciden");
        if(repo.findByEmail(email).isPresent())throw new BusinessException(409,"EMAIL_ALREADY_EXISTS","El correo ya está registrado");
        if(repo.existsByUsernameIgnoreCase(r.username().trim()))throw new BusinessException(409,"USERNAME_ALREADY_EXISTS","El nombre de usuario ya existe");
        if(repo.existsByCellphone(r.cellphone()))throw new BusinessException(409,"PHONE_ALREADY_EXISTS","El teléfono ya está registrado");
        if(r.password().getBytes(java.nio.charset.StandardCharsets.UTF_8).length>72)throw new BusinessException(400,"INVALID_PASSWORD","La contraseña supera el máximo de 72 bytes");
        if(!email.matches(".+@(duoc\\.cl|profesor\\.duoc\\.cl|gmail\\.com)"))throw new BusinessException(400,"INVALID_EMAIL_DOMAIN","Usa un correo Duoc o Gmail");
        UserAccount u=new UserAccount();
        u.username=r.username().trim();
        u.email=email;
        u.passwordHash=encoder.encode(r.password());
        u.cellphone=r.cellphone();
        u.referenceCode=r.referenceCode();
        u.ageConfirmed=r.ageConfirmed();
        return UserMapper.view(repo.saveAndFlush(u));
    }
    public Token login(Login r) {
        var u=repo.findByEmail(r.email().trim().toLowerCase(Locale.ROOT)).orElseThrow(()->new BusinessException(401,"INVALID_CREDENTIALS","Correo o contraseña incorrectos"));
        if(!encoder.matches(r.password(),u.passwordHash))throw new BusinessException(401,"INVALID_CREDENTIALS","Correo o contraseña incorrectos");
        return new Token(jwtService.generateToken(u.id,u.username,u.role),"Bearer",3600,UserMapper.view(u));
    }
    public UserView me(String id) {
        return repo.findById(UUID.fromString(id)).map(UserMapper::view).orElseThrow(()->BusinessException.missing("USER_NOT_FOUND","Usuario no encontrado"));
    }
    public List<UserView> users() {
        return repo.findAll().stream().map(UserMapper::view).toList();
    }
}
