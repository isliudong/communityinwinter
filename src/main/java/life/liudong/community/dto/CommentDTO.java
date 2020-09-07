package life.liudong.community.dto;

import life.liudong.community.model.User;
import lombok.Data;


/**
 * @author liudong
 */
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
