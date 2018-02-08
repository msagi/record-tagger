package com.msagi.recordtagger.repository

import org.junit.Assert
import org.junit.Test

class GetAllTagsTest : RepositoryTest() {

    @Test
    fun testGetAllTagsWithSingleTagSuccess() {
        val tagValue = "testTag"
        val tag = repository.addTag(tagValue)
        val tags = repository.getAllTags()
        Assert.assertTrue(tags.size == 1)
        Assert.assertTrue(tag in tags)
    }

    @Test
    fun testGetAllTagsWithMultipleTagsSuccess() {
        val tagValue = "testTag"
        val tag = repository.addTag(tagValue)
        val tagValue2 = "testTag2"
        val tag2 = repository.addTag(tagValue2)
        val tags = repository.getAllTags()
        Assert.assertTrue(tags.size == 2)
        Assert.assertTrue(tag in tags)
        Assert.assertTrue(tag2 in tags)
    }

    @Test
    fun testGetAllTagsWithMultipleTagsAlphabeticalOrderedSuccess() {
        val tagValue = "testTagZ"
        val tag = repository.addTag(tagValue)
        val tagValue2 = "testTagA"
        val tag2 = repository.addTag(tagValue2)
        val tags = repository.getAllTags()
        Assert.assertTrue(tags.size == 2)
        Assert.assertTrue(tag == tags[1])
        Assert.assertTrue(tag2 == tags[0])
    }
}