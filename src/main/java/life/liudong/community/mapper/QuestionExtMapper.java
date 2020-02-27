package life.liudong.community.mapper;

import life.liudong.community.dto.QuestionQueryDTO;
import life.liudong.community.model.Question;

import java.util.List;

public interface QuestionExtMapper {
    //增加浏览数
    int incView(Question record);
    //增加评论数
    int incCommentCount(Question record);
    //查询相关问题
    List<Question> selectRelated(Question question);

    Integer countBySearch(QuestionQueryDTO questionQueryDTO);

    List<Question> selectBySearch(QuestionQueryDTO questionQueryDTO);
}