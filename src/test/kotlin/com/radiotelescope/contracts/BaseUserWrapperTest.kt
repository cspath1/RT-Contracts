package com.radiotelescope.contracts

import org.junit.Before

abstract class BaseUserWrapperTest {
    var executed = true
    var shouldFailOnPrecondition = false

    @Before
    fun initialize() {
        executed = true
        shouldFailOnPrecondition = false
    }
}