package com.radiotelescope.contracts.appointment

import com.google.common.collect.HashMultimap
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.appointment.Appointment
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit4.SpringRunner
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.SimpleResult

@DataJpaTest
@RunWith(SpringRunner::class)
internal class DeleteTest
{

    private lateinit var apptRepo: IAppointmentRepository
    private lateinit var appt: Appointment
    var deleteT:Delete = Delete(appt, apptRepo)


    @Test
fun DeleteTest()
    {
        var errors = HashMultimap.create<ErrorTag,String>()
        var s: SimpleResult<Long, Multimap<ErrorTag, String>> =  SimpleResult(null, errors)

        s =  deleteT.execute()

        //pass if errors Multimap remains empty
        if ( errors.isEmpty() )
        {

        }
        //fail if otherwise
        else assertTrue(false)

    }
}