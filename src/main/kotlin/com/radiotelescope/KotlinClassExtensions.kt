package com.radiotelescope

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.appointment.info.AppointmentInfo
import com.radiotelescope.contracts.appointment.info.PointAppointmentInfo
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
    content.forEach {
        // TODO: Change when other types are implemented
        infoList.add(PointAppointmentInfo(it))
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
    forEach {
        // TODO: Change when other types are implemented
        infoList.add(PointAppointmentInfo(it))
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
        infoList.add(UserInfo(it, null))
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