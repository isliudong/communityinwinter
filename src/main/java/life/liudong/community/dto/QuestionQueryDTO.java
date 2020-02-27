package life.liudong.community.dto;

import lombok.Data;

/**
 * @program: community
 * @description: 问题查询信息携带模型
 * @author: 闲乘月
 * @create: 2020-02-27 11:24
 **/
@Data
public class QuestionQueryDTO {
    private String search;
    private Integer page;
    private Integer size;
}
