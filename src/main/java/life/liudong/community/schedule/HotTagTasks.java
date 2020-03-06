package life.liudong.community.schedule;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @program: community
 * @description: 计划任务-获取热门标签
 * @author: 闲乘月
 * @create: 2020-03-06 22:22
 **/
@Component
@Slf4j
public class HotTagTasks {

    @Scheduled(fixedRate = 5000)
    public void reportCurrentTime() {
        log.info("The time is now {}", new Date());
    }
}
