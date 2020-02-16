package life.liudong.community.dto;

import lombok.Data;

import java.util.List;

/**
 * @program: community
 * @description: 发布问题标签模型
 * @author: 闲乘月
 * @create: 2020-02-16 21:31
 **/
@Data
public class TagDTO {
    private String categoryName;
    private List<String> tag;
}
