package com.radiotelescope.controller.model.ses

import com.radiotelescope.controller.model.Profile

abstract class AppLink {
    companion object {
        fun generate(profile: Profile): String {
            return if (profile == Profile.LOCAL || profile == Profile.DEV)
                "http://localhost:8081"
            else
                "https://www.ycpradiotelescope.com/#"
        }
    }
}