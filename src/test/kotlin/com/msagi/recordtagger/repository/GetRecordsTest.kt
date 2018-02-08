package com.msagi.recordtagger.repository

import org.junit.Assert
import org.junit.Test

class GetRecordsTest : RepositoryTest() {

    @Test
    fun testGetRecordsWithDefaultsSuccess() {
        val record = repository.addRecord("recordValue")
        val record2 = repository.addRecord("record2Value")
        val record3 = repository.addRecord("record3Value")
        val records = repository.getRecords()
        Assert.assertNotNull(records)
        Assert.assertEquals(3, records.size)
        Assert.assertTrue(record in records)
        Assert.assertTrue(record2 in records)
        Assert.assertTrue(record3 in records)
    }

    @Test
    fun testGetRecordsWithLimitSuccess() {
        val record = repository.addRecord("recordValue")
        val record2 = repository.addRecord("record2Value")
        val record3 = repository.addRecord("record3Value")
        val records = repository.getRecords(limit = 2)
        Assert.assertNotNull(records)
        Assert.assertEquals(2, records.size)
        Assert.assertTrue(record in records)
        Assert.assertTrue(record2 in records)
        Assert.assertFalse(record3 in records)
    }

    @Test
    fun testGetRecordsWithOffsetSuccess() {
        val record = repository.addRecord("recordValue")
        val record2 = repository.addRecord("record2Value")
        val record3 = repository.addRecord("record3Value")
        val records = repository.getRecords(offset = 1)
        Assert.assertNotNull(records)
        Assert.assertEquals(2, records.size)
        Assert.assertFalse(record in records)
        Assert.assertTrue(record2 in records)
        Assert.assertTrue(record3 in records)
    }

    @Test
    fun testGetRecordsWithFilterSuccess() {
        val tag = repository.addTag("tagValue")
        val tag2 = repository.addTag("tag2Value")
        var record = repository.addRecord("recordValue")
        var record2 = repository.addRecord("record2Value")
        var record3 = repository.addRecord("record3Value")
        record = repository.tag(record.id, tag.id)
        record2 = repository.tag(record2.id, tag2.id)
        record3 = repository.tag(record3.id, tag.id)
        record3 = repository.tag(record3.id, tag2.id)
        val records = repository.getRecords(listOf(tag))
        Assert.assertNotNull(records)
        Assert.assertEquals(2, records.size)
        Assert.assertTrue(record in records)
        Assert.assertFalse(record2 in records)
        Assert.assertTrue(record3 in records)
        val records2 = repository.getRecords(listOf(tag2))
        Assert.assertNotNull(records2)
        Assert.assertEquals(2, records2.size)
        Assert.assertFalse(record in records2)
        Assert.assertTrue(record2 in records2)
        Assert.assertTrue(record3 in records2)
    }

    @Test
    fun testGetRecordsWithFilterAndLimitAndOffsetSuccess() {
        val tag = repository.addTag("tagValue")
        var record = repository.addRecord("recordValue")
        val record2 = repository.addRecord("record2Value")
        var record3 = repository.addRecord("record3Value")
        var record4 = repository.addRecord("record4Value")
        record = repository.tag(record.id, tag.id)
        record3 = repository.tag(record3.id, tag.id)
        record4 = repository.tag(record4.id, tag.id)
        val records = repository.getRecords(listOf(tag), 2, 1)
        Assert.assertNotNull(records)
        Assert.assertEquals(2, records.size)
        Assert.assertFalse(record in records) //due to offset = 1
        Assert.assertFalse(record2 in records)
        Assert.assertTrue(record3 in records)
        Assert.assertTrue(record4 in records)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testGetRecordsWithInvalidFilterFailure() {
        val tag = repository.addTag("tagValue")
        val record = repository.addRecord("recordValue")
        repository.tag(record.id, tag.id)
        repository.removeTag(tag.id)
        repository.getRecords(listOf(tag))
    }
}