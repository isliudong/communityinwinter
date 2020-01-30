package life.liudong.community.service;

import life.liudong.community.exception.CustomizeErrorCode;
import life.liudong.community.exception.CustormizeException;
import life.liudong.community.model.Comment;
import org.springframework.stereotype.Service;

@Service
public class CommentService {

    public void insert(Comment comment) {
        if (comment.getParentId()==null||comment.getParentId()==0){
            throw new CustormizeException(CustomizeErrorCode.TARGET_PARAM_NOT_FOUND);
        }
    }
}
