package paa.myfinanceregister

import com.vaadin.flow.component.dependency.StyleSheet
import com.vaadin.flow.component.page.AppShellConfigurator
import com.vaadin.flow.theme.aura.Aura
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
@StyleSheet(Aura.STYLESHEET)
@StyleSheet("styles.css")
class MyFinanceRegisterApplication : AppShellConfigurator

fun main(args: Array<String>) {
    runApplication<MyFinanceRegisterApplication>(*args)
}
