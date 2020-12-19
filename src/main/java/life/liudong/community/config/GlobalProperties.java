package life.liudong.community.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 全局配置
 * @author liudong
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "community")
public class GlobalProperties {

    /**
     * 文章文件服务器
     */
    private String fileSever="/";

}