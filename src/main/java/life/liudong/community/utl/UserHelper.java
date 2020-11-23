package life.liudong.community.utl;

import javax.servlet.http.HttpServletRequest;

import life.liudong.community.model.User;

/**
 * @author 28415@hand-china.com 2020/09/30 15:36
 */
public class UserHelper {
    public static User currentUser(HttpServletRequest request){
        return (User) request.getSession().getAttribute("user");
    }
}
