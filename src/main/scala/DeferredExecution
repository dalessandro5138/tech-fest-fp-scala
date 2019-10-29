// IO Operations
case class Task[A](run: () => A)

object Task {
  def defer[A](a: => A) = Task(() => a)
}

val myProgram = Task.defer(println("hello"))

myProgram.run()
