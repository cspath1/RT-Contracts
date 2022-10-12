package com.radiotelescope.mobileController.mobileModel.MobileWeatherData

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.weatherData.ErrorTag
import com.radiotelescope.controller.model.BaseForm
import com.radiotelescope.mobileContracts.mobileWeatherData.MobileListBetweenCreationDates
import java.util.*

class MobileListBetweenCreationDatesForm(
    val lowerDate: Date?,
    val upperDate: Date?
) : BaseForm<MobileListBetweenCreationDates.Request>{

    override fun toRequest(): MobileListBetweenCreationDates.Request {
        return MobileListBetweenCreationDates.Request(
            lowerDate = lowerDate!!,
            upperDate = upperDate!!
        )
    }

    fun validateRequest(): Multimap<ErrorTag, String>? {
        val errors = HashMultimap.create<ErrorTag, String>()

        if(lowerDate == null)
            errors.put(ErrorTag.INSERT_TIMESTAMP, "Required fields: Lower Date")
        if(upperDate == null)
            errors.put(ErrorTag.INSERT_TIMESTAMP, "Required field: Upper Date")

        if(!errors.isEmpty)
            return errors

        if(lowerDate!!.after(upperDate))
            errors.put(ErrorTag.INSERT_TIMESTAMP, "Start Time must be after End Time")

        return if (errors.isEmpty) null else errors
    }
}