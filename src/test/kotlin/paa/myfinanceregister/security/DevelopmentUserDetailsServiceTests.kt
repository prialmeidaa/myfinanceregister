package paa.myfinanceregister.security

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin
import org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.beans.factory.annotation.Autowired

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
class DevelopmentUserDetailsServiceTests {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun developmentCredentialsAuthenticateSuccessfully() {
        mockMvc.perform(
            formLogin()
                .user(DevelopmentUserDetailsService.DEVELOPMENT_USERNAME)
                .password(DevelopmentUserDetailsService.DEVELOPMENT_PASSWORD)
        ).andExpect(authenticated().withUsername(DevelopmentUserDetailsService.DEVELOPMENT_USERNAME))
    }
}
