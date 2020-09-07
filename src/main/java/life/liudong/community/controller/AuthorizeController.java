package life.liudong.community.controller;

import life.liudong.community.dto.AccessTokenDTO;
import life.liudong.community.dto.GithubUser;
import life.liudong.community.model.User;
import life.liudong.community.provider.GithubProvider;
import life.liudong.community.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * @author liudong
 */
@Controller
@Slf4j
public class AuthorizeController {
    private final GithubProvider githubProvider;

    private final UserService userService;
    @Value("${github.client.id}")
    private String clientId;

    @Value("${github.client.secret}")
    private String clientSecret;

    @Value("${github.redirect.url}")
    private String redirectUrl;

    public AuthorizeController(GithubProvider githubProvider, UserService userService) {
        this.githubProvider = githubProvider;
        this.userService = userService;
    }

    @RequestMapping("/callback")
    public String callback(@RequestParam(name = "code") String code,
                           @RequestParam(name="state") String state,
                           HttpServletResponse response) {

        AccessTokenDTO accessTokenDTO = new AccessTokenDTO();
        accessTokenDTO.setCode(code);
        accessTokenDTO.setRedirect_uri(redirectUrl);
        accessTokenDTO.setState(state);
        accessTokenDTO.setClient_id(clientId);
        accessTokenDTO.setClient_secret(clientSecret);

        //通过code得到accessToken
        String accessToken=githubProvider.getAccessToken(accessTokenDTO);
        //通过accessToken得到用户信息
        GithubUser githubUser=githubProvider.getUser(accessToken);
        log.info("git用户："+githubUser.getName()+"登录");

        User user = new User();
        //uuid主键生成策略
        String token = UUID.randomUUID().toString();
        user.setToken(token);
        user.setName(githubUser.getName());
        user.setAccountId(String.valueOf(githubUser.getId()));
        user.setAvatarUrl(githubUser.getAvatarUrl());
        userService.createOrUpdate(user);
        //登录成功将token写入Cookie
        response.addCookie(new Cookie("token",token));
        return "redirect:";

    }
    @GetMapping("/logout")
    public String logout(HttpServletRequest request ,HttpServletResponse response){
        request.getSession().removeAttribute("user");
        Cookie cookie=new Cookie("token",null);
        response.addCookie(cookie);
        return "redirect:/";
    }
}
