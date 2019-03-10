package com.radiotelescope.repository.model.user

import org.junit.Assert.*
import org.junit.Test

class FilterTest {
    @Test
    fun testFromFirstNameField() {
        val filter = Filter.fromField("firstName")

        assertNotNull(filter)
        assertEquals(Filter.FIRST_NAME, filter)
    }

    @Test
    fun testFromLastNameField() {
        val filter = Filter.fromField("lastName")

        assertNotNull(filter)
        assertEquals(Filter.LAST_NAME, filter)
    }

    @Test
    fun testFromEmailField() {
        val filter = Filter.fromField("email")

        assertNotNull(filter)
        assertEquals(Filter.EMAIL, filter)
    }

    @Test
    fun testFromCompanyField() {
        val filter = Filter.fromField("company")

        assertNotNull(filter)
        assertEquals(Filter.COMPANY, filter)
    }

    @Test
    fun testInvalidField() {
        val filter = Filter.fromField("invalid")

        assertNull(filter)
    }
}