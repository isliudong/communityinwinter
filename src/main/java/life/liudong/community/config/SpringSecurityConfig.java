package life.liudong.community.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

/**
 * SpringSecurity 配置
 *
 * @author 28415@hand-china.com 2020/11/29 12:45
 */
@Configuration
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {
    /**
     * 定义用户信息服务
     */
    @Bean
    @Override
    public UserDetailsService userDetailsService() {
        InMemoryUserDetailsManager userDetailsManager = new InMemoryUserDetailsManager();
        userDetailsManager.createUser(User.withUsername("刘冬").password("123").authorities("p1").build());
        userDetailsManager.createUser(User.withUsername("admin").password("123").authorities("p2").build());
        return userDetailsManager;
    }


    /**
     * 密码加密策略
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        //不加密
        return NoOpPasswordEncoder.getInstance();

    }
    //配置安全拦截

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //拦截
        http.headers().frameOptions().disable();
        http.authorizeRequests()//.antMatchers("/security").authenticated()
                //放行
                .anyRequest().permitAll()
                //允许表单登录
                //.and().formLogin().successForwardUrl("/")
                .and().csrf().disable();
    }
}
