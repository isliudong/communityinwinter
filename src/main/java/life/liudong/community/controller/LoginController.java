package life.liudong.community.controller;

import life.liudong.community.mapper.UserMapper;
import life.liudong.community.model.User;
import life.liudong.community.model.UserExample;
import life.liudong.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.UUID;

/**
 * @program: community
 * @description:
 * @author: 闲乘月
 * @create: 2020-03-09 12:33
 **/
@Controller
public class LoginController {
    @Autowired
    UserMapper userMapper;
    @Autowired
    UserService userService;


    @GetMapping("/login")
    public String login() {
        return "login2";
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
                request.getSession().setAttribute("user",user1);
                response.addCookie(new Cookie("token",user1.getToken()));
                return "redirect:";
            }
        }else {
            System.out.println("无此用户");
            return "register";
        }
        return "login2";
    }

    @RequestMapping("/register")
    public String register(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "password", required = false) String password,
            @RequestParam(value = "email", required = false) String email
    ){
        if (name==null)
            return "register";

        User newUser = new User();
        newUser.setToken(UUID.randomUUID().toString());
        newUser.setPassword(password);
        newUser.setMail(email);
        newUser.setName(name);
        newUser.setAvatarUrl("/img/默认头像.png");
        newUser.setAccountId("1000");//账号生成待完善
        userService.createOrUpdeate(newUser);

        return "redirect:/login";
    }
}
