object Lessons extends App {

  override def main(args: Array[String]): Unit = {
    val sugarBag: Bag[Sugar] = Bag(Sugar(1))
    val halfSugarBag: Bag[Sugar] = sugarBag.map(sugar => half(sugar))
    val quarterShearBag: Bag[Sugar] = halfSugarBag.map(sug => half(sug))
    halfSugarBag.content
    println(halfSugarBag.content.weight)
    println(quarterShearBag.content.weight)
    val bagOfApples: Bag[Apple] = Bag(Apple(7, "green"))
    val bagMinus1 = bagOfApples.map(apl => eatApple(apl))
    val bagPlusNewApple: Bag[Apple] = bagMinus1.map(apl => addApple(apl))
    val bugOfSkinnedApple: Bag[Apple] = bagPlusNewApple.map(apl => skinApple(apl))
    println(s"previous + ${bagOfApples.content.count} of ${bagOfApples.content.getClass.getSimpleName} now " +
      s" ${bagMinus1.content.count} of ${bagOfApples.content.getClass.getSimpleName} later " +
      s" ${bagPlusNewApple.content.count} of ${bagPlusNewApple.content.getClass.getSimpleName} with " +
      s"color ${bagOfApples.content.color} which has become the color of ${bugOfSkinnedApple.content.color}")
    val lst = List("aaa", "bbb", "ggg")
    lst.foreach(println(_))
  }

  //case class Gagulik() {
  //  def getLists: (List[Int], List[String]) = {
  //    val list = List(1, 2, 3, 4)
  //    val list2 = List("a", "b")
  //    (list, list2)
  //  }}

  case class Bag[A](content: A) {
    def map[B](f: A => B): Bag[B] = Bag(f(content))
  }

  case class Sugar(weight: Double)

  case class Apple(count: Int, color: String)

  def half: Sugar => Sugar = (sugar: Sugar) => Sugar(sugar.weight / 2)

  def eatApple: Apple => Apple = (apl: Apple) => Apple(apl.count - 1, apl.color)

  def addApple: Apple => Apple = (apl: Apple) => Apple(apl.count + 1, apl.color)

  def skinApple: Apple => Apple = (apl: Apple) => Apple(apl.count, "white")
}