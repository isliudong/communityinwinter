package life.liudong.community.schedule;

import life.liudong.community.dto.PaginationDTO;
import life.liudong.community.dto.QuestionDTO;
import life.liudong.community.service.QuestionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


/**
 * @program: community
 * @description:
 * @author: 闲乘月
 * @create: 2020-03-16 16:01
 **/
@Component
@Slf4j
public class RedisTask {
    @Autowired
    QuestionService questionService;

    @Scheduled(fixedRate = 300000)
    public void setFirstPage()  {
        PaginationDTO<QuestionDTO> paginationDTO;
        paginationDTO=questionService.list(null, null, 1, 5);
        //将首页写入缓存
        questionService.setPageInRedis(1, paginationDTO);

    }
}
