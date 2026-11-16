package com.novagames.community.service;
import com.novagames.community.dto.CommunityDtos.*;
import com.novagames.community.entity.Comment;
import com.novagames.community.repository.CommentRepository;
import com.novagames.community.mapper.CommentMapper;
import com.novagames.common.exception.BusinessException;
import org.springframework.stereotype.Service;
import java.util.*;
@Service public class CommunityService {
    private final CommentRepository repo;
    public CommunityService(CommentRepository r) {
        repo=r;
    }
    public List<View> list() {
        return repo.findAllByOrderByFechaDesc().stream().map(CommentMapper::view).toList();
    }
    public View create(String owner,String username,Input d) {
        var c=new Comment();
        c.owner=owner;
        c.username=username;
        c.texto=d.texto();
        return CommentMapper.view(repo.save(c));
    }
    public void delete(Long id,String owner,boolean admin) {
        var c=repo.findById(id).orElseThrow(()->BusinessException.missing("COMMENT_NOT_FOUND","Comentario no encontrado"));
        if(!admin&&!c.owner.equals(owner))throw new BusinessException(403,"ACCESS_DENIED","No puedes eliminar este comentario");
        repo.delete(c);
    }
}
