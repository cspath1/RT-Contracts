package com.radiotelescope.controller.model.ses

data class SesSendForm(
        val toAddresses: List<String>,
        val fromAddress: String,
        val subject: String,
        val htmlBody: String
)