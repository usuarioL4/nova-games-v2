package com.novagames.auth.mapper;
import com.novagames.auth.entity.UserAccount;
import com.novagames.auth.dto.AuthDtos.UserView;
public class UserMapper {
    public static UserView view(UserAccount u) {
        return new UserView(u.id,u.username,u.email,u.cellphone,u.role);
    }
}
