package life.liudong.community.mapper;

import javafx.application.Application;
import life.liudong.community.CommunityApplication;
import life.liudong.community.model.UserExample;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = CommunityApplication.class)
class UserMapperTest {

    @Autowired
    UserMapper userMapper;
    @Test
    void countByExample() {
        long l = userMapper.countByExample(new UserExample());
        System.out.println(l);
    }
}