package com.novagames.support.service;
import com.novagames.support.dto.ContactDtos.*;
import com.novagames.support.entity.ContactMessage;
import com.novagames.support.repository.ContactRepository;
import com.novagames.support.mapper.ContactMapper;
import com.novagames.common.exception.BusinessException;
import org.springframework.stereotype.Service;
import java.util.*;
@Service public class ContactService {
    private final ContactRepository repo;
    public ContactService(ContactRepository r) {
        repo=r;
    }
    public Receipt create(Input d) {
        var c=new ContactMessage();
        c.nombre=d.nombre();
        c.email=d.email();
        c.asunto=d.asunto();
        c.mensaje=d.mensaje();
        return new Receipt(repo.save(c).id,"Mensaje recibido");
    }
    public List<View> list() {
        return repo.findAllByOrderByFechaDesc().stream().map(ContactMapper::view).toList();
    }
    public View update(Long id,Status s) {
        var c=repo.findById(id).orElseThrow(()->BusinessException.missing("MESSAGE_NOT_FOUND","Mensaje no encontrado"));
        c.resuelto=s.resuelto();
        return ContactMapper.view(repo.save(c));
    }
}
