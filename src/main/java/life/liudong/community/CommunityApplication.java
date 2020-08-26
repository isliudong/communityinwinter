package life.liudong.community;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;



/**
 * @author liudong
 */
@SpringBootApplication
@MapperScan(basePackages = "life.liudong.community.mapper")
@EnableScheduling
public class CommunityApplication {
    public static void main(String[] args) {
        SpringApplication.run(CommunityApplication.class, args);
    }

}
