//package me.remigiuszkatrynski.downloader;
//
//import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
//import org.springframework.context.ApplicationEventPublisher;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.AuthenticationEventPublisher;
//import org.springframework.security.authentication.DefaultAuthenticationEventPublisher;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.provisioning.InMemoryUserDetailsManager;
//
//public class Auth {
//
//    @EnableWebSecurity
//    @Configuration
//    public static class SecurityConfig {
//        @Bean
//        @ConditionalOnMissingBean(UserDetailsService.class)
//        InMemoryUserDetailsManager inMemoryUserDetailsManager() {
//            String generatedPassword = "{noop}test123";
//            return new InMemoryUserDetailsManager(User.withUsername("admin")
//                    .password(generatedPassword).roles("ADMIN").build());
//        }
//
//        @Bean
//        @ConditionalOnMissingBean(AuthenticationEventPublisher.class)
//        DefaultAuthenticationEventPublisher defaultAuthenticationEventPublisher(ApplicationEventPublisher delegate) {
//            return new DefaultAuthenticationEventPublisher(delegate);
//        }
//    }
//}
