package com.msagi.recordtagger.repository

import org.junit.Assert
import org.junit.Test

class RemoveTagTest : RepositoryTest() {

    @Test
    fun testRemovePassiveTagByIdSuccess() {
        val tagValue = "testTag"
        val tag = repository.addTag(tagValue)
        repository.removeTag(tag.id)
    }

    @Test
    fun testRemoveActiveTagByIdSuccess() {
        val tag = repository.addTag("tagValue")
        var record = repository.addRecord("recordValue")
        record = repository.tag(record.id, tag.id)
        Assert.assertEquals(1, record.tags.size)
        repository.removeTag(tag.id)
        val recordReadAgain = repository.getRecord(record.id)
        Assert.assertNotNull(recordReadAgain)
        Assert.assertTrue(recordReadAgain!!.tags.isEmpty())
    }

    @Test
    fun testRemoveNonExistingTagHasNoEffect() {
        repository.removeTag(-1)
    }
}