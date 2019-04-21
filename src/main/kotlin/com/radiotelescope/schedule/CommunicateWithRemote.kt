package com.radiotelescope.schedule

import com.radiotelescope.controller.model.Profile
import com.radiotelescope.repository.telescope.IRadioTelescopeRepository
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.net.HttpURLConnection
import java.net.URL

/**
 * Spring Component that is executed every 30 seconds in charge of contacting
 * the production server, signifying that it has internet connection. This is
 * only done by the local servers running in the control room(s)
 *
 * @param profile the application's profile
 * @param radioTelescopeRepo the [IRadioTelescopeRepository]
 */
@Component
class CommunicateWithRemote(
        private val profile: Profile,
        private val radioTelescopeRepo: IRadioTelescopeRepository
) {
    /**
     * Iterate through all Radio Telescope's to let the production know each one has
     * internet connection
     */
    // TODO: Uncomment when the production server has changes from this branch deployed
    //@Scheduled(cron = Settings.EVERY_THIRTY_SECONDS)
    fun execute() {
        // Only needs executed by the local servers since they need to communicate
        // with the production server
        if (profile == Profile.LOCAL) {
            // TODO: Change to be control room specific
            radioTelescopeRepo.findAll().forEach {
                val url = URL("https://prod-api.ycpradiotelescope.com/api/heartbeat/" + it.getId())
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
            }
        }
    }
}