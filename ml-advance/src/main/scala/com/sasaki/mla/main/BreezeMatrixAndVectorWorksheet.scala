package com.sasaki.mla.main

import breeze.linalg._
import breeze.numerics._

/**
 * @Author Sasaki
 * @Mail redskirt@outlook.com
 * @Timestamp Mar 8, 2018 7:12:20 PM
 * @Description
 */
object BreezeMatrixAnVectorWorksheet extends App {

  // >================================== Breeze 矩阵创建 ==================================

  val m1 = DenseMatrix.zeros[Double](2, 3)
  // 	m1: breeze.linalg.DenseMatrix[Double] =
  // 0.0  0.0  0.0
  // 0.0  0.0  0.0

  val v1 = DenseVector.zeros[Double](3)
  // v1: breeze.linalg.DenseVector[Double] = DenseVector(0.0, 0.0, 0.0)

  val v2 = DenseVector.ones[Double](3)
  // v2: breeze.linalg.DenseVector[Double] = DenseVector(0.0, 0.0, 0.0)

  val v3 = DenseVector.fill(3) { 5.0 }
  // v3: breeze.linalg.DenseVector[Double] = DenseVector(5.0, 5.0, 5.0)

  val v4 = DenseVector.range(1, 10, 2)
  // v4: breeze.linalg.DenseVector[Int] = DenseVector(1, 3, 5, 7, 9)

  val m2 = DenseMatrix.eye[Double](3)
  // m2: breeze.linalg.DenseMatrix[Double] = 1.0 0.0 0.0
  // 0.0 1.0 0.0
  // 0.0 0.0 1.0

  val v6 = diag(DenseVector(1.0, 2.0, 3.0))
  // v6: breeze.linalg.DenseMatrix[Double] =
  // 1.0 0.0 0.0
  // 0.0 2.0 0.0
  // 0.0 0.0 3.0

  val v8 = DenseVector(1, 2, 3, 4)
  // v8: breeze.linalg.DenseVector[Int] = DenseVector(1, 2, 3, 4)

  val v9 = DenseVector(1, 2, 3, 4).t
  // v9: breeze.linalg.Transpose[breeze.linalg.DenseVector[Int]] = Transpose(DenseVector(1, 2, 3, 4))

  val v10 = DenseVector.tabulate(3) { i => 2 * i }
  // v10: breeze.linalg.DenseVector[Int] = DenseVector(0, 2, 4)

  val m4 = DenseMatrix.tabulate(3, 2) { case (i, j) => i + j }
  // m4: breeze.linalg.DenseMatrix[Int] =
  // 01
  // 12
  // 23

  val v11 = new DenseVector(Array(1, 2, 3, 4))
  // v11: breeze.linalg.DenseVector[Int] = DenseVector(1, 2, 3, 4)

  val m5 = new DenseMatrix(2, 3, Array(11, 12, 13, 21, 22, 23))
  // m5: breeze.linalg.DenseMatrix[Int] =
  // 11 13 22
  // 12 21 23
  val v12 = DenseVector.rand(4)
  // v12: breeze.linalg.DenseVector[Double] = DenseVector(0.7517657487447951, 0.8171495400874123, 0.8923542318540489, 0.174311259949119)
  val m6 = DenseMatrix.rand(2, 3)
  // m6: breeze.linalg.DenseMatrix[Double] =
  // 0.5349430131148125 0.8822136832272578 0.7946323804433382 0.41097756311601086 0.3181490074596882 0.34195102205697414

  // >================================== Breeze 元素访问 ==================================

  val a = DenseVector(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
  // a: breeze.linalg.DenseVector[Int] = DenseVector(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
  a(0)
  // res2: Int = 1
  a(1 to 4)
  // res4: breeze.linalg.DenseVector[Int] = DenseVector(2, 3, 4, 5)
  a(5 to 0 by -1)
  // res5: breeze.linalg.DenseVector[Int] = DenseVector(6, 5, 4, 3, 2, 1)
  a(1 to -1)
  // res6: breeze.linalg.DenseVector[Int] = DenseVector(2, 3, 4, 5, 6, 7, 8, 9, 10)
  a(-1)
  // res7: Int = 10

  val m = DenseMatrix((1.0, 2.0, 3.0), (3.0, 4.0, 5.0))
  // m: breeze.linalg.DenseMatrix[Double] =
  // 1.0 2.0 3.0
  // 3.0 4.0 5.0
  m(0, 1)
  // res8: Double = 2.0
  m(::, 1)
  // res9: breeze.linalg.DenseVector[Double] = DenseVector(2.0, 4.0)
  m.reshape(3, 2)
  //res11: breeze.linalg.DenseMatrix[Double] = 1.0 4.0
  // 3.0 3.0
  // 2.0 5.0
  m.toDenseVector
  // res12: breeze.linalg.DenseVector[Double] = DenseVector(1.0, 3.0, 2.0, 4.0, 3.0, 5.0)

  val m3 = DenseMatrix((1.0, 2.0, 3.0), (4.0, 5.0, 6.0), (7.0, 8.0, 9.0))
  // m3: breeze.linalg.DenseMatrix[Double] =
  // 1.0 2.0 3.0
  // 4.0 5.0 6.0
  // 7.0 8.0 9.0

  lowerTriangular(m)
  // res19: breeze.linalg.DenseMatrix[Double] =
  // 1.0 0.0 0.0
  // 4.0 5.0 0.0
  // 7.0 8.0 9.0

  upperTriangular(m)
  // res20: breeze.linalg.DenseMatrix[Double] =
  // 1.0 2.0 3.0
  // 0.0 5.0 6.0
  // 0.0 0.0 9.0
}