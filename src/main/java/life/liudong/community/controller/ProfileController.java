package life.liudong.community.controller;

import life.liudong.community.dto.NotificationDTO;
import life.liudong.community.dto.PaginationDTO;
import life.liudong.community.dto.QuestionDTO;
import life.liudong.community.model.Notification;
import life.liudong.community.model.User;
import life.liudong.community.service.NotificationService;
import life.liudong.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Controller
public class ProfileController {
    @Autowired
    QuestionService questionService;
    @Autowired
    NotificationService notificationService;

    @GetMapping("/profile/{action}")//动态路径
    public String profile(@PathVariable(name = "action") String action, Model model,
                          HttpServletRequest request,
                          @RequestParam(name = "page",defaultValue = "1") Integer page,
                          @RequestParam(name = "size",defaultValue = "5") Integer size){



        User user=(User) request.getSession().getAttribute("user");
        if (user==null)
        {
            return "redirect:/";
        }
        if("questions".equals(action))
        {
            model.addAttribute("section","questions");
            model.addAttribute("sectionName","我的提问");
            PaginationDTO<QuestionDTO> pagination=questionService.list(user.getId(),page,size);
            model.addAttribute("pagination",pagination);
        }
        else if ("replies".equals(action))
        {
            PaginationDTO<NotificationDTO> pagination=notificationService.list(user.getId(),page,size);
            Long unreadCount=notificationService.unreadCount(user.getId());
            model.addAttribute("pagination",pagination);
            model.addAttribute("section","replies");
            model.addAttribute("sectionName","我的回复");
            model.addAttribute("unreadCount",unreadCount);
        }



        return "profile";
    }
}
