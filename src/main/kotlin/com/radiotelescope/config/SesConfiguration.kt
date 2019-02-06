package com.radiotelescope.config

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.regions.Regions
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceAsyncClientBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Configuration for Amazon Web Services Simple Email Services information,
 * used so that emails can be sent programmatically via AWS SES
 *
 * @param accessKeyId the AWS SES access key idea
 * @param accessKeySecret the AWS SES access secret key
 */
@Configuration
class SesConfiguration(
        @Value("\${amazon.aws.ses.access-key-id}") private val accessKeyId: String,
        @Value("\${amazon.aws.ses.access-key-secret}") private val accessKeySecret: String
) {

    /**
     * Spring Bean that will return a [AmazonSimpleEmailService] object with
     * application-specific AWS SES credential information
     *
     * @return an [AmazonSimpleEmailService] with the proper credentials
     */
    @Bean
    fun sesClient(): AmazonSimpleEmailService {
        val credentials = BasicAWSCredentials(accessKeyId, accessKeySecret)

        return AmazonSimpleEmailServiceAsyncClientBuilder
                .standard()
                .withCredentials(AWSStaticCredentialsProvider(credentials)).withRegion(Regions.US_EAST_1).build()

    }
}