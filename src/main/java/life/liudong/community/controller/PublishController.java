package life.liudong.community.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PublishController {
    @RequestMapping("/publish")
    public String publish(){
        return "publish";
    }
}
