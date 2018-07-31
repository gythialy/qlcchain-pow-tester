package org.qlcchain

import com.squareup.moshi.Moshi
import okhttp3.*
import org.qlcchain.model.Key
import org.qlcchain.model.Work
import org.qlcchain.model.WorkData
import java.io.IOException
import java.util.concurrent.TimeUnit


class FetchWork(private val name: String, private val url: String, private val keys: List<Key>) : Runnable {
    private val mediaType = MediaType.get("application/json; charset=utf-8")

    var client = OkHttpClient.Builder().connectTimeout(2, TimeUnit.MINUTES)
            .addInterceptor(LoggingInterceptor())
            .readTimeout(30, TimeUnit.MINUTES).writeTimeout(2, TimeUnit.MINUTES).build()!!

    @Throws(IOException::class)
    private fun post(json: String, callback: Callback) {
        val body = RequestBody.create(mediaType, json)
        val request = Request.Builder()
                .url(this.url)
                .tag(json)
                .post(body)
                .build()
        client.newCall(request).enqueue(callback)
    }

    override fun run() {
        val moshi = Moshi.Builder().build()
        val workDataAdapter = moshi.adapter<WorkData>(WorkData::class.java)
        val workAdapter = moshi.adapter<Work>(Work::class.java)
        this.keys.forEach { key ->
            val startTime = System.currentTimeMillis()
            val id = Thread.currentThread().id
            try {
                val work = Work("work_generate", key.hash)
                post(workAdapter.toJson(work), object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        System.err.println(e.message)
                        val elapsedTime = System.currentTimeMillis() - startTime
                        println("$name($id of ${Thread.currentThread().id}): [${key.id}]${key.hash}-> failed cost $elapsedTime ms.")
                    }

                    @Throws(IOException::class)
                    override fun onResponse(call: Call, response: Response) {
                        val workData = workDataAdapter.fromJson(response.body()!!.string())
                        val elapsedTime = System.currentTimeMillis() - startTime
                        println("$name($id of ${Thread.currentThread().id})[${response.code()}]: [${key.id}]${key.hash}-> ${workData!!.work} cost $elapsedTime ms.")
                    }
                })

            } catch (e: Exception) {
                System.err.println(e.message)
            }
        }
    }
}
