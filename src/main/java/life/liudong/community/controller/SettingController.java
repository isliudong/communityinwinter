package life.liudong.community.controller;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

import life.liudong.community.mapper.UserMapper;
import life.liudong.community.model.User;
import life.liudong.community.model.UserExample;
import life.liudong.community.service.UserService;
import life.liudong.community.utl.FileHelper;
import life.liudong.community.utl.UserHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


/**
 * @author liudong
 */
@Controller
@RequestMapping("/setting")
public class SettingController {
    @Autowired
    UserService userService;
    @Autowired
    UserMapper userMapper;
    @PostMapping("/update")
    public String updateUser(User user, @RequestParam("img")MultipartFile img, HttpServletRequest request){
        User currentUser = UserHelper.currentUser(request);
        if(Objects.nonNull(currentUser)){
            String avatarUrl = FileHelper.saveImg(img);
            user.setAvatarUrl(avatarUrl);
            userService.updateByAccountId(currentUser.getAccountId(),user);
        }
        return "me";
    }

    @RequestMapping("/info")
    public String face(Model model, HttpServletRequest request) {
        User currentUser = UserHelper.currentUser(request);
        if(Objects.nonNull(currentUser)){
            UserExample example = new UserExample();
            example.createCriteria().andAccountIdEqualTo(currentUser.getAccountId());
            model.addAttribute("user",userMapper.selectByExample(example).get(0));
        }
        return "me";
    }

    @GetMapping("/personal/{action}")
    public String personal(@PathVariable String action,Model model,HttpServletRequest request){
        User currentUser = UserHelper.currentUser(request);
        if(Objects.nonNull(currentUser)){
            UserExample example = new UserExample();
            example.createCriteria().andAccountIdEqualTo(currentUser.getAccountId());
            model.addAttribute("user",userMapper.selectByExample(example).get(0));
        }
        model.addAttribute("section", action);
        return "me";
    }
}
