package life.liudong.community.mapper;

import life.liudong.community.model.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

@Component
@Mapper
public interface UserMapper {
    @Insert("insert into user(name,account_id,token,gmt_create,gmt_modified,avatar_url) values(#{name},#{accountId},#{token},#{gmtCreate},#{gmtModified},#{avatarUrl})")
    void insert(User user);//类自动放

    @Select("select id,name,account_id as accountId,gmt_create as gmtCreate,gmt_Modified as gmtModified,token,avatar_url as avatarUrl from user where token=#{token}")
    User findByToken(@Param("token") String token);//单个参数添加注解@Param

    @Select("select id,name,account_id as accountId,gmt_create as gmtCreate,gmt_Modified as gmtModified,token,avatar_url as avatarUrl from user where id=#{id}")
    User findById(@Param("id") Integer id);
}
