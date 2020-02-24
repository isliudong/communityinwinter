package life.liudong.community.dto;

import lombok.Data;

/**
 * @program: community
 * @description: 暂时为返回图片相关数据信息
 * @author: 闲乘月
 * @create: 2020-02-24 11:23
 **/
@Data
public class FileDTO {
    private int success;
    private String message;
    private String url;
}
