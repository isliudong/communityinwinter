package life.liudong.community;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CommunityApplicationTests {

    @Test
    void contextLoads() {
        String path = System.getProperty("user.dir");
        System.out.println(path);
    }

}
