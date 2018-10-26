package com.radiotelescope.service

data class SendForm(
        val toAddresses: List<String>,
        val fromAddress: String,
        val subject: String,
        val htmlBody: String
)