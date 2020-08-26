package life.liudong.community.config;

import life.liudong.community.interceptor.SessionInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private SessionInterceptor sessionInterceptor;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        //注册session拦截器
        registry.addInterceptor(sessionInterceptor).addPathPatterns("/**");

    }

    /*@Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

    }*/
}