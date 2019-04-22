package com.radiotelescope.repository.subscribedAppointment

import com.radiotelescope.repository.user.User
import javax.persistence.*

@Entity
@Table(name = "subscribedAppointment")
data class subscribedAppointment (
        @Column(name = "appointment_id", nullable = false)
        var appointmentId: Long,
        @Column(name = "user_id", nullable = false)
        var userId: Long
) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="id", unique = true, nullable = false)
    var id: Long = 0

    @Column(name = "status")
    @Enumerated(value = EnumType.STRING)
    var status: subscribedAppointment.Status = Status.SCHEDULED

    enum class Status(val label: String){
        STARTED("Started"),
        SCHEDULED("Scheduled")
    }
}