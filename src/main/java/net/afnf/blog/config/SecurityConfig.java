package net.afnf.blog.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@Order(SecurityProperties.DEFAULT_FILTER_ORDER)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    /** strength=8, 256iteration */
    final private static int STLENGTH = 8;

    @Autowired
    private DataSource dataSource;

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/static/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests() // 
                .antMatchers("/_admin/pub/**").permitAll() // 
                .antMatchers("/_admin/**").hasRole("ADMIN") //
                .anyRequest().permitAll() //
                //
                .and()//
                .formLogin() // 
                .loginPage("/_admin/pub/login") //
                .loginProcessingUrl("/_admin/pub/auth") //
                .failureUrl("/_admin/pub/login?faliled") //
                .defaultSuccessUrl("/_admin/") // 
                //
                .and() //
                .csrf().disable() //
                .headers().cacheControl().disable() //  
                .httpStrictTransportSecurity().disable() //  
        ;
    }

    @Configuration
    public class AuthenticationManagerConfiguration extends GlobalAuthenticationConfigurerAdapter {

        @Override
        public void init(AuthenticationManagerBuilder auth) throws Exception {
            auth.jdbcAuthentication() //
                    .dataSource(dataSource)
                    .usersByUsernameQuery("select username, password, true from users where username = ?")
                    .authoritiesByUsernameQuery("select username, role from users where username = ?")
                    .passwordEncoder(passwordEncoder());
        }
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(STLENGTH);
    }
}
