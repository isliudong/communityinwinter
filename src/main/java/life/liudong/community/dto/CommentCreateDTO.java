package life.liudong.community.dto;

import lombok.Data;

/**
 * @author liudong
 */
@Data
public class CommentCreateDTO {
    private Long parentId;
    private String content;
    private Integer type;
}
