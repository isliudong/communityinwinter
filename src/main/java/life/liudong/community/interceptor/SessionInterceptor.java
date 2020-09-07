package life.liudong.community.interceptor;

import life.liudong.community.mapper.UserMapper;
import life.liudong.community.model.User;
import life.liudong.community.model.UserExample;
import life.liudong.community.service.NotificationService;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author liudong
 */
@Service
public class SessionInterceptor implements HandlerInterceptor {
    final
    UserMapper userMapper;
    final
    NotificationService notificationService;

    public SessionInterceptor(UserMapper userMapper, NotificationService notificationService) {
        this.userMapper = userMapper;
        this.notificationService = notificationService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("token".equals(cookie.getName())) {
                    String token = cookie.getValue();

                    UserExample userExample = new UserExample();
                    //加入token参数
                    userExample.createCriteria().andTokenEqualTo(token);
                    List<User> users = userMapper.selectByExample(userExample);
                    if (users.size() != 0) {
                        request.getSession().setAttribute("user", users.get(0));
                        Long unreadCount = notificationService.unreadCount(users.get(0).getId());
                        request.getSession().setAttribute("unreadNotificationCount",unreadCount);
                    }
                    break;
                }
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {

    }
}
