package com.sasaki.mla.main

import com.sasaki.spark.SparkHandler
import com.sasaki.kit.ReflectHandler

/**
 * @Author Sasaki
 * @Mail redskirt@outlook.com
 * @Timestamp Mar 6, 2018 6:51:20 PM
 * @Description
 *
 * user_install_list 数据格式
 * +======================================================================+
 * |上报日期			用户ID						 	安装包名																 |
 * |----------------------------------------------------------------------+
 * |2016-03-29		110d2230b4567fdf		com.lyrebirdstudio.mirror							 |
 * |2016-03-29		1146b4999fa66b9c		com.jb.gokeyboard.plugin.emojione			 |
 * |	2016-03-28		1146b4999fa66b9c		com.facebook.katana										 |
 * +======================================================================+
 *
 */
object Case01 extends SparkHandler with ReflectHandler {

  lazy val spark = buildLocalSparkSession(false)
  lazy val sc = spark.sparkContext

  implicit val _spark_ = spark
  val path = getClasspath + "datasets/user_install_list/000000_0"

  def main(args: Array[String]): Unit = {

    invokeSessionHandler { () =>
      // 0:date, 1:user_id, 2:package_name
      val rdd = spark.read.textFile(path).rdd.map(_.split('\t'))

      println("Row Count: " + rdd.count)
      val userCount = rdd.map(_(1)).distinct.count
      println("User Count: " + userCount)
      println("Date Count: " + rdd.map(_(0)).distinct.count)

      /**
       * 指定任意1天的数据，根据每个用户的安装列表，统计每个包名的安装用户数量，
       * 由大到小排序，并且取前1000个包名，最后计算这1000个包名之间的支持度和置信度。
       */
      val filterDate = "2016-03-29"
      val rddDate = rdd.filter(_(0) == filterDate)
      
      // 每个用户的安装列表
      val rddUser___installPackages = rddDate.map(r => (r(1) -> r(2)))
        .groupByKey()
      println("rddUser___installPackages Count: " + rddUser___installPackages.count)
       
      // 每个安装包的被安装次数，倒序排列
      val rddInstallCount___package_name = rddDate.map(r => (r(2), r(1)))
        .groupByKey()
        .map { case (p, u) => (u.toSet.size -> p) }
        .sortByKey(false)
      /**
       * (995,com.jb.emoji.gokeyboard)
       * (678,com.facebook.orca)
       * (585,com.facebook.katana)
       * (385,com.instagram.android)
       */

      /**
       * 分析求支持度/Support 和 置信度/Confidence 问题
       *
       * 用户user/4
       * 1 2 3 4
       *
       * 商品product/5
       * a b c d e
       * 
       * 商品二维组合
       * [(a, b), (a, c), (a, d) ...]
       *
       * 每个用户的商品购买清单
       * 1 : a b c
       * 2 : a d
       * 3 : c e
       * 4 : a d e
       *
       * --> 分别求5种商品两两组合(p1, p2)在购买清单中的命中数
       *
       * a : 3
       * b : 10
       * c : 4
       * d : 6
       * e : 8
       *
       */

      // TopN的安装包
      val rddPackage1000 = sc.parallelize(rddInstallCount___package_name.map(_._2).take(10))
      println("TopN的安装包：")
      rddPackage1000 foreach println
      
      // TopN的安装包，带索引
      val rddIndex___Package1000 = rddPackage1000.zipWithIndex().map(o => (o._2, o._1))
      
      // 实现RDD元素的二维组合算法得到任意包名的二维组合
      val rddCombinePackage1000 = rddIndex___Package1000.cartesian(rddIndex___Package1000)
        .filter(o => o._1._1 < o._2._1)
        .map { case (o, o_) => (o._2 -> o_._2) }
      println("安装包名的二维组合：")
      rddCombinePackage1000.collect take (3) foreach println

      // 包名1与包名2同时被某用户安装的次数
      val rddPackage1___Package2___UserCount = rddCombinePackage1000.collect
        .map { case (o, o_) =>
            val user = rddUser___installPackages
              .filter { case (u, ps) => ps.exists(_ == o) && ps.exists(_ == o_) } //
              .map(_._1)
            (o, o_, user.count)
        }
        .filter(0 != _._3)
      println("包名1与包名2同时被某用户安装的次数：")
      rddPackage1___Package2___UserCount take (10) foreach println
      
      println("包名1与包名2的支持度：")
      rddPackage1___Package2___UserCount.map { case(_1, _2, _3) =>
        (_1, _2, (_3.toDouble / userCount))  
      }
      .filter(_._3 > 0.01)
      .take(3)
      .foreach(println)

    }
  }
}