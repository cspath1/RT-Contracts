package com.radiotelescope.controller.model.ses

import com.radiotelescope.controller.model.Profile

abstract class AppLink {
    companion object {
        fun generate(profile: Profile): String {
//            return if (profile == Profile.LOCAL)
//                "http://localhost:8081"
//            else
//                "https://www.ycpradiotelescope.com"

            // Just return the localhost url for now
            return "http://localhost:8081"
        }
    }
}