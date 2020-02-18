package life.liudong.community.controller;

import life.liudong.community.cache.TagCache;
import life.liudong.community.dto.QuestionDTO;
import life.liudong.community.model.Question;
import life.liudong.community.model.User;
import life.liudong.community.service.QuestionService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Controller
public class PublishController {
    @Autowired
    private QuestionService questionService;
    @GetMapping("/publish")
    public String publish(Model model){
        model.addAttribute("tags",TagCache.get());
        return "publish";
    }

    @PostMapping("/publish")
    public String doPublish(
            @RequestParam(value = "title" ,required = false) String title,
            @RequestParam(value = "description",required = false) String description,
            @RequestParam(value = "tag",required = false) String tag,
            @RequestParam(value = "id",required = false) Long id,
            HttpServletRequest request,
            Model model
    ){

        model.addAttribute("title",title);
        model.addAttribute("description",description);
        model.addAttribute("tag",tag);
        model.addAttribute("tags",TagCache.get());

        if (title==null||title==""){
            model.addAttribute("error","标题不能为空");
            return "publish";
        }
        if (description==null||description==""){
            model.addAttribute("error","描述不能为空");
            return "publish";
        }
        if (tag==null||tag==""){
            model.addAttribute("error","标签不能为空");
            return "publish";
        }

        String inValid = TagCache.filterInValid(tag);
        if (StringUtils.isNotBlank(inValid)){
            model.addAttribute("error","不合理标签:"+inValid);
            return "publish";
        }

        User user=(User) request.getSession().getAttribute("user");
        if(user==null){
            model.addAttribute("error","未登录!");
            return "publish";
        }
        Question question = new Question();
        question.setDescription(description);
        question.setTag(tag);
        question.setCreator(user.getId());
        question.setTitle(title);
        question.setId(id);
        questionService.createOrUpdate(question);
        //ready to be beater

        return "redirect:";
    }

    @GetMapping("/publish/{id}")
    public String edit(@PathVariable(name = "id") Long id,Model model)
    {
        QuestionDTO question = questionService.getById(id);
        model.addAttribute("title",question.getTitle());
        model.addAttribute("description",question.getDescription());
        model.addAttribute("tag",question.getTag());
        model.addAttribute("id",question.getId());
        model.addAttribute("tags",TagCache.get());

        return "publish";
    }
}
