package com.radiotelescope.schedule

/**
 * Contains strings for various cron job formats used in the application
 */
object Settings {
    const val EVERY_HOUR = "0 0 * * * *"
    const val EVERY_MINUTE = "0 0/1 * * * *"
    const val EVERY_THIRTY_SECONDS = "0/30 * * * * *"
}