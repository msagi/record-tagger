package com.msagi.recordtagger.repository

import org.junit.Assert
import org.junit.Test


class SetRecordTest : RepositoryTest() {

    @Test
    fun testSetRecordSuccess() {
        val recordValue = "record"
        val recordValue2 = "record2"
        val record = repository.addRecord(recordValue)
        val updatedRecord = repository.setRecord(record.id, recordValue2)
        Assert.assertNotNull(updatedRecord)
        Assert.assertEquals(recordValue2, updatedRecord.record)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testTryToUpdateNonExistingRecordFailure() {
        repository.setRecord(-1, "newValue")
    }

}