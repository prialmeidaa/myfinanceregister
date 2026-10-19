package paa.myfinanceregister

import org.springframework.boot.SpringApplication

class MyFinanceRegisterApplicationLocal {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication(MyFinanceRegisterApplication::class.java)
                .apply { setAdditionalProfiles("dev") }
                .run(*args)
        }
    }
}
