package life.liudong.community.exception;

public class CustormizeException extends RuntimeException{
    private String message;

    public CustormizeException(ICustomizeErrorCode errorCode){
        this.message=errorCode.getMessage();
    }

    public CustormizeException(String message){
        this.message=message;
    }
    @Override
    public String getMessage(){
        return message;
    }
}
