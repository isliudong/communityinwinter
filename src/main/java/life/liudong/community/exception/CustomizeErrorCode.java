package life.liudong.community.exception;

/**
 * @author liudong
 */

public enum  CustomizeErrorCode implements ICustomizeErrorCode{
    /**
     * 错误类型
     */
    QUESTION_NOT_FOUND(2001,"你找的问题不存在了"),
    TARGET_PARAM_NOT_FOUND(2001,"未选中问题或评论"),
    NO_LOGIN(2003,"未登录，请先登录"),
    SYS_ERROR(2004,"服务器冒烟了，要不然一会儿再试试！"),
    TYPE_PARAM_WRONG(2005,"评论类型错误，或者找不到了"),
    COMMENT_NOT_FOUND(2006, "评论不存在了"),
    CONTENT_IS_EMPTY(2007,"内容为空"),
    READ_FAIL(2008,"兄弟你这是在看别人消息呢？"),
    SEND_MAIL_FILED(2010,"邮件发送失败"),
    NOTIFICATION_NOT_FOUND(2009,"莫非消息长翅膀了？"),
    ERROR_TO_LOGIN(500,"登录异常"), NULL_FILE(400, "文件为空"),
    NO_FILE_NAME(401, "文件名为空");
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
