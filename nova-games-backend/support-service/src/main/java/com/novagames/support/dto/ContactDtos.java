package com.novagames.support.dto;
import jakarta.validation.constraints.*;
import java.time.Instant;
public class ContactDtos {
    public record Input(@NotBlank @Size(max=100) String nombre,@NotBlank @Email @Size(max=100) String email,@NotBlank @Size(max=100) String asunto,@NotBlank @Size(max=500) String mensaje) {
    }
    public record Receipt(Long id,String message) {
    }
    public record View(Long id,String nombre,String email,String asunto,String mensaje,boolean resuelto,Instant fecha) {
    }
    public record Status(@NotNull Boolean resuelto) {
    }
}
