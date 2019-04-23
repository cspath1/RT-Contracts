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

fun <T: Enum<T>> Multimap<T, String>.toStringMap(): Map<String, Collection<String>> {
    return this.asMap().mapKeys { it.key.name }
}

fun AccessReport.toStringMap(): Map<String, Collection<String>> {
    val map = kotlin.collections.mutableMapOf<String, Collection<String>>()
    map["MISSING_ROLES"] = this.missingRoles!!.map { it.name }
    return map
}

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

fun List<RFData>.toInfoList(): List<RFDataInfo> {
    val infoList = arrayListOf<RFDataInfo>()
    forEach { infoList.add(RFDataInfo(it)) }

    return infoList
}

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

fun String.Companion.generateToken(): String {
    return UUID.randomUUID().toString().replace("-", "", false)
}

fun <K, V> Multimap<K, V>.isNotEmpty(): Boolean {
    return !isEmpty
}

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