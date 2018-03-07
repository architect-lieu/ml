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

  implicit val _spark_ = spark
  val path = getClasspath + "datasets/user_install_list/000000_0"

  def main(args: Array[String]): Unit = {
    
    invokeSessionHandler { () =>
      val rdd = spark.read.textFile(path).rdd.map(_.split('\t'))
//      val df = rdd.toDF("date", "user_id", "package_name")
      
      println("Row Count: " + rdd.count)
      println("User Count: " + rdd.map(_(1)).distinct.count)
      println("Date Count: " + rdd.map(_(2)).distinct.count)
      
      
      

    }
  }
}