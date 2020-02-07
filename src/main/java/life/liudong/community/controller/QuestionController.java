package life.liudong.community.controller;

import life.liudong.community.dto.CommentCreateDTO;
import life.liudong.community.dto.CommentDTO;
import life.liudong.community.dto.QuestionDTO;
import life.liudong.community.service.CommentService;
import life.liudong.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class QuestionController {
    @Autowired
    private QuestionService questionService;

    @Autowired
    private CommentService commentService;


    @GetMapping("/question/{id}")
    public String question(@PathVariable(name = "id") Long id, Model model){

        QuestionDTO questionDTO=questionService.getById(id);
        List<CommentDTO> comments=commentService.listByQuestionId(id);


        //增加阅读
        questionService.incView(id);
        model.addAttribute("question",questionDTO);
        model.addAttribute("comments",comments);
        return "question";
    }
}
