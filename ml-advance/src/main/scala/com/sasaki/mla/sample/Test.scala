package com.sasaki.mla.sample

import com.sasaki.spark.SparkHandler

object Test extends SparkHandler {

  lazy val spark = buildLocalSparkSession(false)
  implicit val _spark_ = spark

  def main(args: Array[String]): Unit = {

    val r = new scala.util.Random
    /**
     * price = area * roomNum * 0.3 * area
     * price 		[50, 500]
     * area			[50, 500]
     * roomNum 	[2, 8] 
     */
    val sample =
      for (i <- 0 until 100) yield {
        val t = r.nextInt(500)
        val area = if(t < 50) t * 1.45 else t
        val roomNum = {
          val p = t / 66
          if(p < 2) p + 2 else p
        }
        Sample({
          area * 1.234 * roomNum toInt
        }, area, roomNum)
      }
    sample foreach println
    
    
    // 实现RDD元素二维组合算法，可以考虑用笛卡尔积加索引方式和迭代zip方式
    /**
     * a b c d e
     *   a b c d e
     *     a b c d e
     */

    //     invokeSessionHandler { () =>
    //       val rdd = spark.sparkContext.parallelize(1 to 1000)
    //       val rddIndex___Item = rdd.zipWithIndex().map(o => (o._2, o._1))
    //       rddIndex___Item.cartesian(rddIndex___Item).filter(o => o._1._1 < o._2._1 )
    //         .map{ case(o, o_) => (o._2 -> o_._2 )} take(10) foreach println
    //
    //     }
  }

}

case class Sample(price: Int, area: Double, roomNum: Int)