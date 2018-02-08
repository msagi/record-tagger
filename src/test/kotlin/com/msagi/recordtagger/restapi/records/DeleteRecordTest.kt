package com.msagi.recordtagger.restapi.records

import com.msagi.recordtagger.repository.Record
import com.msagi.recordtagger.restapi.RestApiTest
import org.junit.Before
import org.junit.Test

class DeleteRecordTest : RestApiTest() {

    private val records = mutableListOf<Record>()

    @Before
    override fun before() {
        super.before()

        (0 until 10).mapTo(records) { repository.addRecord("recordValue$it") }
    }

    @Test
    fun testDeleteRecordSuccess() {
        given {
            emptyBody()
        } `when` {
            delete("/records/${records[3].id}")
        } then {
            statusCode(204)
        }
    }

    @Test
    fun testDeleteRecordInvalidBadRequest() {
        given {
            emptyBody()
        } `when` {
            delete("/records/thisIsNotANumber")
        } then {
            statusCode(400)
        }
    }

    @Test
    fun testDeleteNonExistingRecordNotFound() {
        given {
            emptyBody()
        } `when` {
            get("/records/${Int.MAX_VALUE}")
        } then {
            statusCode(404)
        }
    }

}