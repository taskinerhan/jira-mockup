package com.jwt.config;

import com.jwt.JsonSchemaValidator;
import com.jwt.Repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;
import java.nio.file.Paths;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {
    private final UserRepository userRepository;
    @Bean
    public UserDetailsService userDetailsService(){
        return  username -> userRepository.findByUsername(username)
                .orElseThrow(()-> new UsernameNotFoundException("User not found!"));
    }
    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authenticationProvider =new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
    @Bean
    public JsonSchemaValidator projectSchemaValidator() throws Exception {
        return new JsonSchemaValidator(Paths.get("src/main/resources/schema/project-schema.json").toString());
    }
    @Bean
    public JsonSchemaValidator issueSchemaValidator() throws Exception {
        return new JsonSchemaValidator(Paths.get("src/main/resources/schema/issue-schema.json").toString());
    }
    @Bean
    public JsonSchemaValidator statusSchemaValidator() throws Exception {
        return new JsonSchemaValidator(Paths.get("src/main/resources/schema/status-schema.json").toString());
    }
    @Bean
    public JsonSchemaValidator updateIssueSchemaValidator() throws Exception {
        return new JsonSchemaValidator(Paths.get("src/main/resources/schema/update-issue-schema.json").toString());
    }
}
