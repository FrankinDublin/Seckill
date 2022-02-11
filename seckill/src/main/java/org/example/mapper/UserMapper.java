package org.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.domain.User;

@Mapper
public interface UserMapper {
    @Select("select * from sk_user where id = #{id}")
    public User getById(@Param("id")String id);
}
