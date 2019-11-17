package com.radiotelescope.repository.videoFile


import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository
import java.util.*

/**
 * Spring Repository for the [VideoFile] Entity
 */
@Repository
interface IVideoFileRepository: PagingAndSortingRepository<VideoFile, Long> {

    /**
     * Spring Repository method that will return all [VideoFile] records between
     * the lower and upper date ranges
     *
     * @param lowerDate the start time of when to start grabbing the appointment
     * @param upperDate the end time of when to stop grabbing the appointment
     * @return a [List] of [VideoFile]
     */
    @Query(value = "SELECT * " +
            "FROM video_file " +
            "WHERE record_created_timestamp BETWEEN ?1 AND ?2",
            nativeQuery = true
    )
    fun findVideosCreatedBetweenDates(lowerDate: Date, upperDate: Date): List<VideoFile>
}