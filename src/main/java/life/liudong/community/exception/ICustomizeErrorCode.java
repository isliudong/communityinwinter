package life.liudong.community.exception;

public interface ICustomizeErrorCode {
    /**
     * 获取错误信息
     * @return  Message
     */
    String getMessage();

    /**
     * 错误代码
     * @return  Code
     */
    Integer getCode();
}
