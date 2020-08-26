package life.liudong.community.enums;

/**
 * @program: community
 * @description: 通知消息类型枚举
 * @author: 闲乘月
 * @create: 2020-02-20 11:01
 **/
public enum NotificationTypeEnum {
    /**
     * 评论对象类型
     */
    REPLY_QUESTION(1, "回复了问题"),
    REPLY_COMMENT(2, "回复了评论");
    private int type;
    private String name;

    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    NotificationTypeEnum(int type, String name) {
        this.type = type;
        this.name = name;
    }

    public static String nameOfType(int type) {
        for (NotificationTypeEnum notificationTypeEnum : NotificationTypeEnum.values()) {
            if (notificationTypeEnum.getType() == type) {
                return notificationTypeEnum.getName();
            }
        }
        //不会执行到这
        return "";
    }
}
