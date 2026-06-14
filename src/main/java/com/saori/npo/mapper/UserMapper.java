package com.saori.npo.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.saori.npo.entity.User;

@Mapper
public interface UserMapper {

    @Select("""
            SELECT
                id,
                username,
                password,
                role,
                enabled,
                created_at,
                updated_at
            FROM users
            WHERE username = #{username}
            """)
    User findByUsername(String username);

}