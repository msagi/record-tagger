package com.msagi.recordtagger.repository

import org.junit.Assert
import org.junit.Test

class GetNumberOfRecordsTest : RepositoryTest() {

    @Test
    fun testGetNumberOfRecordsWithNoRecordNoFilterSuccess() {
        Assert.assertEquals(0, repository.getNumberOfRecords())
    }

    @Test
    fun testGetNumberOfRecordsWithNoRecordButTwoFiltersSuccess() {
        val tag = repository.addTag("tagValue")
        val tag2 = repository.addTag("tagValue2")
        Assert.assertEquals(0, repository.getNumberOfRecords(listOf(tag, tag2)))
    }

    @Test
    fun testGetNumberOfRecordsWithNoMatchingFilterSuccess() {
        val tag = repository.addTag("tagValue")
        val tag2 = repository.addTag("tagValue2")
        repository.addRecord("recordValue")
        Assert.assertEquals(0, repository.getNumberOfRecords(listOf(tag)))
        Assert.assertEquals(0, repository.getNumberOfRecords(listOf(tag2)))
        Assert.assertEquals(0, repository.getNumberOfRecords(listOf(tag, tag2)))
    }

    @Test
    fun testGetNumberOfRecordsWithPartialMatchingFilterSuccess() {
        val tag = repository.addTag("tagValue")
        val tag2 = repository.addTag("tagValue2")
        val record = repository.addRecord("recordValue")
        repository.tag(record.id, tag.id)
        Assert.assertEquals(1, repository.getNumberOfRecords(listOf(tag)))
        Assert.assertEquals(0, repository.getNumberOfRecords(listOf(tag2)))
        Assert.assertEquals(0, repository.getNumberOfRecords(listOf(tag, tag2)))
        repository.tag(record.id, tag2.id)
        repository.untag(record.id, tag.id)
        Assert.assertEquals(0, repository.getNumberOfRecords(listOf(tag)))
        Assert.assertEquals(1, repository.getNumberOfRecords(listOf(tag2)))
        Assert.assertEquals(0, repository.getNumberOfRecords(listOf(tag, tag2)))
    }

    @Test
    fun testGetNumberOfRecordsWithFullyMatchingFilterSuccess() {
        val tag = repository.addTag("tagValue")
        val tag2 = repository.addTag("tagValue2")
        val record = repository.addRecord("recordValue")
        repository.tag(record.id, tag.id)
        repository.tag(record.id, tag2.id)
        Assert.assertEquals(1, repository.getNumberOfRecords(listOf(tag)))
        Assert.assertEquals(1, repository.getNumberOfRecords(listOf(tag2)))
        Assert.assertEquals(1, repository.getNumberOfRecords(listOf(tag, tag2)))
    }

    @Test(expected = IllegalArgumentException::class)
    fun testGetNumberOfRecordsWithInvalidFilterFailure() {
        val tag = repository.addTag("tagValue")
        val tag2 = repository.addTag("tagValue2")
        val record = repository.addRecord("recordValue")
        repository.tag(record.id, tag.id)
        repository.tag(record.id, tag2.id)
        repository.removeTag(tag2.id)
        repository.getNumberOfRecords(listOf(tag, tag2))
    }
}