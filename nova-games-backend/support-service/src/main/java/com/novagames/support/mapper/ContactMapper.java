package com.novagames.support.mapper;
import com.novagames.support.dto.ContactDtos.View;
import com.novagames.support.entity.ContactMessage;
public class ContactMapper {
    public static View view(ContactMessage c) {
        return new View(c.id,c.nombre,c.email,c.asunto,c.mensaje,c.resuelto,c.fecha);
    }
}
