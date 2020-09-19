package life.liudong.community.controller;

import life.liudong.community.dto.PaginationDTO;
import life.liudong.community.dto.QuestionDTO;
import life.liudong.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;


/**
 * @author liudong
 */
@Controller
public class TestController {

    final
    QuestionService questionService;
    final
    RedisTemplate<String, String> redisTemplate;

    public TestController(QuestionService questionService, RedisTemplate<String, String> redisTemplate) {
        this.questionService = questionService;
        this.redisTemplate = redisTemplate;
    }

    @GetMapping("/test")
    public String htmlTest(Model model,
                           @RequestParam(name = "page", defaultValue = "1") Integer page,
                           @RequestParam(name = "size", defaultValue = "5") Integer size,
                           @RequestParam(name = "search", required = false) String search) {

        PaginationDTO<QuestionDTO> pagination = questionService.list(search, "", page, size);
        model.addAttribute("pagination", pagination);
        model.addAttribute("search", search);
        redisTemplate.opsForValue().set("test1", "redis值");
        System.out.println("redis获取成功：  " + redisTemplate.opsForValue().get("test1"));
        return "index";
    }


    @RequestMapping("/ajax")
    public String ajax(@RequestBody(required = false) String name, HttpServletResponse response) {
        System.out.println(name);
        System.out.println(response);
        return "ajaxTest";
    }

    @RequestMapping("/me")
    public String face() {
        return "me";
    }
}
