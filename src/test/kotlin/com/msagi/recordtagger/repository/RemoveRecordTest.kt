package com.msagi.recordtagger.repository

import org.junit.Test

class RemoveRecordTest : RepositoryTest() {

    @Test
    fun testRemoveRecordByIdSuccess() {
        val recordValue = "testValue"
        val record = repository.addRecord(recordValue)
        repository.removeRecord(record.id)
    }

    @Test
    fun testRemoveNonExistingRecordHasNoEffect() {
        repository.removeRecord(-1)
    }
}