package com.msagi.recordtagger

import com.github.salomonbrys.kodein.*
import com.msagi.recordtagger.repository.Repository
import com.msagi.recordtagger.repository.sql.SqlRepository
import io.restassured.builder.RequestSpecBuilder
import io.restassured.filter.log.RequestLoggingFilter
import io.restassured.filter.log.ResponseLoggingFilter
import io.restassured.specification.RequestSpecification
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Configuration provider for REST API unit tests. Note: the REST API must be connected to the repository,
 * the REST API tests will fail otherwise.
 */
class TestConfigProvider {
    companion object {

        private val kodein: Kodein = Kodein {

            bind<Repository>() with singleton {
                SqlRepository(
                        "com.mysql.jdbc.Driver",
                        "jdbc:mysql://127.0.0.1/recordtaggertest",
                        "recota",
                        "recota"
                )
            }

            bind<RequestSpecification>() with singleton {
                RequestSpecBuilder()
                        .setBaseUri("http://localhost:8080/")
                        .addFilter(ResponseLoggingFilter())//log request and response for better debugging. You can also only log if a requests fails.
                        .addFilter(RequestLoggingFilter())
                        .build()
            }

            bind<Server>() with singleton {
                Server(this)
            }

            bind<Logger>() with multiton { cls: Class<*> -> LoggerFactory.getLogger(cls) }

            constant("debug") with true

            constant("recordsPageSize") with 30

            constant("serverPort") with 8080
        }

        fun provideTestRepository() : Repository = kodein.instance()

        fun provideTestRestApi() : RequestSpecification = kodein.instance()

        fun provideTestServer() : Server = kodein.instance()
    }
}