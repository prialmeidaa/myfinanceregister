package paa.myfinanceregister.security

import com.vaadin.flow.spring.security.VaadinSecurityConfigurer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain
import paa.myfinanceregister.ui.views.LoginView

@Configuration
@EnableWebSecurity
@Profile("!dev")
class SecurityConfiguration {
    @Bean
    fun vaadinSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.oauth2Login(Customizer.withDefaults())
        http.authorizeHttpRequests { requests ->
            requests.requestMatchers("/images/**").permitAll()
        }
        http.with(VaadinSecurityConfigurer.vaadin()) { vaadin ->
            vaadin.loginView(LoginView::class.java)
        }
        return http.build()
    }
}
