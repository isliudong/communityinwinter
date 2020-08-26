package life.liudong.community.enums;

/**
 * @author liudong
 */

public enum CommentTypeEnum {
    /**
     * 回复类型
     */
    QUESTION(1),
    COMMENT(2);


    private Integer type;

    CommentTypeEnum(Integer type) {
        this.type = type;
    }

    public static boolean isExist(Integer type) {
        for (CommentTypeEnum commentTypeEnum : CommentTypeEnum.values()) {
            if (commentTypeEnum.getType().equals(type)){
                return true;
            }
        }
        return false;

    }

    public Integer getType() {
        return type;
    }
}
