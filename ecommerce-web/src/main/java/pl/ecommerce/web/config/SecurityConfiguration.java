package pl.ecommerce.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf().ignoringAntMatchers("/h2-console/**").disable()
                .headers().frameOptions().disable()
                .and()
                .formLogin()
//                .loginPage("/login.html")
                .and()
                .authorizeRequests()
                .antMatchers("/admin/**").permitAll()                                 // for now
                .antMatchers("/h2-console/**").permitAll()                              // for now
                .mvcMatchers("/js/**", "/css/**", "/webjars/**").permitAll()
                .mvcMatchers("/images/**").permitAll()
                .mvcMatchers("/").permitAll()
                .mvcMatchers("/register").permitAll()
                .mvcMatchers("/cart/**").permitAll()
                .mvcMatchers("/products/create").authenticated()
                .mvcMatchers("/products", "/products/*", "/products/add/**").permitAll()
                .mvcMatchers("/categories/**").permitAll()
                .mvcMatchers("/products/archive/*").permitAll()
                .antMatchers("/sellers/**").permitAll()
                .anyRequest().authenticated();

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
