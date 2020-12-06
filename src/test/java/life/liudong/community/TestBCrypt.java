package life.liudong.community;

import java.util.Objects;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCrypt;

/**
 * @author 28415@hand-china.com 2020/11/29 21:11
 */
public class TestBCrypt {
    @Test
    public void bcrypt(){
        String hashpw = BCrypt.hashpw("123", BCrypt.gensalt());
        System.out.println(hashpw);
        System.out.println(BCrypt.checkpw("123",hashpw));
    }

}
