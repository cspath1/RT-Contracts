package com.radiotelescope

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.appointment.info.*
import com.radiotelescope.contracts.celestialBody.CelestialBodyInfo
import com.radiotelescope.contracts.rfdata.RFDataInfo
import com.radiotelescope.contracts.user.UserInfo
import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.celestialBody.CelestialBody
import com.radiotelescope.repository.rfdata.RFData
import com.radiotelescope.repository.user.User
import com.radiotelescope.security.AccessReport
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import java.util.*

/**
 * Adapts a [Multimap] into a Map of string keys, with collections of strings as key values
 *
 * @return a Map of strings with a collections of strings as the values
 */
fun <T: Enum<T>> Multimap<T, String>.toStringMap(): Map<String, Collection<String>> {
    return this.asMap().mapKeys { it.key.name }
}

/**
 * Adapts an [AccessReport] into a Map of string keys, with collections of strings as key values
 *
 * @return a Map of strings with a collection of string as the values
 */
fun AccessReport.toStringMap(): Map<String, Collection<String>> {
    val map = kotlin.collections.mutableMapOf<String, Collection<String>>()
    map["MISSING_ROLES"] = this.missingRoles!!.map { it.name }
    return map
}

/**
 * Adapts a Page of [Appointment] objects into a page of [AppointmentInfo] objects
 *
 * @return a Page of [AppointmentInfo] objects
 */
fun Page<Appointment>.toAppointmentInfoPage(): Page<AppointmentInfo> {
    val infoList = arrayListOf<AppointmentInfo>()

    // Type-based info assignment
    content.forEach {
        when (it.type) {
            Appointment.Type.POINT -> infoList.add(PointAppointmentInfo(it))
            Appointment.Type.DRIFT_SCAN -> infoList.add(DriftScanAppointmentInfo(it))
            Appointment.Type.CELESTIAL_BODY -> infoList.add(CelestialBodyAppointmentInfo(it))
            Appointment.Type.RASTER_SCAN -> infoList.add(MultiPointAppointmentInfo(it))
            Appointment.Type.FREE_CONTROL -> infoList.add(MultiPointAppointmentInfo(it))
        }
    }

    return PageImpl(infoList, pageable, totalElements)
}

/**
 * Adapts a List of [RFData] objects into a List of [RFDataInfo] objects
 *
 * @return a List of [RFDataInfo] objects
 */
fun List<RFData>.toInfoList(): List<RFDataInfo> {
    val infoList = arrayListOf<RFDataInfo>()
    forEach { infoList.add(RFDataInfo(it)) }

    return infoList
}

/**
 * Adapts a List of [Appointment] objects into a List of [AppointmentInfo] objects
 *
 * @return a List of [AppointmentInfo] objects
 */
fun List<Appointment>.toAppointmentInfoList(): List<AppointmentInfo> {
    val infoList = arrayListOf<AppointmentInfo>()

    // Type-based info assignment
    forEach {
        when (it.type) {
            Appointment.Type.POINT -> infoList.add(PointAppointmentInfo(it))
            Appointment.Type.DRIFT_SCAN -> infoList.add(DriftScanAppointmentInfo(it))
            Appointment.Type.CELESTIAL_BODY -> infoList.add(CelestialBodyAppointmentInfo(it))
            Appointment.Type.RASTER_SCAN -> infoList.add(MultiPointAppointmentInfo(it))
            Appointment.Type.FREE_CONTROL -> infoList.add(MultiPointAppointmentInfo(it))
        }
    }

    return infoList
}

/**
 * Generates a token using UUIDs
 */
fun String.Companion.generateToken(): String {
    return UUID.randomUUID().toString().replace("-", "", false)
}

/**
 * Determines if a Multimap is not empty
 *
 * @return true if a Multimap is not empty, false if it is
 */
fun <K, V> Multimap<K, V>.isNotEmpty(): Boolean {
    return !isEmpty
}

/**
 * Adapts a Page of [User] objects into a Page of [UserInfo] objects
 *
 * @return a Page of [UserInfo] objects
 */
fun Page<User>.toUserInfoPage(): Page<UserInfo> {
    val infoList = arrayListOf<UserInfo>()
    content.forEach {
        infoList.add(UserInfo(it, null, 0L))
    }
    return PageImpl(infoList, pageable, totalElements)
}

fun Page<CelestialBody>.toInfoPage(): Page<CelestialBodyInfo> {
    val infoList = arrayListOf<CelestialBodyInfo>()
    forEach {
        infoList.add(CelestialBodyInfo(it))
    }

    return PageImpl(infoList, pageable, totalElements)
}