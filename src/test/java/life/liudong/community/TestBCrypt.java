package life.liudong.community;

import java.util.Objects;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCrypt;

/**
 * @author 28415@hand-china.com 2020/11/29 21:11
 */
@SpringBootTest("CommunityApplication.class")
public class TestBCrypt {
    @Value("${image.server.url}")
    private String imgServer;
    @Value("${web.upload-path}")
    private String fileServer;
    @Test
    public void bcrypt(){
        String hashpw = BCrypt.hashpw("123", BCrypt.gensalt());
        System.out.println(hashpw);
        System.out.println(BCrypt.checkpw("123",hashpw));
    }
    @Test
    public void test1(){
        String s = imgServer.substring(fileServer.length()-1);
        System.out.println(s);
    }

}
