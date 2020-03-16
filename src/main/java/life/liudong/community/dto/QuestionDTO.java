package life.liudong.community.dto;

import life.liudong.community.model.User;
import lombok.Data;

import java.io.Serializable;

@Data
public class QuestionDTO implements Serializable {
    private Long id;
    private String title;
    private String description;
    private String tag;
    private Long gmtCreate;
    private Long gmtModified;
    private Long creator;
    private Integer viewCount;
    private Integer likeCount;
    private Integer commentCount;
    private User user;
}
