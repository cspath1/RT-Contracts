package com.radiotelescope.controller.spectracyberConfig

import com.radiotelescope.controller.model.spectracyberConfig.UpdateForm
import com.radiotelescope.controller.user.BaseUserRestControllerTest
import com.radiotelescope.repository.log.ILogRepository
import com.radiotelescope.repository.spectracyberConfig.SpectracyberConfig
import org.junit.Before
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit4.SpringRunner

@DataJpaTest
@RunWith(SpringRunner::class)
internal class SpectracyberConfigUpdateControllerTest : BaseUserRestControllerTest() {
    @Autowired
    private lateinit var logRepo: ILogRepository

    private lateinit var spectracyberConfigUpdateController: SpectracyberConfigUpdateController

    private lateinit var baseForm: UpdateForm

    private lateinit var spectracyberConfig: SpectracyberConfig

    private var userContext = getContext()

    @Before
    override fun init() {
        super.init()
    }
}