package life.liudong.community.service;

import life.liudong.community.enums.CommentTypeEnum;
import life.liudong.community.exception.CustomizeErrorCode;
import life.liudong.community.exception.CustormizeException;
import life.liudong.community.mapper.CommentMapper;
import life.liudong.community.mapper.QuestionExtMapper;
import life.liudong.community.mapper.QuestionMapper;
import life.liudong.community.model.Comment;
import life.liudong.community.model.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentService {

    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private QuestionExtMapper questionExtMapper;

    public void insert(Comment comment) {
        if (comment.getParentId()==null||comment.getParentId()==0){
            throw new CustormizeException(CustomizeErrorCode.TARGET_PARAM_NOT_FOUND);
        }

        if (comment.getType()==null|| !CommentTypeEnum.isExist(comment.getType())){
            throw new CustormizeException(CustomizeErrorCode.TYPE_PARAM_WRONG);
        }

        if (comment.getType()==CommentTypeEnum.COMMENT.getType()){
            //回复评论
            Comment dbComment = commentMapper.selectByPrimaryKey(comment.getParentId());
            if (dbComment!=null){
                commentMapper.insert(comment);
            }
            else {throw new CustormizeException(CustomizeErrorCode.COMMENT_NOT_FOUND);}

        }else {
            //回复问题
            Question question = questionMapper.selectByPrimaryKey(comment.getParentId());
            if (question!=null){
                commentMapper.insert(comment);
                question.setCommentCount(1);
                questionExtMapper.incCommentCount(question);
            }
            else {
                throw new CustormizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }

        }
    }
}
