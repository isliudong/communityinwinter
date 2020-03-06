package life.liudong.community.schedule;

import life.liudong.community.mapper.QuestionMapper;
import life.liudong.community.model.Question;
import life.liudong.community.model.QuestionExample;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @program: community
 * @description: 计划任务-获取热门标签
 * @author: 闲乘月
 * @create: 2020-03-06 22:22
 **/
@Component
@Slf4j
public class HotTagTasks {
    @Autowired
    private QuestionMapper questionMapper;


    @Scheduled(fixedRate = 5000)
    //@Scheduled(cron="0 0 1 * * *")
    public void hotTagSchedule() {
        int offset=0;
        int limit=5;
        log.info("The time start now {}", new Date());
        List<Question> list=new ArrayList<>();

        while (offset==0||list.size()==limit){
            list=questionMapper.selectByExampleWithRowbounds(new QuestionExample(),new RowBounds(offset,limit));
            for (Question question : list) {
                log.info("list question:{}",question.getId());

            }

            offset+=limit;
        }

        log.info("The time stop now {}", new Date());
    }
}
