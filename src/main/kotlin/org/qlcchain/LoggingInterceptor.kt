package org.qlcchain

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException


internal class LoggingInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val t1 = System.currentTimeMillis()
//        println(String.format("Sending request %s on %s%n%s",
//                request.url(), chain.connection(), request.toString()))

        val tag = request.tag()
        val response = chain.proceed(request)

        val t2 = System.currentTimeMillis()
//        println(String.format("Received response for %s in %.1fms%n%s",
//                response.request().url(), (t2 - t1) / 1e6, response.headers()))

        println(String.format("Received(%s) response in [%s] %s ms", response.request().url(), tag, (t2 - t1)))

        return response
    }
}