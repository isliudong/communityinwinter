package life.liudong.community.dto;

import lombok.Data;


/**
 * @author liudong
 */
@Data
public class QuestionQueryDTO {
    private String search;
    private String tag;
    private Integer page;
    private Integer size;
}
