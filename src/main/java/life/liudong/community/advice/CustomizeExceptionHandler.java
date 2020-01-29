package life.liudong.community.advice;

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
    ModelAndView handle(Throwable ex, Model model) {
        //HttpStatus status = getStatus(request);

        if (ex instanceof CustormizeException){
            model.addAttribute("message", ex.getMessage());
        }
        else {
            model.addAttribute("message","服务器冒烟了，要不然一会儿再试试！");
        }

        return new ModelAndView("error");
    }

}
