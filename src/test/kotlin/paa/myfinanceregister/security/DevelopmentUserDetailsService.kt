package paa.myfinanceregister.security

import org.springframework.context.annotation.Profile
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
@Profile("dev")
class DevelopmentUserDetailsService : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails {
        if (username != DEVELOPMENT_USERNAME) {
            throw UsernameNotFoundException("Development user not found")
        }
        return User.withUsername(DEVELOPMENT_USERNAME)
            .password("{noop}$DEVELOPMENT_PASSWORD")
            .roles("USER")
            .build()
    }

    companion object {
        const val DEVELOPMENT_USERNAME = "dev"
        const val DEVELOPMENT_PASSWORD = "dev"
    }
}
