package com.msagi.recordtagger.repository.sql

import org.jetbrains.exposed.dao.IntIdTable

object SqlTags : IntIdTable(name = "tag") {
    val tag = varchar("tag", 32).uniqueIndex()
}
