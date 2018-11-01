package com.radiotelescope.controller.model.appointment

import java.util.*

data class ListBetweenDatesForm(
        val startTime: Date?,
        val endTime: Date?
)