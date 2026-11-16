package com.novagames.community.mapper;
import com.novagames.community.entity.Comment;
import com.novagames.community.dto.CommunityDtos.View;
public class CommentMapper {
    public static View view(Comment c) {
        return new View(c.id,c.username,c.texto,c.fecha);
    }
}
