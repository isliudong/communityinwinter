package life.liudong.community.controller;

import com.alibaba.fastjson.JSONObject;
import life.liudong.community.dto.ResultDTO;
import life.liudong.community.model.User;
import life.liudong.community.service.UserService;
import life.liudong.community.utl.Mail;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.util.UUID;


/**
 * @author liudong
 */
@Controller
public class RegisterController {
    final
    UserService userService;
    final
    Mail mail;

    int emailCode = 1234;

    public RegisterController(UserService userService, Mail mail) {
        this.userService = userService;
        this.mail = mail;
    }

    @RequestMapping("/register")
    public String register(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "password", required = false) String password,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "code", required = false) String code
    ) {
        if (code != null && code.equals(String.valueOf(this.emailCode))) {
            User newUser = new User();
            newUser.setToken(UUID.randomUUID().toString());
            newUser.setPassword(password);
            newUser.setMail(email);
            newUser.setName(name);
            newUser.setAvatarUrl("/img/默认头像.png");
            long accountId = userService.getUserCount() + 1000;
            newUser.setAccountId(String.valueOf(accountId));
            userService.createOrUpdate(newUser);
            return "redirect:/login";
        }

        return "login";
    }

    /**注册信息验证,未验证邮箱重复*/
    @ResponseBody
    @PostMapping("/verify")
    public ResultDTO verify(@RequestBody JSONObject jsonObject) {

        String username = jsonObject.getString("username");
        if (userService.nameAvailable(username) && !"".equals(username)) {
            return ResultDTO.okOf(200, "昵称合法");
        }

        return ResultDTO.okOf(200, "昵称重复或者不合法");
    }

    //发送邮件验证码
    @PostMapping("/getMailCode")
    @ResponseBody
    public ResultDTO sendMailCode(@RequestBody JSONObject jsonObject) throws MessagingException {
        int code = emailCode;
        String userMail = jsonObject.getString("userMail");
        String content = "验证码为：" + code;
        mail.setContent("西柚社区", "");
        mail.send("2351036454@qq.com", userMail, "西柚社区注册验证", content, "D:\\wintervacation-2020\\community\\src\\main\\resources\\static\\img\\心情童年.jpg");
        return ResultDTO.okOf(200, "send mail success");
    }

}
