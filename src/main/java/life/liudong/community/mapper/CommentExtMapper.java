package life.liudong.community.mapper;

import life.liudong.community.model.Comment;
import life.liudong.community.model.CommentExample;
import life.liudong.community.model.Question;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

public interface CommentExtMapper {
    int incCommentCount(Comment comment);
}