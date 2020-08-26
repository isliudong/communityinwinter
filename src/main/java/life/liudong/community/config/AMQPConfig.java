package life.liudong.community.config;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 项目未使用mq，仅用于测试
 * @author LD
 * @since  2020-08-07 15:08
 **/

@Configuration
public class AMQPConfig {

    @Bean
    public MessageConverter messageConverter(){

        return new Jackson2JsonMessageConverter();
    }
}
