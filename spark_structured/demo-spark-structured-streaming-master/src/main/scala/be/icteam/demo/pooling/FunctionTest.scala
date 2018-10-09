package be.icteam.demo.pooling

object FunctionTest {

  def main(args: Array[String]): Unit = {
    val array=Array(1,2,4,8,9,18)
    val afterArray=for (i<-array) yield  i*3
    afterArray.foreach(x=>println(x))

  }
}
