package life.liudong.community.config;

import life.liudong.community.model.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;


/**
 * @author liudong
 */
@Configuration
public class RedisConfig {
    @Bean
    public RedisTemplate<Object, User> userRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<Object, User> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        //自定义序列化器，支持json
        Jackson2JsonRedisSerializer<User> serializer = new Jackson2JsonRedisSerializer<>(User.class);
        template.setDefaultSerializer(serializer);
        return template;
    }
}
