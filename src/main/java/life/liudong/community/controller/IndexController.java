package life.liudong.community.controller;

import life.liudong.community.dto.PaginationDTO;
import life.liudong.community.service.QuestionService;
import life.liudong.community.mapper.UserMapper;
import life.liudong.community.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

@Controller
public class IndexController {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    QuestionService questionService;
    @GetMapping("/")
    public String index(HttpServletRequest request, Model model,
                        @RequestParam(name = "page",defaultValue = "1") Integer page,
                        @RequestParam(name = "size",defaultValue = "5") Integer size)
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


        PaginationDTO pagination=questionService.list(page,size);

        model.addAttribute("pagination",pagination);


        return "index";
    }
}
