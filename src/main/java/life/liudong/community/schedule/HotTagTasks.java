package life.liudong.community.schedule;

import life.liudong.community.cache.HotTagCache;
import life.liudong.community.mapper.QuestionMapper;
import life.liudong.community.model.Question;
import life.liudong.community.model.QuestionExample;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;

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

    @Autowired
    private HotTagCache hotTagCache;


    @Scheduled(fixedRate = 1000*60*60)//时间控制暂定5秒有利于开发
    //@Scheduled(cron="0 0 1 * * *")
    public void hotTagSchedule() {
        int offset = 0;
        int limit = 5;
        log.info("The time start now {}", new Date());
        List<Question> list = new ArrayList<>();
        Map<String, Integer> hotTags = new HashMap<>();

        //遍历问题，计算标签权重，放入HashMap
        while (offset == 0 || list.size() == limit) {
            list = questionMapper.selectByExampleWithRowbounds(new QuestionExample(), new RowBounds(offset, limit));
            for (Question question : list) {

                String[] tagsInQuestion = StringUtils.split(question.getTag(), ",");
                for (String aTag : tagsInQuestion) {
                    Integer priority = hotTags.get(aTag);
                    if (priority != null) {
                        hotTags.put(aTag, priority + 5 + question.getCommentCount());
                    } else {
                        hotTags.put(aTag, 5 + question.getCommentCount());
                    }
                }
            }
            offset += limit;
        }
        hotTagCache.updateTags(hotTags);
        log.info("刷新热点时间：{} ", new Date());
    }
}
