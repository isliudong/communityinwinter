package life.liudong.community.controller;

import life.liudong.community.dto.NotificationDTO;
import life.liudong.community.dto.PaginationDTO;
import life.liudong.community.dto.QuestionDTO;
import life.liudong.community.enums.NotificationTypeEnum;
import life.liudong.community.model.User;
import life.liudong.community.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;


/**
 * @author liudong
 */
@Controller
public class NotificationController {
    final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/notification/{id}")
    public String profile(@PathVariable(name = "id") Long id, HttpServletRequest request) {

        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            return "redirect:/";
        }
        NotificationDTO notificationDTO = notificationService.read(id, user);

        if (NotificationTypeEnum.REPLY_COMMENT.getType() == notificationDTO.getType()
                || NotificationTypeEnum.REPLY_QUESTION.getType() == notificationDTO.getType()) {
            return "redirect:/question/" + notificationDTO.getOuterId();
        } else {
            return "redirect:/";
        }
    }
}
