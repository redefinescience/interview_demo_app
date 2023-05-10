package com.kotlineering.interview.domain

import io.ktor.client.HttpClient
import io.ktor.client.request.accept
import io.ktor.client.request.get
import io.ktor.http.ContentType
import io.ktor.http.encodedPath
import io.ktor.http.takeFrom

// Provide methods to centralize configuration of remote calls
// (eg: setting base domain, configuring Content-Type, etc...)
abstract class RemoteApi(
    private val domain: String
) {
    protected suspend fun HttpClient.fetch(path: String) =
        get {
            url {
                accept(ContentType.Application.Json)
                takeFrom(domain)
                encodedPath = path
            }
        }
}
