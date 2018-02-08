package com.msagi.recordtagger.repository

import org.junit.Assert
import org.junit.Test

class GetTagsByValuesTest : RepositoryTest() {

    @Test
    fun testGetTagsByValuesWithNoTagSuccess() {
        val tags = repository.getTagsByValues(listOf())
        Assert.assertTrue(tags.isEmpty())
    }

    @Test
    fun testGetTagsByValuesWithSingleTagSuccess() {
        val tagValue = "testTag"
        val tag = repository.addTag(tagValue)
        val tags = repository.getTagsByValues(listOf(tagValue))
        Assert.assertTrue(tags.size == 1)
        Assert.assertTrue(tag in tags)
    }

    @Test
    fun testGetTagsByValuesWithMultipleTagsSuccess() {
        val tagValue = "testTag"
        val tag = repository.addTag(tagValue)
        val tagValue2 = "testTag2"
        val tag2 = repository.addTag(tagValue2)
        val tags = repository.getTagsByValues(listOf(tagValue, tagValue2))
        Assert.assertTrue(tags.size == 2)
        Assert.assertTrue(tag in tags)
        Assert.assertTrue(tag2 in tags)
    }

    @Test
    fun testGetTagsByValuesWithMultipleTagsAlphabeticalOrderedSuccess() {
        val tagValue = "testTagZ"
        val tag = repository.addTag(tagValue)
        val tagValue2 = "testTagA"
        val tag2 = repository.addTag(tagValue2)
        val tags = repository.getTagsByValues(listOf(tagValue, tagValue2))
        Assert.assertTrue(tags.size == 2)
        Assert.assertTrue(tag == tags[1])
        Assert.assertTrue(tag2 == tags[0])
    }
}