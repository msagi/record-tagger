package com.msagi.recordtagger.repository

import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class ResetTest : RepositoryTest() {

    @Test
    fun testResetSuccess() {
        val tagValue = "testTag"
        val tag = repository.addTag(tagValue)
        assertNotNull(repository.getTag(tag.id))
        repository.reset()
        assertNull(repository.getTag(tag.id))
    }
}