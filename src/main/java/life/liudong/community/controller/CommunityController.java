package life.liudong.community.controller;

import life.liudong.community.cache.HotTagCache;
import life.liudong.community.cache.RedisOp;
import life.liudong.community.dto.PaginationDTO;
import life.liudong.community.dto.QuestionDTO;
import life.liudong.community.service.QuestionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.List;

/**
 * @author 28415@hand-china.com 2020/11/25 19:15
 */
@Controller
@RequestMapping("/community")
public class CommunityController {
    final QuestionService questionService;
    final HotTagCache hotTagCache;
    final RedisOp<PaginationDTO<QuestionDTO>> redisOp;

    public CommunityController(QuestionService questionService,
                               HotTagCache hotTagCache,
                               RedisOp<PaginationDTO<QuestionDTO>> redisOp) {
        this.questionService = questionService;
        this.hotTagCache = hotTagCache;
        this.redisOp = redisOp;
    }

    @GetMapping
    public String community(Model model,
                        @RequestParam(name = "page",defaultValue = "1") Integer page,
                        @RequestParam(name = "size",defaultValue = "5") Integer size,
                        @RequestParam(name="search",required = false) String search,
                        @RequestParam(name="tag",required = false) String tag)
    {

        PaginationDTO<QuestionDTO> pagination;
        pagination=questionService.list(search,tag,page,size);

        List<String> topTags = hotTagCache.getHots();
        model.addAttribute("pagination",pagination);
        model.addAttribute("search",search);
        model.addAttribute("topTags",topTags);
        model.addAttribute("tag",tag);

        return "community";
    }

}
