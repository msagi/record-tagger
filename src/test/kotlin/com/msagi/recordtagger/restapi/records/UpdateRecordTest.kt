package com.msagi.recordtagger.restapi.records

import com.msagi.recordtagger.restapi.RestApiTest
import org.hamcrest.CoreMatchers
import org.junit.Test

class UpdateRecordTest : RestApiTest() {

    @Test
    fun testUpdateRecordSuccess() {
        val recordValue = "recordValue"
        val recordValue2 = "recordValue2"
        val record = repository.addRecord(recordValue)
        given {
            body("record=$recordValue2")
            contentType(Companion.contentTypeUrlEnc)
        } `when` {
            put("/records/${record.id}")
        } then {
            body("id", CoreMatchers.equalTo(record.id))
            body("record", CoreMatchers.equalTo(recordValue2))
            statusCode(201)
        }
    }

    @Test
    fun testUpdateInvalidRecordBadRequest() {
        val recordValue = "recordValue"
        val record = repository.addRecord(recordValue)
        given {
            emptyBody()
            contentType(Companion.contentTypeUrlEnc)
        } `when` {
            put("/records/${record.id}")
        } then {
            statusCode(400)
        }
    }

    @Test
    fun testUpdateNonExistingRecordNotFound() {
        val recordValue = "recordValue"
        given {
            body("record=$recordValue")
            contentType(Companion.contentTypeUrlEnc)
        } `when` {
            put("/records/${Int.MAX_VALUE}")
        } then {
            statusCode(404)
        }
    }

}
