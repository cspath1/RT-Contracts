package com.radiotelescope

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

internal abstract class BaseDataJpaTest {
    companion object {
        @TestConfiguration
        class UtilTestContextConfiguration {
            @Bean
            fun utilService(): TestUtil { return TestUtil() }
        }
    }
}