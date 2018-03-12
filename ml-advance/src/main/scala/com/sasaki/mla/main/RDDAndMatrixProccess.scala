package com.sasaki.mla.main

import com.sasaki.spark.SparkHandler
import scala.util.Random
import org.apache.spark.ml.linalg.{ DenseMatrix => SparkDenseMatrix, DenseVector => SparkDenseVector, Vectors }
import breeze.linalg.{ sum, DenseVector, DenseMatrix }
import breeze.numerics._

/**
 * @Author Sasaki
 * @Mail redskirt@outlook.com
 * @Timestamp Mar 8, 2018 10:51:11 PM
 * @Description
 */
object RDDAndMatrixProccess extends SparkHandler {

  lazy val spark = buildLocalSparkSession(false)
  lazy val sc = spark.sparkContext

  implicit val _spark_ = spark

  /**
   * 随机生成RDD：特征值，特征向量/5-dim
   */
  def main(args: Array[String]): Unit = {
    val random = new Random
    val Y                   = "y"               // 实际值
    val Y_                  = "y_"              // 预测值A
    val T                   = "t"               // 预测值T
    val FEATURES            = "features"        // 特征向量
    val SCALED_FEATURES     = "scaled_features"
    val DIMENSION           = 2

    val samples = for (i <- 0 until 10) yield 
      (random.nextDouble() * 3,
        new SparkDenseVector(
          DenseVector.vertcat(
            DenseVector.ones[Double](1),
            DenseVector.rand[Double](DIMENSION - 1) :*= 11.0).data))
    
    // 随机参数向量
    val parameter = new SparkDenseVector(DenseVector.rand[Double](DIMENSION).:*=(2.5).data)
    println(parameter)

    invokeSessionHandler { () =>
      import spark.implicits._
      import org.apache.spark.ml.feature.MinMaxScaler

      val dfData = spark.createDataFrame(samples).toDF(Y, FEATURES)
      dfData.show(false)
      
      // 用min-max标准化方式对样本数据归一化，使结果落到[0, 1]区间
      // x* = (x - min) / (max - min)
      val scaler = new MinMaxScaler()
        .setInputCol(FEATURES)
        .setOutputCol(SCALED_FEATURES)

      // Compute summary statistics and generate MinMaxScalerModel
      val scalerModel = scaler.fit(dfData)
      // rescale each feature to range [min, max].
      val dfScaledData = scalerModel.transform(dfData)

      dfScaledData.select(Y, SCALED_FEATURES).show(false)
      
      // 将dfScaledData转换成矩阵形式
      val array = dfScaledData.select(SCALED_FEATURES).rdd
        .map(_.getAs[SparkDenseVector](SCALED_FEATURES).toArray)
        .collect
        .flatMap(identity(_))
      val mSamples = new SparkDenseMatrix(10, DIMENSION, array)

      /**
       * 求特征向量预测值
       * 1）A = X * W_t
       * 2) T = g(A) = 1 / (1 + e^{-A})
       */
      dfScaledData.rdd.map { r =>
        val scaledFeatures = r.getAs[SparkDenseVector](SCALED_FEATURES)
        // 预测值A
        val resultA = sum((new DenseVector(scaledFeatures.values) :*= new DenseVector(parameter.values)).data)
        // 预测值T
        val resultT = 1.0 / (1 + math.pow(math.E, resultA))
        
        (r.getAs[Double](Y), resultA, resultT)
      }
        .toDF(Y, Y_, T)
        .show(false)
        
      /**
       * 矩阵形式计算上述步骤
       * mSamples 		->	 20 * 6
       * parameter 	-> 6 * 1
       */
      val resultA = mSamples.multiply(parameter).values
      val resultT = for(i <- 0 until resultA.size) yield 1.0 / (1 + math.pow(math.E, resultA(i)))
      val vY = samples.map(_._1)
      for(i <- 0 until vY.size) 
        println(s"y: ${vY(i)}, y_: ${resultA(i)}, t: ${resultT(i)}")
        
       /**
        * 1 1
        * 1 2
        * 2 2
        * 
        * 
        */
      val t1 = new SparkDenseMatrix(3, 2, Array(1, 1, 1, 2, 2, 2))
      val t2 = new SparkDenseMatrix(2, 1, Array(2, 1))
      val t3 = new SparkDenseVector(Array(2, 1))
      println {
        t1.multiply(t2)      }
        println {
        	t1.multiply(t3)      }
        
    }
  }
}