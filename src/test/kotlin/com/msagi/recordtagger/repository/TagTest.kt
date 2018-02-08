package com.msagi.recordtagger.repository

import org.junit.Assert
import org.junit.Test

class TagTest : RepositoryTest() {

    @Test
    fun testTagSuccess() {
        val recordValue = "recordValue"
        var record = repository.addRecord(recordValue)
        val tagValue = "tagValue"
        val tag = repository.addTag(tagValue)
        record = repository.tag(record.id, tag.id)
        val tags = record.tags
        Assert.assertTrue(tags.size == 1)
        Assert.assertTrue(tag in tags)
    }

    @Test
    fun testTagWithMultipleTagsSuccess() {
        val recordValue = "recordValue"
        var record = repository.addRecord(recordValue)
        val tagValue = "tagValue"
        val tag = repository.addTag(tagValue)
        val tag2Value = "tag2Value"
        val tag2 = repository.addTag(tag2Value)
        repository.tag(record.id, tag.id)
        record = repository.tag(record.id, tag2.id)
        val tags = record.tags
        Assert.assertTrue(tags.size == 2)
        Assert.assertTrue(tag in tags)
        Assert.assertTrue(tag2 in tags)
    }

    @Test
    fun testTagWithSameTagMultipleTimesSuccess() {
        val recordValue = "recordValue"
        var record = repository.addRecord(recordValue)
        val tagValue = "tagValue"
        val tag = repository.addTag(tagValue)
        repository.tag(record.id, tag.id)
        record = repository.tag(record.id, tag.id)
        val tags = record.tags
        Assert.assertTrue(tags.size == 1)
        Assert.assertTrue(tag in tags)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testTagNonExistingRecordFailure() {
        val tagValue = "tagValue"
        val tag = repository.addTag(tagValue)
        repository.tag(-1, tag.id)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testTagWithNonExistingTagFailure() {
        val recordValue = "recordValue"
        val record = repository.addRecord(recordValue)
        repository.tag(record.id, -1)
    }
}