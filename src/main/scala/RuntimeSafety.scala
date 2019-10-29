object RuntimeSafetyExercise {
  
  //dynamically
  def averageDyn(num: BigDecimal, den: BigDecimal) =
    if (den == 0) None else num / den

  //typed - option
  def averageOpt(num: BigDecimal, den: BigDecimal): Option[BigDecimal] =
    if (den == 0) None else Some(num / den)

  // using refined types
  class NonZero private (a: BigDecimal) {
    def value: BigDecimal = a
  }

  object NonZero {
    def from(a: BigDecimal): Either[String, NonZero] =
      if (a == 0) Left("value is zero") else Right(new NonZero(a))
  }

  def averageRef(num: BigDecimal, den: NonZero): BigDecimal =
    num / den.value

  // IO Operations
  class Task[A](a: => A) { self =>
    def run(): A = self.a
  }

  val myProgram = new Task(println("hello"))

  myProgram.run()
}
