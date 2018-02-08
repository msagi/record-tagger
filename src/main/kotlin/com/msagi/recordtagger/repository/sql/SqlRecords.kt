package com.msagi.recordtagger.repository.sql

import org.jetbrains.exposed.dao.IntIdTable

object SqlRecords : IntIdTable(name = "record") {
    val record = varchar("record", 1500)
}