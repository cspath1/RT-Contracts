package com.radiotelescope.repository.model.user

import org.junit.Assert.*
import org.junit.Test

internal class UserSpecificationBuilderTest {
    private val specificationBuilder = UserSpecificationBuilder()

    @Test
    fun testBuildWithNoParams() {
        val specification = specificationBuilder.build()

        assertNull(specification)
    }

    @Test
    fun testBuildWIthOneParam() {
        val specification = specificationBuilder.with(SearchCriteria(Filter.FIRST_NAME, "Cody")).build()

        assertNotNull(specification)
    }
}