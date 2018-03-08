package com.sasaki.mla.main

import com.sasaki.spark.SparkHandler
import scala.util.Random
import breeze.linalg._
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
    
    val data = for(i <- 0 until 20) yield {
      (random.nextDouble() * 3, DenseVector.rand[Double](5) :*= 11.0)
    }
    
    val parameter = DenseVector.rand[Double](5) :*= 2.5
    
    invokeSessionHandler { () =>  
      val rddData = sc.parallelize(data)
      
      rddData foreach println
      
    }
  }
  
  
}