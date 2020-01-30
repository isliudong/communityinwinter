package life.liudong.community.advice;

import life.liudong.community.dto.ResultDTO;
import life.liudong.community.exception.CustomizeErrorCode;
import life.liudong.community.exception.CustormizeException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import sun.applet.resources.MsgAppletViewer;

import javax.servlet.http.HttpServletRequest;
//全局异常切面
@ControllerAdvice
public class CustomizeExceptionHandler {
    @ExceptionHandler(Exception.class)
    Object handle(Throwable ex, Model model, HttpServletRequest request) {
        //HttpStatus status = getStatus(request);

        String contentType = request.getContentType();
        if (contentType.equals("application/json")){
            //请求接口错误返回json
            if (ex instanceof CustormizeException) {
                //已知异常，返回异常详情
                return ResultDTO.errorOf((CustormizeException) ex);
            }
            else {
                //未知异常，统一返回系统错误
                return ResultDTO.errorOf(CustomizeErrorCode.SYS_ERROR);
            }

        }
        else {
            //错误页面跳转
            if (ex instanceof CustormizeException) {
                model.addAttribute("message", ex.getMessage());
            } else {
                model.addAttribute("message", CustomizeErrorCode.SYS_ERROR.getMessage());
            }
            return new ModelAndView("error");
        }




    }

}
