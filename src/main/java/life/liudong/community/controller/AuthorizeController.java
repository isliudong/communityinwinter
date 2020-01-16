package life.liudong.community.controller;

import life.liudong.community.dto.AccessTokenDTO;
import life.liudong.community.dto.GithubUser;
import life.liudong.community.mapper.UserMapper;
import life.liudong.community.model.User;
import life.liudong.community.provider.GithubProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Controller
public class AuthorizeController {
    @Autowired
    private GithubProvider githubProvider;

    @Autowired
    private UserMapper userMapper;

    @Value("${github.client.id}")
    private String cliendId;

    @Value("${github.client.secret}")
    private String cliendSecret;

    @Value("${github.redirect.uri}")
    private String redirectUri;

    @RequestMapping("/callback")
    public String callback(@RequestParam(name = "code") String code,
                           @RequestParam(name="state") String state,
                           HttpServletResponse response) {

        AccessTokenDTO accessTokenDTO = new AccessTokenDTO();
        accessTokenDTO.setCode(code);
        accessTokenDTO.setRedirect_uri(redirectUri);
        accessTokenDTO.setState(state);
        accessTokenDTO.setClient_id(cliendId);
        accessTokenDTO.setClient_secret(cliendSecret);

        String accessToken=githubProvider.getAccessToken(accessTokenDTO);
        GithubUser githubUser=githubProvider.getUser(accessToken);
        System.out.println(githubUser.getName());
        if(githubUser!=null)
        {
            User user = new User();
            String token = UUID.randomUUID().toString();//uuid主键生成策略
            user.setToken(token);
            user.setName(githubUser.getName());
            user.setAccountId(String.valueOf(githubUser.getId()));
            user.setGmtCreate(System.currentTimeMillis());
            user.setGmtModified(user.getGmtCreate());
            userMapper.insert(user);
            //登录成功将token写入Cookie
            response.addCookie(new Cookie("token",token));
            return "redirect:";//转发至主页，可以去掉链接后缀信息
        }else {
            return "redirect:";
            //登录失败
        }

    }
}
