package com.msagi.recordtagger.repository

import org.junit.Assert
import org.junit.Test

class UntagTest : RepositoryTest() {

    @Test
    fun testUntagOneOfTwoTagsSuccess() {
        val recordValue = "recordValue"
        var record = repository.addRecord(recordValue)
        val tagValue = "tagValue"
        val tag = repository.addTag(tagValue)
        val tag2Value = "tag2Value"
        val tag2 = repository.addTag(tag2Value)
        repository.tag(record.id, tag.id)
        repository.tag(record.id, tag2.id)
        record = repository.untag(record.id, tag.id)
        val tags = record.tags
        Assert.assertTrue(tags.size == 1)
        Assert.assertTrue(tag2 in tags)
    }

    @Test
    fun testUntagTwoOfTagsSuccess() {
        val recordValue = "recordValue"
        var record = repository.addRecord(recordValue)
        val tagValue = "tagValue"
        val tag = repository.addTag(tagValue)
        val tag2Value = "tag2Value"
        val tag2 = repository.addTag(tag2Value)
        repository.tag(record.id, tag.id)
        repository.tag(record.id, tag2.id)
        repository.untag(record.id, tag.id)
        record = repository.untag(record.id, tag2.id)
        Assert.assertTrue(record.tags.isEmpty())
    }

    @Test(expected = IllegalArgumentException::class)
    fun testUntagNonExistingRecordFailure() {
        val tagValue = "tagValue"
        val tag = repository.addTag(tagValue)
        repository.tag(-1, tag.id)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testUntagWithNonExistingTagFailure() {
        val recordValue = "recordValue"
        val record = repository.addRecord(recordValue)
        repository.tag(record.id, -1)
    }
}