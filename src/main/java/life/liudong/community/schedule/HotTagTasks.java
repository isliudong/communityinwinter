package life.liudong.community.schedule;

import life.liudong.community.cache.HotTagCache;
import life.liudong.community.mapper.QuestionMapper;
import life.liudong.community.model.Question;
import life.liudong.community.model.QuestionExample;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;


/**
 * @author liudong
 */
@Component
@Slf4j
public class HotTagTasks {
    private final QuestionMapper questionMapper;
    private final HotTagCache hotTagCache;

    public HotTagTasks(QuestionMapper questionMapper, HotTagCache hotTagCache) {
        this.questionMapper = questionMapper;
        this.hotTagCache = hotTagCache;
    }


    //时间控制5秒有利于开发
    @Scheduled(fixedRate = 1000*60*60)
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
