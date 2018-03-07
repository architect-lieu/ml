package com.sasaki.mla.sample

import com.sasaki.spark.SparkHandler

object Test extends SparkHandler {
  
   lazy val spark = buildLocalSparkSession(false)
  implicit val _spark_ = spark
  
  def main(args: Array[String]): Unit = {
    
    // 实现RDD元素二维组合算法，可以考虑用笛卡尔积加索引方式和迭代zip方式
     /**
         * a b c d e 
         *   a b c d e 
         *     a b c d e 
         */
    
     invokeSessionHandler { () =>
       val rdd = spark.sparkContext.parallelize(List("a", "b", "c", "d", "e"))
       val rddIndex___Item = rdd.zipWithIndex().map(o => (o._2, o._1))
       rddIndex___Item.cartesian(rddIndex___Item).filter(o => o._1._1 < o._2._1 )
         .map{ case(o, o_) => (o._2 -> o_._2 )}  foreach println
       
     }
  }
   
}