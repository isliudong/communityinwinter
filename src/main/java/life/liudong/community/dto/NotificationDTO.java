package life.liudong.community.dto;

import life.liudong.community.model.User;
import lombok.Data;

/**
 * @program: community
 * @description: 通知数据交换模型
 * @author: 闲乘月
 * @create: 2020-02-20 13:12
 **/
@Data
public class NotificationDTO {
    private Long id;
    private Long gmtCreate;
    private Integer status;
    private Long notifier;
    private String notifierName;
    private String outerTitle;
    private Long outerId;
    private String typeName;
    private Integer type;
}
