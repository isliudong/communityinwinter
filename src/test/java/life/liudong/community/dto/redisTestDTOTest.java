package life.liudong.community.dto;

import life.liudong.community.cache.RedisOP;
import life.liudong.community.mapper.UserMapper;
import life.liudong.community.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class redisTestDTOTest {
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    RedisOP<PaginationDTO> redisOP;

    @Autowired
    UserMapper userMapper;
    @Autowired
    RedisTemplate<Object,User> userRedisTemplate;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Test
    public void redisSave() throws IOException, ClassNotFoundException {
        redisTestDTO p = new redisTestDTO();
        p.setName("中华牙膏");
        p.setDescription("美白防蛀");
        p.setCateLog("日用");
        p.setPrice(10.8);

        PaginationDTO<QuestionDTO> paginationDTO = new PaginationDTO<>();

        QuestionDTO questionDTO = new QuestionDTO();
        questionDTO.setId((long) 1314);
        List<QuestionDTO> data=new ArrayList<>();
        data.add(questionDTO);
        paginationDTO.setData(data);
        paginationDTO.setShowEndPage(false);
        paginationDTO.setShowFirstPage(true);

        redisOP.setObject(paginationDTO.getData().get(0).getId(),paginationDTO);


        PaginationDTO paginationDTO1= (PaginationDTO) redisOP.getObject(paginationDTO.getData().get(0).getId());


    }

    //自定义Redis操作模板
    @Test
    public void test2(){
        User user = userMapper.selectByPrimaryKey(673L);
        userRedisTemplate.opsForValue().set("01",user);

    }

    //mq测试单播
    @Test
    public void test3(){
        User user = userMapper.selectByPrimaryKey(673L);
        rabbitTemplate.convertAndSend("exchange.direct","demo.news",user);

    }

    @Test
    public void receive(){
        Object o = rabbitTemplate.receiveAndConvert("demo.news");
        User user = (User) o;
        assert user != null;
        System.out.println(user.getName());
    }

    //mq测试广播
    @Test
    public void test4(){
        User user = userMapper.selectByPrimaryKey(673L);
        rabbitTemplate.convertAndSend("exchange.fanout","",user);

    }

}