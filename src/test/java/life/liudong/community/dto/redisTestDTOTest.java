package life.liudong.community.dto;

import life.liudong.community.cache.RedisOP;
import org.junit.jupiter.api.Test;
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


}