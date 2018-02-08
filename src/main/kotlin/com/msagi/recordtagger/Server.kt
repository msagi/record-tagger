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

package com.msagi.recordtagger

import com.github.salomonbrys.kodein.*
import com.msagi.recordtagger.repository.Repository
import com.msagi.recordtagger.repository.sql.SqlRepository
import com.msagi.recordtagger.restapi.records.KEY_RECORDS_PAGE_SIZE
import com.msagi.recordtagger.restapi.records.RecordsRestApi
import com.msagi.recordtagger.restapi.tags.TagsRestApi
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.content.resource
import io.ktor.content.resources
import io.ktor.content.static
import io.ktor.features.CallLogging
import io.ktor.features.Compression
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.gson.gson
import io.ktor.http.HttpHeaders
import io.ktor.response.respondRedirect
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.engine.embeddedServer
import io.ktor.server.jetty.Jetty
import io.ktor.server.netty.Netty
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.text.DateFormat
import java.util.concurrent.TimeUnit

interface RequestHandler {
    fun getHandler(): Routing.() -> Unit
}

const val KEY_DEBUG = "debug"
const val KEY_SERVER_ENGINE = "engine"
const val KEY_SERVER_PORT = "port"

enum class ServerEngine {
    Netty,
    Jetty
}

class Server constructor(di: Kodein? = null) : KodeinAware {

    override val kodein = di ?: Kodein {

        constant(KEY_DEBUG) with false

        bind<Logger>() with multiton { cls: Class<*> -> LoggerFactory.getLogger(cls) }

        bind<Repository>() with singleton {
            SqlRepository(
                    "com.mysql.jdbc.Driver",
                    "jdbc:mysql://127.0.0.1/recordtagger",
                    "recota",
                    "recota"
            )
        }

        constant(KEY_SERVER_ENGINE) with ServerEngine.Netty
        constant(KEY_SERVER_PORT) with 8080

        constant(KEY_RECORDS_PAGE_SIZE) with 30
    }

    private val logger: Logger = kodein.withClassOf(this).instance()

    private lateinit var server: ApplicationEngine

    private fun Application.module() {
        val debug: Boolean = kodein.instance(KEY_DEBUG)

        install(DefaultHeaders) {
            header(HttpHeaders.Server, "RecoTa/1.0")
        }
        install(Compression)
        install(ContentNegotiation) {
            gson {
                setDateFormat(DateFormat.LONG)
                if (debug) { setPrettyPrinting() }
                disableHtmlEscaping()
            }
        }
        if (debug) {
            install(CallLogging)
        }
        routing {
            static("static") {
                resources("css")
                resources("js")
                resource("index.html")
                resource("tag-admin.html")
            }
            get("/") {
                this.call.respondRedirect("/static/index.html")
            }
        }
        routing(RecordsRestApi(kodein).getHandler())
        routing(TagsRestApi(kodein).getHandler())
    }

    fun start() {
        logger.info("Initializing repository...")

        val repository: Repository = kodein.instance()
        repository.initialize()

        val engine: ServerEngine = kodein.instance(KEY_SERVER_ENGINE)
        logger.info("Starting Server Engine ($engine)...")

        val port: Int = kodein.instance(KEY_SERVER_PORT)

        server = when (engine) {
            ServerEngine.Netty -> embeddedServer(Netty, port) { module() }
            ServerEngine.Jetty -> embeddedServer(Jetty, port) { module() }
        }
        server.start()
    }

    fun stop() {
        server.stop(2, 2, TimeUnit.SECONDS)
    }
}

fun main(args: Array<String>) {
    Server().start()
}