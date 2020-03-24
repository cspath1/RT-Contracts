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

/**
 * Configuration for Amazon Web Services Simple Storage Service information,
 * used so that files can be upload/retrieved programmatically via AWS S3
 *
 * @param accessKeyId the AWS S3 access key id
 * @param accessKeySecret the AWS SES access secret key
 * @param s3Bucket the AWS S3 bucket name
 */
@Configuration
class S3Configuration(
        @Value("\${amazon.aws.s3.access-key-id}")
        private val accessKeyId: String,
        @Value("\${amazon.aws.s3.access-key-secret}")
        private val accessKeySecret: String,
        @Value("\${amazon.aws.s3.bucket}")
        private val s3Bucket: String
) {

    /**
     * Spring Bean that will return the AWS S3 bucket name
     */
    @Bean
    fun getS3Bucket(): String { return s3Bucket }

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

    /**
     * Returns the AWS S3 access key id
     */
    fun getAccessKeyId(): String { return accessKeyId }

    /**
     * Returns the AWS S3 access secret key
     */
    fun getAccessKeySecret(): String { return accessKeySecret }
}