package com.radiotelescope.repository.model.appointment

import org.junit.Assert.*
import org.junit.Test

internal class AppointmentSpecificationBuilderTest {
    private val specificationBuilder = AppointmentSpecificationBuilder()

    @Test
    fun testBuildWithNoParams() {
        val specification = specificationBuilder.build()

        assertNull(specification)
    }

    @Test
    fun testBuildWithOneParam() {
        val specification = specificationBuilder.with(SearchCriteria(Filter.USER_FIRST_NAME, "Cody")).build()

        assertNotNull(specification)
    }
}