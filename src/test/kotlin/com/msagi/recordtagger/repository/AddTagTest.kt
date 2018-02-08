package com.msagi.recordtagger.repository

import org.junit.Assert
import org.junit.Test

class AddTagTest : RepositoryTest() {

    @Test
    fun testAddTagValueReturnsTagSuccess() {
        val tagValue = "testTag"
        val tag = repository.addTag(tagValue)
        Assert.assertNotNull(tag)
    }

    @Test
    fun testAddTagThenReloadAndCompareSuccess() {
        val tagValue = "testTag"
        val tag = repository.addTag(tagValue)
        val tagReloaded = repository.getTag(tag.id)
        Assert.assertNotNull(tagReloaded)
        Assert.assertEquals(tag, tagReloaded)
    }

    @Test
    fun testAddSameTagValueSecondTimeReturnsSameTagSuccess() {
        val tagValue = "testTag"
        val tag = repository.addTag(tagValue)
        val tagAddedAgain = repository.addTag(tagValue)
        Assert.assertNotNull(tagAddedAgain)
        Assert.assertEquals(tag, tagAddedAgain)
    }
}