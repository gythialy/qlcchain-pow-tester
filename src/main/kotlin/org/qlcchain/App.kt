package org.qlcchain

import org.abstractj.kalium.encoders.Encoder.HEX
import org.abstractj.kalium.keys.KeyPair
import org.qlcchain.model.Key
import java.util.concurrent.Executors
import kotlin.system.measureTimeMillis

object App {

    @JvmStatic
    fun main(args: Array<String>) {
        val keys = mutableListOf<Key>()
//        val url = "https://pow.qlcchain.online"
        val url = "http://localhost:7076"
        val keySize = 100
        val chunkSize = 20
        prepare(keys, keySize)
        val threadSize = keySize/chunkSize
        val executor = Executors.newFixedThreadPool(threadSize)

        keys.chunked(threadSize).forEachIndexed { index, items ->
            val worker = FetchWork("QLC Worker $index",url, items)
            executor.execute(worker)
        }

        executor.shutdown()
        while (!executor.isTerminated) {
        }
        println("Finished all threads")
    }

    private fun prepare(keys: MutableList<Key>, size: Int) {
        val len = size / 2;
        val prepareTime = measureTimeMillis {
            var index =0
            for (i in 1..len) {
                val key = KeyPair()
                val pri = HEX.encode(key.privateKey.toBytes()).toUpperCase()
                val pub = HEX.encode(key.publicKey.toBytes()).toUpperCase()
                keys.add(Key(index, pri))
                index++
                keys.add(Key(index, pub))
                index++
            }
        }
        println("prepare ${len * 2} hashes cost $prepareTime ms.")
    }
}