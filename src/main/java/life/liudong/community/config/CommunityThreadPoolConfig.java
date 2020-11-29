package life.liudong.community.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 社区线程池配置（单线程任务队列,避免并发错误）
 * @author 28415@hand-china.com 2020/11/25 15:49
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "community.core.pool")
public class CommunityThreadPoolConfig {
    private int corePoolSize=1;

    private int maxPoolSize=1;

    private int keepAliveSeconds;

    private int queueCapacity=200;

    private String threadNamePrefix="single-thread-";
}
