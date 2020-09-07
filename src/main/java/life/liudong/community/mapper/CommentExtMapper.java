package life.liudong.community.mapper;

import life.liudong.community.model.Comment;
import life.liudong.community.model.CommentExample;
import life.liudong.community.model.Question;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Component;

import java.util.List;
/**
 * @author liudong
 */
@Component
public interface CommentExtMapper {
    /**
     * 增加评论数
     * @param comment Comment
     * @return int
     */
    int incCommentCount(Comment comment);
}