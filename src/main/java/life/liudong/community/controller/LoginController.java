package life.liudong.community.controller;

import life.liudong.community.mapper.UserMapper;
import life.liudong.community.model.User;
import life.liudong.community.model.UserExample;
import life.liudong.community.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;


/**
 * @author liudong
 */
@Controller
public class LoginController {
    final
    UserMapper userMapper;
    final
    UserService userService;

    @Value("${github.client.id}")
    String clientId;
    @Value("${github.redirect.url}")
    String redirectUrl;

    public LoginController(UserMapper userMapper, UserService userService) {
        this.userMapper = userMapper;
        this.userService = userService;
    }


    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("clientId",clientId);
        model.addAttribute("redirect_url", redirectUrl);
        return "login";
    }

    @PostMapping("/goLogin")
    public String goLogin(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "password", required = false) String password,
            HttpServletResponse response,
            HttpServletRequest request
    ) {
        UserExample user = new UserExample();
        user.createCriteria().andNameEqualTo(name);
        List<User> users = userMapper.selectByExample(user);

        if (users.size() == 1) {
            User user1 = users.get(0);
            System.out.println(user1);
            System.out.println(name + ":  " + password);
            if (user1.getPassword().equals(password)) {
                request.getSession().setAttribute("user", user1);
                response.addCookie(new Cookie("token", user1.getToken()));
                return "redirect:";
            }
        } else {
            System.out.println("无此用户");
            return "register";
        }
        return "login";
    }
}
