package life.liudong.community.service;

import life.liudong.community.dto.CommentDTO;
import life.liudong.community.enums.CommentTypeEnum;
import life.liudong.community.exception.CustomizeErrorCode;
import life.liudong.community.exception.CustormizeException;
import life.liudong.community.mapper.CommentMapper;
import life.liudong.community.mapper.QuestionExtMapper;
import life.liudong.community.mapper.QuestionMapper;
import life.liudong.community.mapper.UserMapper;
import life.liudong.community.model.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CommentService {

    @Autowired(required = false)
    private CommentMapper commentMapper;
    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private QuestionExtMapper questionExtMapper;
    @Autowired
    private UserMapper userMapper;

    @Transactional//开启事务
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

    public List<CommentDTO> listByQuestionId(Long id) {
        CommentExample example = new CommentExample();
        example.createCriteria().andParentIdEqualTo(id).andTypeEqualTo(CommentTypeEnum.QUESTION.getType());//type为question类型，回复有两种类型，此处为问题回复
        //倒序
        example.setOrderByClause("gmt_create desc");
        List<Comment> commentList = commentMapper.selectByExample(example);

        if (commentList.size()==0){
            return new ArrayList<>();
        }
        //获取去重评论人id
        //Java8语法：集合流式处理,不采用QuestionService中遍历question寻找UserId的方法（费时）
        Set<Long> commentators = commentList.stream().map(comment -> comment.getCommentator()).collect(Collectors.toSet());
        List<Long> userIds=new ArrayList<>(commentators);

        //获取评论user对象转换为map
        UserExample userExample = new UserExample();
        userExample.createCriteria().andIdIn(userIds);
        List<User> users = userMapper.selectByExample(userExample);
        Map<Long, User> userMap = users.stream().collect(Collectors.toMap(user -> user.getId(), user -> user));

        //将构造commentDTO
        List<CommentDTO> commentDTOList = commentList.stream().map(comment -> {
            CommentDTO commentDTO = new CommentDTO();
            BeanUtils.copyProperties(comment,commentDTO);
            commentDTO.setUser(userMap.get(comment.getCommentator()));
            return commentDTO;
        }).collect(Collectors.toList());
        return commentDTOList;
    }
}
