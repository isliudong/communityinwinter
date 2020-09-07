package life.liudong.community.controller;

import life.liudong.community.dto.CommentDTO;
import life.liudong.community.dto.QuestionDTO;
import life.liudong.community.enums.CommentTypeEnum;
import life.liudong.community.service.CommentService;
import life.liudong.community.service.QuestionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * @author liudong
 */
@Controller
public class QuestionController {
    private final QuestionService questionService;

    private final CommentService commentService;

    public QuestionController(QuestionService questionService, CommentService commentService) {
        this.questionService = questionService;
        this.commentService = commentService;
    }


    @GetMapping("/question/{id}")
    public String question(@PathVariable(name = "id") Long id, Model model) {

        QuestionDTO questionDTO = questionService.getById(id);
        List<QuestionDTO> relatedQuestions = questionService.selectRelated(questionDTO);
        List<CommentDTO> comments = commentService.listByTargetId(id, CommentTypeEnum.QUESTION);


        //增加阅读
        questionService.incView(id);
        model.addAttribute("question", questionDTO);
        model.addAttribute("comments", comments);
        model.addAttribute("relatedQuestions", relatedQuestions);
        return "question";
    }
}
