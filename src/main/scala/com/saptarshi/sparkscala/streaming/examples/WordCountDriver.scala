package com.saptarshi.sparkscala.streaming.examples

import org.apache.spark.SparkConf
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.Seconds

object WordCountDriver {

    def main(args: Array[String]): Unit = {
        require(args != null && args.length == 3, "Host, Port and Batch Interval (secs) required")
        val Array(host, port, batchInterval) = args

        val sparkConf = new SparkConf().setAppName("Streaming WordCount")
        val streamingContext = new StreamingContext(sparkConf, Seconds(batchInterval.toInt))

        val lines = streamingContext.socketTextStream(host, port.toInt, StorageLevel.MEMORY_AND_DISK_SER)
        val words = lines.flatMap(line => line.split(" "))
        val pairs = words.map(word => (word, 1))
        val wc = pairs.reduceByKey(_ + _)
        wc.print()

        streamingContext.start()
        streamingContext.awaitTermination()
    }

}
