package life.liudong.community.dto;

import lombok.Data;

import java.util.List;


/**
 * @author liudong
 */
@Data
public class TagDTO {
    private String categoryName;
    private List<String> tag;
}
