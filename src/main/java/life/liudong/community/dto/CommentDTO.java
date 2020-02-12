package life.liudong.community.dto;

import life.liudong.community.model.User;
import lombok.Data;

/**
 * @program: community
 * @description:
 * @author: 闲乘月
 * @create: 2020-02-07 16:05
 **/
@Data
public class CommentDTO {
    private Long id;
    private Long parentId;
    private Integer type;
    private Long commentator;
    private Long gmtCreate;
    private Long gmtModified;
    private Long likeCount;
    private String content;
    private User user;
    private Integer commentCount;

}
