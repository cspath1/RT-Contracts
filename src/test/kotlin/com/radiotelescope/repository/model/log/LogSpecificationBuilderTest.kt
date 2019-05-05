package com.radiotelescope.repository.model.log

import org.junit.Assert.*
import org.junit.Test

internal class LogSpecificationBuilderTest {
    private val specificationBuilder = LogSpecificationBuilder()

    @Test
    fun testBuildWithNoParams() {
        val specification = specificationBuilder.build()

        assertNull(specification)
    }

    @Test
    fun testBuildWithOneParam() {
        val specification = specificationBuilder.with(SearchCriteria(Filter.IS_SUCCESS, "TRUE")).build()

        assertNotNull(specification)
    }
}