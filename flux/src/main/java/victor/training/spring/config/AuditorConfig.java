package victor.training.spring.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.ReactiveAuditorAware;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.User;

@Configuration
@EnableR2dbcAuditing
@Slf4j
public class AuditorConfig {

    @Bean
    public ReactiveAuditorAware<String> auditorProvider(){
        return ()-> ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(Authentication::getPrincipal)
                .doOnNext(x -> log.debug("current principal: [{}]",x))
                .filter(User.class::isInstance)
                .map(User.class::cast)
                .map(User::getUsername)
                .doOnNext(x -> log.debug("current principal: [{}]",x))
                .doOnSuccess(x -> log.debug("success: [{}]",x))
                ;
    }
}
