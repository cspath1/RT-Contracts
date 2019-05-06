package com.radiotelescope.repository.model.log

import org.junit.Assert.*
import org.junit.Test

class FilterTest {
    @Test
    fun testFromActionField() {
        val filter = Filter.fromField("action")

        assertNotNull(filter)
        assertEquals(Filter.ACTION, filter)
    }

    @Test
    fun testFromAffectedTableField() {
        val filter = Filter.fromField("affectedTable")

        assertNotNull(filter)
        assertEquals(Filter.AFFECTED_TABLE, filter)
    }

    @Test
    fun testFromIsSuccessField() {
        val filter = Filter.fromField("isSuccess")

        assertNotNull(filter)
        assertEquals(Filter.IS_SUCCESS, filter)
    }

    @Test
    fun testFromStatusField() {
        val filter = Filter.fromField("status")

        assertNotNull(filter)
        assertEquals(Filter.STATUS, filter)
    }

    @Test
    fun testInvalidField() {
        val filter = Filter.fromField("invalid")

        assertNull(filter)
    }
}