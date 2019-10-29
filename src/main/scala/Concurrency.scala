object Concurrency {

  def time[R, E, A](program: ZIO[R, E, A]) =
    for {
      _     <- console.putStrLn("start")
      start <- ZIO(System.currentTimeMillis())
      _     <- program
      end   <- ZIO(System.currentTimeMillis())
      _     <- console.putStrLn("stop")
      _     <- console.putStrLn(s"stopped: ran in ${(end - start) / 1000} seconds")
    } yield ()

  val sleepFor7                         = ZIO.sleep(7 seconds)
  val sleepFor1                         = ZIO.sleep(1 second)
  val sleepFor3                         = ZIO.sleep(3 seconds)
  val execPar: RIO[Clock, (Unit, Unit)] = sleepFor1 zipPar sleepFor7
  new DefaultRuntime {}.unsafeRun(time(execPar))

  val things      = List(1, 2, 3, 4, 5, 6, 7)
  val execManyPar = ZIO.foreachPar(things)(p => sleepFor3)
  new DefaultRuntime {}.unsafeRun(time(execManyPar))

}
