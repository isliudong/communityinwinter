package life.liudong.community.service;

import life.liudong.community.dto.CommentDTO;
import life.liudong.community.enums.CommentTypeEnum;
import life.liudong.community.enums.NotificationStatusEnum;
import life.liudong.community.enums.NotificationTypeEnum;
import life.liudong.community.exception.CustomizeErrorCode;
import life.liudong.community.exception.CustomizeException;
import life.liudong.community.mapper.*;
import life.liudong.community.model.*;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author liudong
 */
@Service
public class CommentService {

    private final CommentMapper commentMapper;
    private final QuestionMapper questionMapper;
    private final QuestionExtMapper questionExtMapper;
    private final UserMapper userMapper;
    private final CommentExtMapper commentExtMapper;
    private final NotificationMapper notificationMapper;

    public CommentService(CommentMapper commentMapper, QuestionMapper questionMapper, QuestionExtMapper questionExtMapper, UserMapper userMapper, CommentExtMapper commentExtMapper, NotificationMapper notificationMapper) {
        this.commentMapper = commentMapper;
        this.questionMapper = questionMapper;
        this.questionExtMapper = questionExtMapper;
        this.userMapper = userMapper;
        this.commentExtMapper = commentExtMapper;
        this.notificationMapper = notificationMapper;
    }

    /**开启事务*/
    @Transactional(rollbackFor = Exception.class)
    public void insert(Comment comment, User commentator) {
        if (comment.getParentId() == null || comment.getParentId() == 0) {
            throw new CustomizeException(CustomizeErrorCode.TARGET_PARAM_NOT_FOUND);
        }

        if (comment.getType() == null || !CommentTypeEnum.isExist(comment.getType())) {
            throw new CustomizeException(CustomizeErrorCode.TYPE_PARAM_WRONG);
        }

        if (comment.getType().equals(CommentTypeEnum.COMMENT.getType())) {
            //回复评论
            Comment dbComment = commentMapper.selectByPrimaryKey(comment.getParentId());
            if (dbComment != null) {
                commentMapper.insert(comment);
                Comment parentComment = new Comment();
                //增加评论数
                parentComment.setId(comment.getParentId());
                parentComment.setCommentCount(1);
                commentExtMapper.incCommentCount(parentComment);
                //添加通知
                Question question = questionMapper.selectByPrimaryKey(dbComment.getParentId());
                if (question == null) {
                    throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
                }
                createNotification(comment, dbComment.getCommentator(), commentator.getName(), question.getTitle(), NotificationTypeEnum.REPLY_COMMENT, question.getId());


            } else {
                throw new CustomizeException(CustomizeErrorCode.COMMENT_NOT_FOUND);
            }

        } else {
            //回复问题
            Question question = questionMapper.selectByPrimaryKey(comment.getParentId());
            if (question != null) {
                commentMapper.insert(comment);
                question.setCommentCount(1);
                questionExtMapper.incCommentCount(question);
                //创建通知
                createNotification(comment, question.getCreator(), commentator.getName(), question.getTitle(), NotificationTypeEnum.REPLY_QUESTION, question.getId());
            } else {
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }

        }
    }

    /**创建评论通知*/
    private void createNotification(Comment comment, Long receiver, String notifierName, String outerTitle, NotificationTypeEnum notificationType, Long outerId) {
        if (receiver.equals(comment.getCommentator())){
            return;
        }
        Notification notification = new Notification();
        notification.setGmtCreate(System.currentTimeMillis());
        notification.setType(notificationType.getType());
        notification.setOuterId(outerId);
        notification.setNotifier(comment.getCommentator());
        notification.setStatus(NotificationStatusEnum.UNREAD.getStatus());
        notification.setReceiver(receiver);
        notification.setNotifierName(notifierName);
        notification.setOuterTitle(outerTitle);
        notificationMapper.insert(notification);
    }

    public List<CommentDTO> listByTargetId(Long id, CommentTypeEnum type) {
        CommentExample example = new CommentExample();
        //type为question类型，回复有两种类型，此处为问题回复
        example.createCriteria().andParentIdEqualTo(id).andTypeEqualTo(type.getType());
        //倒序
        example.setOrderByClause("gmt_create desc");
        List<Comment> commentList = commentMapper.selectByExample(example);

        if (commentList.size() == 0) {
            return new ArrayList<>();
        }
        //获取去重评论人id
        //Java8语法：集合流式处理,不采用QuestionService中遍历question寻找UserId的方法（费时）
        List<Long> userIds = commentList.stream().map(Comment::getCommentator).distinct().collect(Collectors.toList());

        //获取评论user对象转换为map
        UserExample userExample = new UserExample();
        userExample.createCriteria().andIdIn(userIds);
        List<User> users = userMapper.selectByExample(userExample);
        Map<Long, User> userMap = users.stream().collect(Collectors.toMap(User::getId, user -> user));

        //将构造commentDTO
        return commentList.stream().map(comment -> {
            CommentDTO commentDTO = new CommentDTO();
            BeanUtils.copyProperties(comment, commentDTO);
            commentDTO.setUser(userMap.get(comment.getCommentator()));
            return commentDTO;
        }).collect(Collectors.toList());
    }
}
