package com.msagi.recordtagger.repository

import com.msagi.recordtagger.TestConfigProvider
import org.junit.After
import org.junit.Before

open class RepositoryTest {

    val repository = TestConfigProvider.provideTestRepository()

    @Before
    open fun before() {
        repository.initialize()
        repository.reset()
    }

    @After
    fun after() {
        repository.reset()
    }
}
