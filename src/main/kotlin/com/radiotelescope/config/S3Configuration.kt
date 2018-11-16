package com.radiotelescope.config

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.radiotelescope.service.s3.S3Bucket
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
    fun getS3Bucket(): S3Bucket { return S3Bucket(s3Bucket) }

    @Bean
    fun getAmazonS3Client(): AmazonS3 {
        return AmazonS3ClientBuilder.standard()
                .withRegion(Regions.US_EAST_1)
                .withCredentials(
                        AWSStaticCredentialsProvider(
                                BasicAWSCredentials(
                                        accessKeyId,
                                        accessKeySecret
                                )
                        )
                ).build()
    }

    fun getAccessKeyId(): String { return accessKeyId }

    fun getAccessKeySecret(): String { return accessKeySecret }
}