package life.liudong.community.provider;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class GithubProviderTest {

    @Autowired private GithubProvider githubProvider;
    @Test
    void stringHand() {
        String string=githubProvider.stringHand("sad=123ad&asda=adas&asdas=asdad");
        System.out.printf(string);
    }
}