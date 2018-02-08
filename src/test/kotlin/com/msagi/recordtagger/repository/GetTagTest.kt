package com.msagi.recordtagger.repository

import org.junit.Assert
import org.junit.Test

class GetTagTest : RepositoryTest() {

    @Test
    fun testGetTagByIdSuccess() {
        val tagValue = "testTag"
        val tag = repository.addTag(tagValue)
        val tagReadById = repository.getTag(tag.id)
        Assert.assertNotNull(tagReadById)
        Assert.assertEquals(tag.id, tagReadById!!.id)
        Assert.assertEquals(tag.tag, tagReadById.tag)
    }

    @Test
    fun testGetNonExistingTagReturnsNull() {
        val tagValue = "testTag"
        val tag = repository.addTag(tagValue)
        repository.removeTag(tag.id)
        val tagReadById = repository.getTag(tag.id)
        Assert.assertNull(tagReadById)
    }
}