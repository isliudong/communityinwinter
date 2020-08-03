package life.liudong.community.controller;

import life.liudong.community.cache.HotTagCache;
import life.liudong.community.cache.RedisOP;
import life.liudong.community.dto.PaginationDTO;
import life.liudong.community.dto.QuestionDTO;
import life.liudong.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.List;

@Controller
public class IndexController {


    @Autowired
    QuestionService questionService;
    @Autowired
    HotTagCache hotTagCache;
    @Autowired
    RedisOP<PaginationDTO<QuestionDTO>> redisOP;
    @Value("${github.client.id}")
    String clientId;
    @Value("${github.redirect.url}")
    String redirect_url;
    @GetMapping("/")
    public String index(Model model,
                        @RequestParam(name = "page",defaultValue = "1") Integer page,
                        @RequestParam(name = "size",defaultValue = "5") Integer size,
                        @RequestParam(name="search",required = false) String search,
                        @RequestParam(name="tag",required = false) String tag)
    {

        PaginationDTO<QuestionDTO> pagination=null;
        if (page==1&&search==null&&tag==null){
            try {
                pagination=questionService.getPageByIdInRedis(page);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }else {pagination=questionService.list(search,tag,page,size);}

        List<String> topTags = hotTagCache.getHots();
        model.addAttribute("pagination",pagination);
        model.addAttribute("search",search);
        model.addAttribute("clientId",clientId);
        model.addAttribute("redirect_url",redirect_url);
        model.addAttribute("topTags",topTags);
        model.addAttribute("tag",tag);

        return "index";
    }
}
