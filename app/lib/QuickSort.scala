package lib

object QuickSort {
  // IDEA FROM: http://www.scala-lang.org/docu/files/ScalaByExample.pdf
  def firstClass[T](list: List[T], f:(T,T) => Boolean, e:(T,T) => Boolean): List[T] = {
    def notf(x: T, y: T) = !f(x, y) && !e(x, y)
    if (list.length <= 1) list
    else {
        val pivot = list(list.length / 2)
        val before = list filter (each => f(pivot, each))
        val after = list filter (each => notf(pivot, each))
        firstClass(before, f, e) ::: (list filter (pivot ==)) ::: firstClass(after, f, e)
    }
  }

  def orderedTrait[T <% Ordered[T]](list: List[T]): List[T] = {
    if (list.length <= 1) list
    else {
        val pivot = list(list.length / 2)
        val before = list filter (each => pivot > each)
        val after = list filter (each => pivot < each)
        orderedTrait(before) ::: (list filter (pivot ==)) ::: orderedTrait(after)
    }
  }
}