package life.liudong.community.advice;

import com.alibaba.fastjson.JSON;
import life.liudong.community.dto.ResultDTO;
import life.liudong.community.exception.CustomizeErrorCode;
import life.liudong.community.exception.CustomizeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

/**
 * 全局异常切面
 *
 * @author liudong
 */

@ControllerAdvice
@Slf4j
public class CustomizeExceptionHandler {

    @ExceptionHandler(Exception.class)
    Object handle(Throwable ex, HttpServletRequest request, HttpServletResponse response) {


        String contentType = request.getContentType();
        String type = "application/json";
        if (type.equals(contentType)) {
            ResultDTO resultDTO;
            //请求接口错误返回json
            if (ex instanceof CustomizeException) {
                //已知异常，返回异常详情
                resultDTO = ResultDTO.errorOf((CustomizeException) ex);
            } else if(ex instanceof MethodArgumentNotValidException) {
                resultDTO=ResultDTO.errorOf(509,"参数错误");
            }else{
                //未知异常，统一返回系统错误
                resultDTO = ResultDTO.errorOf(CustomizeErrorCode.SYS_ERROR);
                log.error(ex.getMessage());
            }

            try {
                //基本方法实现接口错误写入json返回错误信息，而不是跳转页面
                response.setContentType(type);
                response.setStatus(200);
                response.setCharacterEncoding("utf-8");
                PrintWriter writer = response.getWriter();
                writer.write(JSON.toJSONString(resultDTO));
                writer.close();
            } catch (IOException ioe) {
                log.debug(Arrays.toString(ioe.getStackTrace()));
            }
            //不反回页面
            return null;

        } else {
            //非接口错误跳转页面
            ModelAndView error = new ModelAndView("error");
            if (ex instanceof CustomizeException) {
                error.addObject("message", ex.getMessage());
            } else if(ex instanceof MethodArgumentNotValidException) {
                error.addObject("message", "参数错误");
            }else {
                error.addObject("message", CustomizeErrorCode.SYS_ERROR.getMessage());
                ex.printStackTrace();
                log.error(ex.getMessage());
            }
            return error;
        }

    }

}
