package life.liudong.community.exception;

public enum  CustomizeErrorCode implements ICustomizeErrorCode{
    QUESTION_NOT_FOUND(2001,"你找的问题不存在了"),
    TARGET_PARAM_NOT_FOUND(2001,"未选中问题或评论"),
    NO_LOGIN(2003,"未登录，请先登录"),
    SYS_ERROR(2004,"服务器冒烟了，要不然一会儿再试试！"),
    TYPE_PARAM_WRONG(2005,"评论类型错误，或者找不到了"),
    COMMENT_NOT_FOUND(2006, "评论不存在了");
    private String message;
    private Integer code;
    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public Integer getCode() {
        return code;
    }

    CustomizeErrorCode(Integer code, String message) {
        this.message = message;
        this.code = code;
    }

}
