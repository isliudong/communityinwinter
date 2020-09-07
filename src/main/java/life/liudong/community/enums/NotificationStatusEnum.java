package life.liudong.community.enums;


/**
 * @author liudong
 */

public enum NotificationStatusEnum {
    /**
     * 通知状态
     */
    UNREAD(0),READ(1);
    private int status;

    public int getStatus() {
        return status;
    }

    NotificationStatusEnum(int status) {
        this.status = status;
    }
}

