package life.liudong.community.provider;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import javax.annotation.security.RunAs;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class GithubProviderTest {

    @Autowired private GithubProvider githubProvider;
    @Test
    void stringHand() {
        String string=githubProvider.StringHand("sad=123ad&asda=adas&asdas=asdad");
        System.out.printf(string);
    }
}