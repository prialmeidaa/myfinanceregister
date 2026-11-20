package paa.myfinanceregister

import com.vaadin.flow.component.dependency.StyleSheet
import com.vaadin.flow.theme.aura.Aura
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class MyFinanceRegisterApplicationTests {
    @Test
    fun loadsAuraBeforeTheBrandStylesheet() {
        val stylesheets = MyFinanceRegisterApplication::class.java
            .getAnnotationsByType(StyleSheet::class.java)

        assertThat(stylesheets.map { it.value })
            .containsExactly(Aura.STYLESHEET, "styles.css")
        assertThat(javaClass.classLoader.getResource("META-INF/resources/styles.css"))
            .isNotNull()
    }

    @Test
    fun contextLoads() {
    }
}
