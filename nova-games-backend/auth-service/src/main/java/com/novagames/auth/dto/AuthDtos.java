package com.novagames.auth.dto;
import jakarta.validation.constraints.*;
import java.util.UUID;
public class AuthDtos {
    public record Register(@NotBlank @Size(min=3,max=60) String username,@NotBlank @Email @Size(max=100) String email,@NotBlank @Size(min=8,max=72) String password,@NotBlank String confirmPassword,@NotBlank @Pattern(regexp="[0-9]{9,15}") String cellphone,@Size(max=100) String referenceCode,@AssertTrue boolean ageConfirmed) {
    }
    public record Login(@NotBlank @Email String email,@NotBlank String password) {
    }
    public record UserView(UUID id,String username,String email,String cellphone,String role) {
    }
    public record Token(String accessToken,String tokenType,long expiresIn,UserView user) {
    }
}
