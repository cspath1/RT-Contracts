package com.radiotelescope.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class S3Configuration(@Value("\${amazon.aws.s3.access-key-id}")
                      private val accessKeyId: String,
                      @Value("\${amazon.aws.s3.access-key-secret}")
                      private val accessKeySecret: String,
                      @Value("\${amazon.aws.s3.bucket}")
                      private val s3Bucket: String) {

    @Bean
    fun getS3Bucket(): String { return s3Bucket }

    fun getAccessKeyId(): String { return accessKeyId }

    fun getAccessKeySecret(): String { return accessKeySecret }
}