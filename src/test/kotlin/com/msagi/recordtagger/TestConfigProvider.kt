package com.msagi.recordtagger

import com.github.salomonbrys.kodein.*
import com.msagi.recordtagger.repository.Repository
import com.msagi.recordtagger.repository.sql.SqlRepository
import com.msagi.recordtagger.restapi.records.KEY_RECORDS_PAGE_SIZE
import io.restassured.builder.RequestSpecBuilder
import io.restassured.filter.log.RequestLoggingFilter
import io.restassured.filter.log.ResponseLoggingFilter
import io.restassured.specification.RequestSpecification
import org.slf4j.Logger
import org.slf4j.LoggerFactory

const val serverPort = 8080

class TestConfigProvider {
    companion object {

        private val kodein: Kodein = Kodein {

            constant(KEY_DEBUG) with true

            bind<Logger>() with multiton { cls: Class<*> -> LoggerFactory.getLogger(cls) }

            bind<Repository>() with singleton {
                SqlRepository(
                        "com.mysql.jdbc.Driver",
                        "jdbc:mysql://127.0.0.1/recordtaggertest",
                        "recota",
                        "recota"
                )
            }

            constant(KEY_SERVER_PORT) with serverPort

            bind<RequestSpecification>() with singleton {
                RequestSpecBuilder()
                        .setBaseUri("http://localhost:$serverPort/")
                        .addFilter(ResponseLoggingFilter())
                        .addFilter(RequestLoggingFilter())
                        .build()
            }

            bind<Server>() with singleton {
                Server(this)
            }

            constant(KEY_SERVER_ENGINE) with ServerEngine.Netty

            constant(KEY_RECORDS_PAGE_SIZE) with 30
        }

        fun provideTestRepository() : Repository = kodein.instance()

        fun provideTestRestApi() : RequestSpecification = kodein.instance()

        fun provideTestServer() : Server = kodein.instance()
    }
}