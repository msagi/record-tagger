package com.msagi.recordtagger.repository.sql

import org.jetbrains.exposed.dao.IntIdTable

object SqlRecordsToTags : IntIdTable(name = "recordstotags") {
    val recordId = reference("recordId", SqlRecords)
    val tagId = reference("tagId", SqlTags)

    init {
        uniqueIndex(recordId, tagId)
    }
}