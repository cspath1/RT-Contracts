package com.radiotelescope.repository.userNotificationType

import javax.persistence.*


@Entity
@Table(name = "user_notification_type")
data class UserNotificationType(
        @Column(name = "user_id", nullable = false)
        var userId: Long,
        @Column(name = "type", nullable = false)
        var type: NotificationType
) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long = 0

    enum class NotificationType(val label: String) {
        PHONE("Phone"),
        EMAIL("Email")
    }
}