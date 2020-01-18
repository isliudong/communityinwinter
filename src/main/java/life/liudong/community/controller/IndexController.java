package life.liudong.community.controller;

import life.liudong.community.controller.service.QuestionService;
import life.liudong.community.dto.QuestionDTO;
import life.liudong.community.mapper.QuestionMapper;
import life.liudong.community.mapper.UserMapper;
import life.liudong.community.model.Question;
import life.liudong.community.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class IndexController {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    QuestionService questionService;
    @GetMapping("/")
    public String index(HttpServletRequest request,Model model)
    {
        Cookie[] cookies = request.getCookies();
        if(cookies!=null)
        for(Cookie cookie:cookies)
        {
            if(cookie.getName().equals("token"))
            {
                String token=cookie.getValue();
                User user=userMapper.findByToken(token);
                if(user!=null)
                {
                    request.getSession().setAttribute("user",user);
                }
                break;
            }
        }


        List<QuestionDTO> questionList=questionService.list();

        model.addAttribute("questions",questionList);


        return "index";
    }
}
