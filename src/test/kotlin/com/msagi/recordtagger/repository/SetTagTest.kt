package com.msagi.recordtagger.repository

import org.junit.Assert
import org.junit.Test


class SetTagTest : RepositoryTest() {

    @Test
    fun testSetTagSuccess() {
        val tagValue = "tagValue"
        val tagValue2 = "tagValue2"
        val tag = repository.addTag(tagValue)
        val updatedTag = repository.setTag(tag.id, tagValue2)
        Assert.assertNotNull(updatedTag)
        Assert.assertEquals(tagValue2, updatedTag.tag)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testTryToUpdateNonExistingTagFailure() {
        repository.setTag(-1, "newValue")
    }

}