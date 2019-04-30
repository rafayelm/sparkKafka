import java.lang.Boolean

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.streaming.OutputMode
import org.apache.spark.sql.types.{StringType, StructField, StructType}
import org.apache.spark.streaming.{Seconds, StreamingContext}

object KafkaBatchTest {


  def main(args: Array[String]) {
    val ssBuilder = SparkSession.builder()
    if (Boolean.getBoolean("debug")) {
      ssBuilder.master("local[*]")
    } else {
      ssBuilder
        //        .config("hive.metastore.uris", "thrift://er-dmp-tst-hn01:9083")
        .config("spark.master", "local[*]")
      //        .enableHiveSupport()
    }
    val ss = ssBuilder.appName("Test").getOrCreate()
    //    doIt(ss)

    val ssc = new StreamingContext(ss.sparkContext, Seconds(2))

    doIt2(ss, ssc)
    ssc.start()

    ssc.awaitTermination()
    ss.stop()
  }

  private def doIt(ss: SparkSession): Unit = {
    import ss.implicits._

    val df = Seq(
      (8, "bat"),
      (64, "mouse"),
      (-27, "horse")
    ).toDF("key", "value")

    df.show()

    df
      .selectExpr("CAST(key AS STRING)", "CAST(value AS STRING)")
      .write
      .format("kafka")
      .option("kafka.bootstrap.servers", "localhost:9092")
      .option("topic", "topic1")
      .save()

    val df2 = ss
      .read
      .format("kafka")
      .option("kafka.bootstrap.servers", "localhost:9092")
      .option("subscribe", "topic1")
      .load()

    df2.selectExpr("CAST(key AS STRING)", "CAST(value AS STRING)")
      .as[(String, String)].show()

  }

  def doIt2(ss: SparkSession, ssc: StreamingContext): Unit = {

    // run this in terminal "nc -lk 9999"
    val lines = ssc.socketTextStream("localhost", 9999)
    lines.print()


    val schema = StructType(
      Array(StructField("transactionId", StringType, nullable = true),
        StructField("customerId", StringType),
        StructField("itemId", StringType),
        StructField("amountPaid", StringType)))

    val fileStreamDf = ss.readStream
      .textFile("/home/rambo/Documents/projects/spark/src/main/resources/new").toDF("aaa")

    val query = fileStreamDf.writeStream
      .format("console")
      .outputMode(OutputMode.Append()).start()

  }

}
