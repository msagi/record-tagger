/*
 * Copyright 2018 Miklos Sagi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.msagi.recordtagger.repository.sql

import com.msagi.recordtagger.repository.Record
import com.msagi.recordtagger.repository.Repository
import com.msagi.recordtagger.repository.Tag
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import org.jetbrains.exposed.sql.transactions.transaction

class SqlRepository constructor(driver: String, connectString: String, dbUser: String, dbPassword: String) :
        Repository {

    init {
        Database.connect(connectString, driver = driver, user = dbUser, password = dbPassword)
    }

    override fun initialize() {
        transaction {
            create(SqlRecords, SqlTags, SqlRecordsToTags)
        }
    }

    override fun reset() {
        transaction {
            SqlRecordsToTags.deleteAll()
            SqlTags.deleteAll()
            SqlRecords.deleteAll()
        }
    }

    // RECORDS API

    override fun addRecord(recordValue: String): Record {
        lateinit var record: Record
        transaction {
            val sqlRecordExisting = SqlRecords.select { SqlRecords.record eq recordValue }.firstOrNull()
            sqlRecordExisting?.let {
                val tags = getRecordTags(sqlRecordExisting[SqlRecords.id].value)
                record = Record(sqlRecordExisting[SqlRecords.id].value, sqlRecordExisting[SqlRecords.record], tags)
                return@transaction
            }

            SqlRecords.insertAndGetId { it[SqlRecords.record] = recordValue }?.let {
                record = Record(it.value, recordValue, listOf())
                return@transaction
            }

            throw IllegalStateException("error creating record")
        }
        return record
    }

    override fun getRecord(recordId: Int): Record? {
        var record: Record? = null
        transaction {
            val tags = getRecordTags(recordId)
            record = SqlRecords.select { SqlRecords.id eq recordId }.firstOrNull()?.let {
                Record(it[SqlRecords.id].value, it[SqlRecords.record], tags)
            }
        }
        return record
    }

    override fun setRecord(recordId: Int, recordValue: String): Record {
        lateinit var record: Record
        transaction {
            SqlRecords.update({ SqlRecords.id eq recordId }) {
                it[SqlRecords.record] = recordValue
            }
            record = getRecord(recordId) ?: throw IllegalArgumentException("record not found")
        }
        return record
    }

    override fun removeRecord(recordId: Int) {
        transaction {
            SqlRecordsToTags.deleteWhere { SqlRecordsToTags.recordId eq recordId }
            SqlRecords.deleteWhere { SqlRecords.id eq recordId }
        }
    }

    override fun getNumberOfRecords(tags: List<Tag>): Int {
        var result = 0
        transaction {
            if (SqlTags.select(SqlTags.id.inList(tags.map { it.id })).count() != tags.size) {
                throw IllegalArgumentException("invalid tag found in tags")
            }
            result = when {
                tags.isEmpty() -> SqlRecords.selectAll().count()
                else -> SqlRecordsToTags
                        .select(SqlRecordsToTags.tagId.inList(tags.map { it.id }))
                        .groupBy(SqlRecordsToTags.recordId)
                        .having {
                            SqlRecordsToTags.recordId.count().eq(tags.size)
                        }
                        .orderBy(SqlRecordsToTags.recordId)
                        .count()
            }
        }
        return result
    }

    override fun getRecords(tags: List<Tag>, limit: Int, offset: Int): List<Record> {
        val records = mutableListOf<Record>()
        transaction {
            if (SqlTags.select(SqlTags.id.inList(tags.map { it.id })).count() != tags.size) {
                throw IllegalArgumentException("invalid tag found in tags")
            }
            when {
                tags.isEmpty() -> SqlRecords.selectAll()
                else -> (SqlRecords innerJoin SqlRecordsToTags)
                        .select { SqlRecordsToTags.tagId inList tags.map { it.id } }
                        .groupBy(SqlRecordsToTags.recordId)
                        .having {
                            SqlRecordsToTags.recordId.count().eq(tags.size)
                        }
            }.orderBy(SqlRecords.id)
                    .limit(limit, offset)
                    .map { sqlRecord ->

                        val recordTags = (SqlRecordsToTags innerJoin SqlTags)
                                .select { SqlRecordsToTags.recordId eq sqlRecord[SqlRecords.id] }
                                .map { Tag(it[SqlTags.id].value, it[SqlTags.tag]) }

                        records.add(Record(sqlRecord[SqlRecords.id].value, sqlRecord[SqlRecords.record], recordTags))
                    }
        }
        return records.toList()
    }

    private fun getRecordTags(recordId: Int): List<Tag> {
        val tags = mutableListOf<Tag>()
        (SqlRecordsToTags innerJoin SqlTags)
                .slice(SqlTags.id, SqlTags.tag)
                .select { SqlRecordsToTags.recordId eq recordId }
                .forEach {
                    tags.add(Tag(it[SqlTags.id].value, it[SqlTags.tag]))
                }
        return tags.toList()
    }

    // TAG API

    override fun addTag(tagValue: String): Tag {
        lateinit var tag: Tag
        transaction {
            SqlTags.insertIgnore {
                it[SqlTags.tag] = tagValue
            }

            SqlTags.select { SqlTags.tag eq tagValue }.firstOrNull()?.let {
                tag = Tag(it[SqlTags.id].value, it[SqlTags.tag])
                return@transaction
            }

            throw IllegalStateException("error adding tag")
        }
        return tag
    }

    override fun removeTag(tagId: Int) {
        transaction {
            SqlRecordsToTags.deleteWhere { SqlRecordsToTags.tagId eq tagId }
            SqlTags.deleteWhere { SqlTags.id eq tagId }
        }
    }

    override fun getTag(tagId: Int): Tag? {
        var tag: Tag? = null
        transaction {
            SqlTags.select { SqlTags.id eq tagId }.firstOrNull()?.let {
                tag = Tag(it[SqlTags.id].value, it[SqlTags.tag])
            }
        }
        return tag
    }

    override fun setTag(tagId: Int, tagValue: String): Tag {
        lateinit var tag: Tag
        transaction {
            SqlTags.update({ SqlTags.id eq tagId }) {
                it[SqlTags.tag] = tagValue
            }
            tag = getTag(tagId) ?: throw IllegalArgumentException("tag not found")
        }
        return tag
    }

    override fun getAllTags(): List<Tag> {
        val tags = mutableListOf<Tag>()
        transaction {
            SqlTags.selectAll()
                    .orderBy(SqlTags.tag)
                    .forEach {
                        tags.add(Tag(it[SqlTags.id].value, it[SqlTags.tag]))
                    }
        }
        return tags.toList()
    }

    override fun getTagsByValues(tagValues: List<String>): List<Tag> {
        if (tagValues.isEmpty()) return listOf()
        val tags = mutableListOf<Tag>()
        transaction {
            SqlTags.select(SqlTags.tag.inList(tagValues))
                    .orderBy(SqlTags.tag)
                    .forEach { tags.add(Tag(it[SqlTags.id].value, it[SqlTags.tag])) }
        }
        return tags.toList()
    }

    // TAGGING API

    override fun tag(recordId: Int, tagId: Int): Record {
        lateinit var record: Record
        transaction {
            val sqlRecord = SqlRecords.select { SqlRecords.id eq recordId }.firstOrNull()
            val sqlTag = SqlTags.select { SqlTags.id eq tagId }.firstOrNull()
            when {
                sqlRecord == null -> throw IllegalArgumentException("record not found")
                sqlTag == null -> throw IllegalArgumentException("tag not found")
                else -> {
                    SqlRecordsToTags.insertIgnore {
                        it[SqlRecordsToTags.recordId] = EntityID(recordId, SqlRecords)
                        it[SqlRecordsToTags.tagId] = EntityID(tagId, SqlTags)
                    }
                    val tags = getRecordTags(recordId)
                    record = Record(sqlRecord[SqlRecords.id].value, sqlRecord[SqlRecords.record], tags)
                }
            }
        }
        return record
    }

    override fun untag(recordId: Int, tagId: Int): Record {
        lateinit var record: Record
        transaction {
            val sqlRecord = SqlRecords.select { SqlRecords.id eq recordId }.firstOrNull()
            val sqlTag = SqlTags.select { SqlTags.id eq tagId }.firstOrNull()
            when {
                sqlRecord == null -> throw IllegalArgumentException("record not found")
                sqlTag == null -> throw IllegalArgumentException("tag not found")
                else -> {
                    SqlRecordsToTags.deleteWhere {
                        (SqlRecordsToTags.recordId eq recordId) and (SqlRecordsToTags.tagId eq tagId)
                    }
                    val tags = getRecordTags(recordId)
                    record = Record(sqlRecord[SqlRecords.id].value, sqlRecord[SqlRecords.record], tags)
                }
            }
        }
        return record
    }

}
