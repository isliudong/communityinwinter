package life.liudong.community.core;

import life.liudong.community.config.CommunityThreadPoolConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * @author 28415@hand-china.com 2020/11/25 16:01
 */
@Configuration
public class CommunityThreadPool {
    private final CommunityThreadPoolConfig poolConfig;

    public CommunityThreadPool(CommunityThreadPoolConfig poolConfig) {
        this.poolConfig = poolConfig;
    }


    @Bean("communityPool")
    public Executor communityPool(){
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        // 单线程
        taskExecutor.setCorePoolSize(poolConfig.getCorePoolSize());
        taskExecutor.setMaxPoolSize(poolConfig.getMaxPoolSize());
        // 任务队列大小
        taskExecutor.setQueueCapacity(poolConfig.getQueueCapacity());
        // 线程前缀
        taskExecutor.setThreadNamePrefix(poolConfig.getThreadNamePrefix());
        taskExecutor.initialize();
        return taskExecutor;
    }
}
