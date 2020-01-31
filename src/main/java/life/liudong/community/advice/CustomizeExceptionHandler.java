package life.liudong.community.advice;

import com.alibaba.fastjson.JSON;
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
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PipedWriter;
import java.io.PrintWriter;

//全局异常切面
@ControllerAdvice
public class CustomizeExceptionHandler {


    @ExceptionHandler(Exception.class)
    Object handle(Throwable ex, Model model, HttpServletRequest request, HttpServletResponse response) {
        //HttpStatus status = getStatus(request);

        String contentType = request.getContentType();
        if (contentType.equals("application/json")){
            ResultDTO resultDTO;
            //请求接口错误返回json
            if (ex instanceof CustormizeException) {
                //已知异常，返回异常详情
                resultDTO= (ResultDTO) ResultDTO.errorOf((CustormizeException) ex);
            }
            else {
                //未知异常，统一返回系统错误
                resultDTO= (ResultDTO) ResultDTO.errorOf(CustomizeErrorCode.SYS_ERROR);
            }

            try {
                //基本方法实现接口错误写入json返回错误信息，而不是跳转页面
                response.setContentType("application/json");
                response.setStatus(200);
                response.setCharacterEncoding("utf-8");
                PrintWriter writer=response.getWriter();
                writer.write(JSON.toJSONString(resultDTO));
                writer.close();
            } catch (IOException ioe) {
                //e.printStackTrace();
            }
            return null;

        }
        else {
            //非接口错误跳转页面
            if (ex instanceof CustormizeException) {
                model.addAttribute("message", ex.getMessage());
            } else {
                model.addAttribute("message", CustomizeErrorCode.SYS_ERROR.getMessage());
            }
            //返回视图
            return new ModelAndView("error");
        }




    }

}
