package com.radiotelescope.repository.videoFile

import java.util.*
import javax.persistence.*

/**
 * Entity Class representing a Video File for the web-application
 *
 * This Entity correlates to the video_file SQL table
 */
@Entity
@Table(name = "video_file")
data class VideoFile (
        @Column(name = "thumbnail_path", nullable = false)
        var thumbnailPath: String?,
        @Column(name = "video_path", nullable = false)
        var videoPath: String?,
        @Column(name = "video_length", nullable = false)
        var videoLength: String?
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long = 0

    @Column(name = "insert_timestamp", nullable = false)
    var recordCreatedTimestamp: Date = Date()

    @Column(name = "update_timestamp", nullable = true)
    var recordUpdatedTimestamp: Date = Date()
}