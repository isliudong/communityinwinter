package life.liudong.community.controller;

import life.liudong.community.cache.HotTagCache;
import life.liudong.community.cache.RedisOp;
import life.liudong.community.dto.PaginationDTO;
import life.liudong.community.dto.QuestionDTO;
import life.liudong.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.List;

/**
 * @author liudong
 */
@Controller
public class IndexController {


    final
    QuestionService questionService;
    final
    HotTagCache hotTagCache;
    final
    RedisOp<PaginationDTO<QuestionDTO>> redisOp;

    public IndexController(QuestionService questionService, HotTagCache hotTagCache, RedisOp<PaginationDTO<QuestionDTO>> redisOp) {
        this.questionService = questionService;
        this.hotTagCache = hotTagCache;
        this.redisOp = redisOp;
    }

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
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }else {pagination=questionService.list(search,tag,page,size);}

        List<String> topTags = hotTagCache.getHots();
        model.addAttribute("pagination",pagination);
        model.addAttribute("search",search);
        model.addAttribute("topTags",topTags);
        model.addAttribute("tag",tag);

        return "index";
    }
}
