package life.liudong.community.controller;

import life.liudong.community.mapper.QuestionMapper;
import life.liudong.community.mapper.UserMapper;
import life.liudong.community.model.Question;
import life.liudong.community.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

@Controller
public class PublishController {
    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private UserMapper userMapper;

    @GetMapping("/publish")
    public String publish(){
        return "publish";
    }

    @PostMapping("/publish")
    public String doPublish(
            @RequestParam(value = "title" ,required = false) String title,
            @RequestParam(value = "description",required = false) String description,
            @RequestParam(value = "tag",required = false) String tag,
            HttpServletRequest request,
            Model model
    ){

        model.addAttribute("title",title);
        model.addAttribute("description",description);
        model.addAttribute("tag",tag);

        if (title==null||title==""){
            model.addAttribute("error","标题不能为空");
            return "publish";
        }
        if (description==null||description==""){
            model.addAttribute("error","描述不能为空");
            return "publish";
        }
        if (tag==null||tag==""){
            model.addAttribute("error","标签不能为空");
            return "publish";
        }

        User user = null;
        Cookie[] cookies = request.getCookies();
        if(cookies!=null)
            for(Cookie cookie:cookies)
            {
                if(cookie.getName().equals("token"))
                {
                    String token=cookie.getValue();
                    user=userMapper.findByToken(token);
                    if(user!=null)
                    {
                        request.getSession().setAttribute("user",user);
                    }
                    break;
                }
            }
        if(user==null){
            model.addAttribute("error","未登录!");
            return "publish";
        }
        Question question = new Question();
        question.setDescription(description);
        question.setTag(tag);
        question.setCreator(user.getId());
        question.setGmt_create(System.currentTimeMillis());
        question.setGmt_modified(question.getGmt_create());
        question.setTitle(title);
        questionMapper.create(question);
        //ready to be beater

        return "redirect:";
    }
}
