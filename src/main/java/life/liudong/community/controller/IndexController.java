package life.liudong.community.controller;

import life.liudong.community.cache.HotTagCache;
import life.liudong.community.dto.PaginationDTO;
import life.liudong.community.dto.QuestionDTO;
import life.liudong.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@Controller
public class IndexController {

    @Autowired
    QuestionService questionService;
    @Autowired
    HotTagCache hotTagCache;
    @Value("${github.client.id}")
    String clientId;
    @GetMapping("/")
    public String index(Model model,
                        @RequestParam(name = "page",defaultValue = "1") Integer page,
                        @RequestParam(name = "size",defaultValue = "5") Integer size,
                        @RequestParam(name="search",required = false) String search,
                        @RequestParam(name="tag",required = false) String tag)
    {

        PaginationDTO<QuestionDTO> pagination=questionService.list(search,tag,page,size);
        List<String> topTags = hotTagCache.getHots();
        model.addAttribute("pagination",pagination);
        model.addAttribute("search",search);
        model.addAttribute("clientId",clientId);
        model.addAttribute("topTags",topTags);
        model.addAttribute("tag",tag);

        return "index";
    }
}
