package lib

case class Similarity(percentage: Float, name: String, gender: String, picture: String, time: String) extends Ordered[Similarity]{
  override def compare(that: Similarity): Int =
    (that.percentage) match {
      case (percentage) if (percentage > this.percentage) => 1
      case (percentage) if (percentage == this.percentage) => 0
      case _ => -1
  }
}