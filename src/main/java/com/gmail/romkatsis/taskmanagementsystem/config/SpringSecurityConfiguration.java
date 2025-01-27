package com.gmail.romkatsis.taskmanagementsystem.config;

import com.gmail.romkatsis.taskmanagementsystem.repositories.UserRepository;
import com.gmail.romkatsis.taskmanagementsystem.security.DefaultAccessDeniedHandler;
import com.gmail.romkatsis.taskmanagementsystem.security.DefaultAuthenticationEntryPoint;
import com.gmail.romkatsis.taskmanagementsystem.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SpringSecurityConfiguration {

    private final UserRepository userRepository;

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private final DefaultAuthenticationEntryPoint defaultAuthenticationEntryPoint;

    private final DefaultAccessDeniedHandler defaultAccessDeniedHandler;

    @Autowired
    public SpringSecurityConfiguration(UserRepository userRepository, JwtAuthenticationFilter jwtAuthenticationFilter, DefaultAuthenticationEntryPoint defaultAuthenticationEntryPoint, DefaultAccessDeniedHandler defaultAccessDeniedHandler) {
        this.userRepository = userRepository;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.defaultAuthenticationEntryPoint = defaultAuthenticationEntryPoint;
        this.defaultAccessDeniedHandler = defaultAccessDeniedHandler;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)
            throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return email -> userRepository.findUserByEmail(email)
                .map(user -> new User(
                        user.getId().toString(),
                        user.getPassword(),
                        user.getRoles()))
                .orElseThrow(() ->
                        new UsernameNotFoundException("User with email %s not found".formatted(email)));
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        daoAuthenticationProvider.setUserDetailsService(userDetailsService());
        return daoAuthenticationProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(configurer -> {
                    configurer.authenticationEntryPoint(defaultAuthenticationEntryPoint);
                    configurer.accessDeniedHandler(defaultAccessDeniedHandler);
                })
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/tasks/*").hasRole("ADMIN")
                        .requestMatchers("/api/v1/tasks", "/api/v1/tasks/**").authenticated()
                        .anyRequest().denyAll()
                );

        httpSecurity.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }
}
