package life.liudong.community.mapper;

import life.liudong.community.dto.QuestionQueryDTO;
import life.liudong.community.model.Question;
import org.springframework.stereotype.Component;

import java.util.List;
/**
 * @author liudong
 */
@Component
public interface QuestionExtMapper {
    /**
     * 增加浏览数
     * @param record Question
     * @return int
     */
    int incView(Question record);

    /**
     * 增加评论数
     * @param record Question
     * @return int
     */
    int incCommentCount(Question record);

    /**
     * 查询相关问题
     * @param question Question
     * @return List<Question>
     */
    List<Question> selectRelated(Question question);

    /**
     * 查询问题总数
     * @param questionQueryDTO QuestionQueryDTO
     * @return Integer
     */
    Integer countBySearch(QuestionQueryDTO questionQueryDTO);

    /**
     * 查询问题
     * @param questionQueryDTO QuestionQueryDTO
     * @return List<Question>
     */
    List<Question> selectBySearch(QuestionQueryDTO questionQueryDTO);
}