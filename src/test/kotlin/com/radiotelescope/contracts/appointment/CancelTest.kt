package com.radiotelescope.contracts.appointment

import com.radiotelescope.repository.appointment.IAppointmentRepository
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit4.SpringRunner
import com.radiotelescope.TestUtil
import com.radiotelescope.repository.appointment.Appointment
import org.junit.Before
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.test.context.ActiveProfiles
import java.util.*

@DataJpaTest
@RunWith(SpringRunner::class)
@ActiveProfiles(value = ["test"])
internal class CancelTest {
    @TestConfiguration
    class UtilTestContextConfiguration {
        @Bean
        fun utilService(): TestUtil { return TestUtil() }
    }

    @Autowired
    private lateinit var testUtil: TestUtil

    @Autowired
    private lateinit var appointmentRepo: IAppointmentRepository

    private var appointmentRequest = Create.Request(
            startTime = Date(Date().time + 5000),
            endTime = Date(Date().time + 10000),
            isPublic = true,
            telescopeId = 456,
            userId = 23
    )

    private var appointmentRequest2 = Create.Request(
            startTime = Date(),
            endTime = Date(Date().time + 2500),
            isPublic = true,
            telescopeId = 512,
            userId = 54
    )

    private var appointmentRequest3 = Create.Request(
            startTime = Date(Date().time + 12500) ,
            endTime = Date(Date().time + 15000),
            isPublic = true,
            telescopeId = 513,
            userId = 55
    )

    private var appointmentRequest4 = Create.Request(
            startTime = Date(Date().time + 16000) ,
            endTime = Date(Date().time + 17000),
            isPublic = true,
            telescopeId = 514,
            userId = 56
    )

    var scheduledAppointmentId:Long = 0
    var inProgressAppointmentId:Long = 0
    var canceledAppointmentId:Long = 0
    var completedAppointmentId:Long = 0

    @Before
    fun setUp() {
        // Persist a user
        val user = testUtil.createUser("spathcody@gmail.com")

        // Scheduled to Canceled
        val appt1 = testUtil.createAppointment(user = user,
                telescopeId = appointmentRequest.telescopeId,
                status = Appointment.Status.Scheduled,
                startTime = appointmentRequest.startTime,
                endTime = appointmentRequest.endTime,
                isPublic = appointmentRequest.isPublic
        )

        // In Progress to Canceled
        val appt2 = testUtil.createAppointment(user = user,
                telescopeId = appointmentRequest2.telescopeId,
                status = Appointment.Status.InProgress,
                startTime = appointmentRequest2.startTime,
                endTime = appointmentRequest2.endTime,
                isPublic = appointmentRequest2.isPublic
        )
        // Should result in error: Canceled to Canceled
        val appt3 = testUtil.createAppointment(user = user,
                telescopeId = appointmentRequest3.telescopeId,
                status = Appointment.Status.Canceled,
                startTime = appointmentRequest3.startTime,
                endTime = appointmentRequest3.endTime,
                isPublic = appointmentRequest3.isPublic)

        // Already completed appointments cannot be canceled
        val appt4 = testUtil.createAppointment(user = user,
                telescopeId = appointmentRequest4.telescopeId,
                status = Appointment.Status.Completed,
                startTime = appointmentRequest4.startTime,
                endTime = appointmentRequest4.endTime,
                isPublic = appointmentRequest4.isPublic)

        scheduledAppointmentId = appt1.id
        inProgressAppointmentId = appt2.id
        canceledAppointmentId = appt3.id
        completedAppointmentId = appt4.id
    }

    @Test
    fun testValidConstraints_Success() {
        // Test the Scheduled cleaning
        val (id1, errors1) = Cancel(
                appointmentId = scheduledAppointmentId,
                appointmentRepo = appointmentRepo
        ).execute()

        assertNull(errors1)
        assertNotNull(id1)

        val theFirstAppointment = appointmentRepo.findById(id1!!).get()
        assertEquals(theFirstAppointment.status, Appointment.Status.Canceled)

        // Test the In Progress Cleaning
        val (id2, errors2) = Cancel(
                appointmentId = inProgressAppointmentId,
                appointmentRepo = appointmentRepo
        ).execute()

        assertNull(errors2)
        assertNotNull(id2)

        val theSecondAppointment = appointmentRepo.findById(id2!!).get()
        assertEquals(theSecondAppointment.status, Appointment.Status.Canceled)
    }

    @Test
    fun testInvalidStatus_AlreadyCanceled_Failure() {
        // Execute the command on the already canceled cleaning
        val (id, error) = Cancel(
                appointmentId = canceledAppointmentId,
                appointmentRepo = appointmentRepo
        ).execute()

        assertNotNull(error)
        assertNull(id)

        assertEquals(1, error!!.size())
        assertTrue(error[ErrorTag.STATUS].isNotEmpty())
    }

    @Test
    fun testInvalidStatus_Completed_Failure() {
        // Execute the command on the already completed cleaning
        val (id, error) = Cancel(
                appointmentId = completedAppointmentId,
                appointmentRepo = appointmentRepo
        ).execute()

        assertNotNull(error)
        assertNull(id)

        assertEquals(1, error!!.size())
        assertTrue(error[ErrorTag.STATUS].isNotEmpty())
    }

    @Test
    fun testNonExistentAppointment_Failure() {
        val (id, error) = Cancel(
                appointmentId = 311L,
                appointmentRepo = appointmentRepo
        ).execute()

        assertNotNull(error)
        assertNull(id)

        assertEquals(1, error!!.size())
        assertTrue(error[ErrorTag.ID].isNotEmpty())
    }
}