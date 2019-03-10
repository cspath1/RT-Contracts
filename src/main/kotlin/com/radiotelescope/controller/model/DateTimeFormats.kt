package com.radiotelescope.controller.model

import java.time.format.DateTimeFormatter

object DateTimeFormats {
    val dateTimeFormat = DateTimeFormatter.ofPattern("MM/dd/YYYY hh:mm a")
}