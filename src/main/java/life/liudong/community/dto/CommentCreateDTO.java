package life.liudong.community.dto;

import lombok.Data;

import javax.validation.constraints.Size;

/**
 * @author liudong
 */
@Data
public class CommentCreateDTO {
    private Long parentId;
    @Size(max = 500)
    private String content;
    private Integer type;
}
