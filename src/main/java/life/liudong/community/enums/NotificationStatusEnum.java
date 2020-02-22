package life.liudong.community.enums;

/**
 * @program: community
 * @description: 通知状态枚举
 * @author: 闲乘月
 * @create: 2020-02-20 11:22
 **/
public enum NotificationStatusEnum {
    UNREAD(0),READ(1);
    private int status;

    public int getStatus() {
        return status;
    }

    NotificationStatusEnum(int status) {
        this.status = status;
    }
}

