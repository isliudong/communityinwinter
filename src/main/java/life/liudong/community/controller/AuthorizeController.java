package life.liudong.community.controller;

import life.liudong.community.dto.AccessTokenDTO;
import life.liudong.community.dto.GithubUser;
import life.liudong.community.model.User;
import life.liudong.community.provider.GithubProvider;
import life.liudong.community.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Controller
@Slf4j
public class AuthorizeController {
    @Autowired
    private GithubProvider githubProvider;

    @Autowired
    private UserService userService;
    @Value("${github.client.id}")
    private String cliendId;

    @Value("${github.client.secret}")
    private String cliendSecret;

    @Value("${github.redirect.url}")
    private String redirectUrl;

    @RequestMapping("/callback")
    public String callback(@RequestParam(name = "code") String code,
                           @RequestParam(name="state") String state,
                           HttpServletResponse response) {

        AccessTokenDTO accessTokenDTO = new AccessTokenDTO();
        accessTokenDTO.setCode(code);//得到code
        accessTokenDTO.setRedirect_uri(redirectUrl);
        accessTokenDTO.setState(state);
        accessTokenDTO.setClient_id(cliendId);
        accessTokenDTO.setClient_secret(cliendSecret);

        String accessToken=githubProvider.getAccessToken(accessTokenDTO);//通过code得到accessToken
        GithubUser githubUser=githubProvider.getUser(accessToken);//通过accessToken得到用户信息
        log.info("git用户："+githubUser.getName()+"登录");
        if(githubUser!=null)
        {

            User user = new User();
            String token = UUID.randomUUID().toString();//uuid主键生成策略
            user.setToken(token);
            user.setName(githubUser.getName());
            user.setAccountId(String.valueOf(githubUser.getId()));
            user.setAvatarUrl(githubUser.getAvatarUrl());
            userService.createOrUpdeate(user);
            //登录成功将token写入Cookie
            response.addCookie(new Cookie("token",token));
            return "redirect:";//转发至主页，可以去掉链接后缀信息
        }else {
            log.error("callback get github error, {}",githubUser);
            return "redirect:";
            //登录失败
        }

    }
    @GetMapping("/logout")
    public String logout(HttpServletRequest request ,HttpServletResponse response){
        request.getSession().removeAttribute("user");
        Cookie cookie=new Cookie("token",null);
        response.addCookie(cookie);
        return "redirect:/";
    }
}
