package com.msagi.recordtagger.repository

import org.junit.Assert
import org.junit.Test

class AddRecordTest : RepositoryTest() {

    @Test
    fun testAddRecordSuccess() {
        val recordValue = "record"
        val record = repository.addRecord(recordValue)
        Assert.assertNotNull(record)
        Assert.assertEquals(recordValue, record.record)
    }

    @Test
    fun testAddMultipleRecordSuccess() {
        val recordValue = "record"
        val record = repository.addRecord(recordValue)
        Assert.assertNotNull(record)
        Assert.assertEquals(recordValue, record.record)
        val recordValue2 = "record2"
        val record2 = repository.addRecord(recordValue2)
        Assert.assertNotNull(record2)
        Assert.assertEquals(recordValue2, record2.record)
    }

    @Test
    fun testAddSameRecordValueMultipleTimesReturnsTheSameRecordWithTagsSuccess() {
        val recordValue = "record"
        var record = repository.addRecord(recordValue)
        Assert.assertNotNull(record)
        Assert.assertEquals(recordValue, record.record)
        val tagValue = "tag"
        val tag = repository.addTag(tagValue)
        Assert.assertNotNull(tag)
        Assert.assertEquals(tagValue, tag.tag)
        record = repository.tag(record.id, tag.id)
        val record2 = repository.addRecord(recordValue)
        Assert.assertEquals(record, record2)
        Assert.assertEquals(1, record2.tags.size)
        Assert.assertEquals(tag, record2.tags.first())
    }

}