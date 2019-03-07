package com.radiotelescope.repository.model.celestialBody

import org.junit.Assert.*
import org.junit.Test

internal class CelestialBodySpecificationBuilderTest {
    private val specificationBuilder = CelestialBodySpecificationBuilder()

    @Test
    fun testBuildWithNoParams() {
        val specification = specificationBuilder.build()

        assertNull(specification)
    }

    @Test
    fun testBuildWithOneParam() {
        val specification = specificationBuilder.with(SearchCriteria(Filter.NAME, "Crab Nebula")).build()

        assertNotNull(specification)
    }
}