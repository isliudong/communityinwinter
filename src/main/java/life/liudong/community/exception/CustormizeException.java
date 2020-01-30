package life.liudong.community.exception;

public class CustormizeException extends RuntimeException {
    private String message;
    private Integer code;

    public CustormizeException(ICustomizeErrorCode errorCode) {
        this.message = errorCode.getMessage();
        this.code = errorCode.getCode();
    }

    @Override
    public String getMessage() {
        return message;
    }

    public Integer getCode() {
        return code;
    }
}
