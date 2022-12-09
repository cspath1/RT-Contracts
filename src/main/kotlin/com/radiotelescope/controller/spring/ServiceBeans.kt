package com.radiotelescope.controller.spring

import com.radiotelescope.service.s3.IAwsS3DeleteService
import com.radiotelescope.service.s3.IAwsS3RetrieveService
import com.radiotelescope.service.s3.IAwsS3UploadService
import com.radiotelescope.service.ses.IAwsSesSendService
import com.radiotelescope.service.sns.IAwsSnsService
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component

@Component
@Configuration
class ServiceBeans(
        val awsSesSendService: IAwsSesSendService,
        val awsSnsService: IAwsSnsService,
        val s3DeleteService: IAwsS3DeleteService,
        val s3UploadService: IAwsS3UploadService,
        val s3RetrieveService: IAwsS3RetrieveService
)