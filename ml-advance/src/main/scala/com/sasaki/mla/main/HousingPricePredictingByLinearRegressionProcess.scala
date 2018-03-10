package com.sasaki.mla.main

import com.sasaki.spark.SparkHandler
import scala.util.Random
import com.sasaki.spark.enums.SparkType.Spark
import com.sasaki.kit.ReflectHandler
import org.apache.spark.mllib.regression.LinearRegressionWithSGD
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.regression.LinearRegressionModel

/**
 * @Author Sasaki
 * @Mail redskirt@outlook.com
 * @Timestamp Mar 9, 2018 10:43:10 AM
 * @Description Spark线性回归，房价预测
 *
 * +======================================================================+
 * |价格/price		面积/area					房间数/room_num 											   |
 * |----------------------------------------------------------------------+
 * |446					5831.933451422327	7																			 |
 * |132					1457.9792947473338	4									 										 |
 * +======================================================================+
 */
object HousingPricePredictingByLinearRegressionProcess extends SparkHandler with ReflectHandler {

  lazy val spark = buildLocalSparkSession(false)
  lazy val sc = spark.sparkContext

  implicit val _spark_ = spark

  val path = s"$getClasspath/datasets/housing_price"
  val clomns = Array("price", "area", "room_num")

  def main(args: Array[String]): Unit = {
    invokeSessionHandler { () =>
      val rddSample = spark.read.textFile(path).rdd.map { r =>
        val o = r.split(',')
        LabeledPoint(o(0).toDouble, Vectors.dense(o(1).toDouble, o(2).toDouble))
      }.cache()
      rddSample.take(5).foreach(println)
      val countSample = rddSample.count()

      // 设置训练参数，建立回归模型
      val numIterations = 100
      val stepSize = 1
      val miniBatchFraction = 1.0
      val model = LinearRegressionWithSGD.train(rddSample, numIterations, stepSize, miniBatchFraction)
      model.weights
      model.intercept
    
      // 测试样本
      val prediction = model.predict(rddSample.map(_.features))
      val predictionAndLabel = prediction.zip(rddSample.map(_.label))
      val print_predict = predictionAndLabel.take(20)
      println("prediction" + "\t" + "label")
      print_predict.foreach(o => println(o._1 + "\t" + o._2))

      // 计算训练误差
      val loss = predictionAndLabel.map { case (p, l) => // 
          val err = p - l
          err * err
      }.reduce(_ + _)
      val rmse = math.sqrt(loss / countSample)
      println(s"Test RMSE = $rmse.")

      // 保存模型
//      val ModelPath = s"$getClasspath/model_persistence"
//      model.save(sc, ModelPath)
      
//      val sameModel = LinearRegressionModel.load(sc, ModelPath)
    }
  }

  //  def mock(spark: Spark) = {
  //    val r = new Random
  //    val sample =
  //      for(i <- 0 until 100) yield {
  //        val t = r.nextInt(500)
  //        Sample(t, r.nextDouble() * t * 23, t / 100 + 3)
  //      }
  //    spark.createDataFrame(sample)
  //  }

}

case class Sample(price: Int, area: Double, roomCount: Int)