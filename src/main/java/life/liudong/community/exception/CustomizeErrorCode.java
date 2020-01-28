package life.liudong.community.exception;

public enum  CustomizeErrorCode implements ICustomizeErrorCode{
    QUESTION_nOT_FOUND("你找的问题不存在了");

    @Override
    public String getMessage() {
        return message;
    }

    private String message;
    CustomizeErrorCode(String message) {
        this.message = message;
    }
}
