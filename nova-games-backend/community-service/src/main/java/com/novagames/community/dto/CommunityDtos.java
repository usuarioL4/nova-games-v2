package com.novagames.community.dto;
import jakarta.validation.constraints.*;
import java.time.Instant;
public class CommunityDtos {
    public record Input(@NotBlank @Size(max=1000) String texto) {
    }
    public record View(Long id,String username,String texto,Instant fecha) {
    }
}
