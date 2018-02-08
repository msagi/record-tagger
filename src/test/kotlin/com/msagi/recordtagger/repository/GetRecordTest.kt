package com.msagi.recordtagger.repository

import org.junit.Assert
import org.junit.Test

class GetRecordTest : RepositoryTest() {

    @Test
    fun testGetRecordSuccess() {
        val recordValue = "record"
        val record = repository.addRecord(recordValue)
        val record2 = repository.getRecord(record.id)
        Assert.assertNotNull(record2)
        Assert.assertEquals(recordValue, record2!!.record)
    }

    @Test
    fun testGetMultipleRecordSuccess() {
        val recordValue = "record"
        val record = repository.addRecord(recordValue)
        val recordValue2 = "record2"
        val record2 = repository.addRecord(recordValue2)
        val recordRead = repository.getRecord(record.id)
        val record2Read = repository.getRecord(record2.id)
        Assert.assertNotNull(recordRead)
        Assert.assertNotNull(record2Read)
        Assert.assertEquals(recordRead!!.id, record.id)
        Assert.assertEquals(record2Read!!.id, record2.id)
    }

    @Test
    fun testGetRecordWithTagsSuccess() {
        val recordValue = "record"
        val record = repository.addRecord(recordValue)
        val tagValue = "tag"
        val tag = repository.addTag(tagValue)
        repository.tag(record.id, tag.id)
        val tagValue2 = "tag2"
        val tag2 = repository.addTag(tagValue2)
        repository.tag(record.id, tag2.id)
        val recordRead = repository.getRecord(record.id)
        Assert.assertNotNull(recordRead)
        val tags = recordRead!!.tags
        Assert.assertTrue(tag in tags)
        Assert.assertTrue(tag2 in tags)
    }
}