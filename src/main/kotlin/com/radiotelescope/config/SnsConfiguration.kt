package com.radiotelescope.config

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.regions.Regions
import com.amazonaws.services.sns.AmazonSNSAsyncClientBuilder
import com.amazonaws.services.sns.AmazonSNS
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Configuration for Amazon Web Services Simple Notification Services information,
 * used so that notifications can be sent programmatically via AWS SNS
 *
 * @param accessKeyId the AWS SNS access key
 * @param accessKeySecret the AWS SNS access secret key
 */
@Configuration
class SnsConfiguration (
        // TODO: get a new key for this
        @Value("\${amazon.aws.sns.access-key-id}") private val accessKeyId: String,
        @Value("\${amazon.aws.sns.access-key-secret}") private val accessKeySecret: String
) {
    /**
     * Spring Bean that will return a [AmazonSNS] object with
     * application-specific AWS SNS credential information
     *
     * @return an [AmazonSNS] client object with the proper credentials
     */
    @Bean
    fun snsClient(): AmazonSNS {
        val credentials = BasicAWSCredentials(accessKeyId, accessKeySecret)

        return AmazonSNSAsyncClientBuilder
                .standard()
                .withCredentials(AWSStaticCredentialsProvider(credentials)).withRegion(Regions.US_EAST_1).build()
    }
}