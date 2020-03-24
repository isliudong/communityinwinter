package life.liudong.community.controller;

import com.alibaba.fastjson.JSONObject;
import life.liudong.community.dto.ResultDTO;
import life.liudong.community.model.User;
import life.liudong.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * @program: community
 * @description:
 * @author: 闲乘月
 * @create: 2020-03-24 12:19
 **/
@Controller
public class RegisterController {
    @Autowired
    UserService userService;

    @RequestMapping("/register")
    public String register(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "password", required = false) String password,
            @RequestParam(value = "email", required = false) String email
    ){
        if (name==null) {
            return "register";
        }
        User newUser = new User();
        newUser.setToken(UUID.randomUUID().toString());
        newUser.setPassword(password);
        newUser.setMail(email);
        newUser.setName(name);
        newUser.setAvatarUrl("/img/默认头像.png");
        long accountId=userService.getUserCount()+1000;
        newUser.setAccountId(String.valueOf(accountId));
        userService.createOrUpdeate(newUser);

        return "redirect:/login";
    }

    @ResponseBody
    @PostMapping("/verify")//未验证邮箱重复
    public ResultDTO verify(@RequestBody JSONObject jsonObject) {

        String username = jsonObject.getString("username");
        if (userService.nameAvailable(username)&& !username.equals("")){
            return ResultDTO.okOf(200,"昵称合法");
        }

        return ResultDTO.okOf(200,"昵称重复或者不合法");
    }

}
