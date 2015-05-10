package net.afnf.blog.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.jdbc.JdbcDaoImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
@Configuration
@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
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
                .headers().httpStrictTransportSecurity().disable() //  
                //
                .headers().contentTypeOptions().frameOptions().xssProtection() //
        ;
    }

    @Override
    @Bean
    public UserDetailsService userDetailsServiceBean() throws Exception {
        JdbcDaoImpl userDetailsService = new JdbcDaoImpl();
        userDetailsService.setDataSource(dataSource);
        userDetailsService.setUsersByUsernameQuery("select username, password, true from users where username = ?");
        userDetailsService.setAuthoritiesByUsernameQuery("select username, role from users where username = ?");
        return userDetailsService;
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth, UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(STLENGTH);
    }
}
