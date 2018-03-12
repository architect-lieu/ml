package com.sasaki.mla.main

import org.apache.spark.mllib.feature.Normalizer
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.regression.LinearRegressionWithSGD

import com.sasaki.spark.SparkHandler

/**
 * @Author Sasaki
 * @Mail redskirt@outlook.com
 * @Timestamp Mar 9, 2018 10:43:10 AM
 * @Description Spark线性回归，房价预测
 *
 * +======================================================================+
 * |价格/price(1000$s)		面积/area(feet^2)		房间数/bedrooms								 |
 * |----------------------------------------------------------------------+
 * |446									5831.933451422327		7														 |
 * |132									1457.9792947473338		4														 |
 * +======================================================================+
 */
object HousingPricePredictingByLinearRegressionProcess extends SparkHandler {

  lazy val spark = buildLocalSparkSession(false)
  lazy val sc = spark.sparkContext

  implicit val _spark_ = spark
  
  val basePath = "/Users/sasaki/datasets"
  val path = s"$basePath/housing_price"
  
  val clomns = Array("price", "area", "room_num")
  val Y                   = "y"               // 实际值
  val Y_                  = "y_"              // 预测值A
  val T                   = "t"               // 预测值T
  val FEATURES            = "features"        // 特征向量
  val SCALED_FEATURES     = "scaled_features"
    
  def main(args: Array[String]): Unit = {

    invokeSessionHandler { () =>
      val tmp = scala.reflect.io.File(s"$path/.part-00000.crc")
      if (tmp.exists) tmp.delete()

      val dsSample = spark.read.textFile(path).rdd.map { r =>
        val o = r.split(',').map(_.toDouble)
        val v = Vectors.dense(o(1) / 4, o(2) / 4000)
//        val scaler = new Normalizer(4)
        (o(0).toDouble / 80000, /*scaler.transform(v)*/v)
      }

      val rddSample = spark.createDataFrame(dsSample).rdd.map { r =>
        LabeledPoint(r.getAs(0), r.getAs(1))
      }.cache()
      rddSample.take(5).foreach(println)
      val countSample = rddSample.count()

      // 设置训练参数，建立回归模型
      val numIterations = 250
      val stepSize = 0.3
      val miniBatchFraction = 1.0
      val model = LinearRegressionWithSGD.train(rddSample, numIterations, stepSize, miniBatchFraction)
      // 权重，计算结果的theta参数向量
      println(model.weights)
      // 截距项
      println(model.intercept)

      // 测试样本
      val prediction = model.predict(rddSample.map(_.features))
      val predictionAndLabel = prediction.zip(rddSample.map(_.label))
      val print_predict = predictionAndLabel.take(20)
      println("prediction" + "\t" + "label")
      print_predict.foreach(o => println(o._1 + " -> " + o._2))

      // 计算训练误差
      val loss = predictionAndLabel.map {
        case (p, l) => //
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
}

case class Sample(price: Int, area: Double, roomNum: Int)