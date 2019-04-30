import java.lang.Boolean

import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010._


object KafkaStreamTest {

  def main(args: Array[String]) {
    val ssBuilder = SparkSession.builder()
    ssBuilder.master("local[*]")
    val ss = ssBuilder.appName("Test").getOrCreate()
    ss.sparkContext.setLogLevel("ERROR")
    //    val ssc = new StreamingContext(ss.sparkContext, Seconds(5))
    getDfFromKafka(ss)
    //    getFromKafka(ssc, ss)
    //    ssc.start()
    //    ssc.awaitTermination()
    ss.stop()
  }

  private def getFromKafka(ssc: StreamingContext, ss: SparkSession): Unit = {
    val topics = Array("odd", "even")
    val stream = KafkaUtils.createDirectStream(
      ssc,
      PreferConsistent,
      Subscribe[String, String](topics, getKafkaSettings))
    val stt = stream.map(record => (record.key, record.value))
    stt.foreachRDD(i => i.foreach(j => println(j._2)))
  }

  private def getKafkaSettings: Map[String, Object] = {
    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> "localhost:9092",
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> "use_a_separate_group_id_for_each_stream",
      "auto.offset.reset" -> "latest",
      "enable.auto.commit" -> (false: Boolean)
    )
    kafkaParams
  }


  private def getDfFromKafka(ss: SparkSession): Unit = {
    val df = ss
      .readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "localhost:9092")
      .option("subscribe", "odd, even")
      .load()
    val df2 = df.selectExpr("cast(key as string) key",
      "cast(value as string) value",
      "topic")
    printStreamDf(df2)
  }

  val printStreamDf: DataFrame => Unit = (x: DataFrame) => x.writeStream.outputMode("append").format("console").start().awaitTermination()

}
