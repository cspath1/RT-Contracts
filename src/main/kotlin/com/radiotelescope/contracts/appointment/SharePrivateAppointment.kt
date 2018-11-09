package com.radiotelescope.contracts.appointment

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.user.IUserRepository
import org.springframework.data.domain.Page
/*
class SharePrivateAppointment(
        private val appointmentRepo: IAppointmentRepository,
        private val a_id: Long,
        private val userRepo: IUserRepository,
        private val user_id:Long
        ):Command<Page<AppointmentInfo>, Multimap<ErrorTag, String>>
{

        override fun execute(): SimpleResult<Long, Multimap<ErrorTag, String>> {
        }



}
        */