object StatementExerciseAfter {

  import zio.{ Task, ZIO }

  case class Statement(accountId: Long,
                       lineItemCharges: List[(String, BigDecimal)],
                       balanceFee: BigDecimal,
                       totalCharge: BigDecimal)

  trait DataSource {
    def fetchPrice(item: String): Task[BigDecimal]
    def fetchUsage(item: String): Task[Int]
    def fetchBalance(accountId: Long): Task[BigDecimal]
  }

  final class StatementService(D: DataSource) {

    def makeStatement(accountId: Long)(bankingServices: Set[String]): Task[Statement] = {

      // calculate charge for each service (price * usage)
      // hint: use zipPar to execute datasource queries in parallel
      val items: Task[List[(String, BigDecimal)]] =
        ZIO.foreachPar(bankingServices) { item =>
          ???
        }

      // calculate balance fee
      val fee: Task[BigDecimal] = {
        val BALANCE_FEE = "BalanceFee"
        ???
      }

      //populate statement case class with various components
      //don't forget to calculate the total charge
      def mapToStatement(is: List[(String, BigDecimal)], f: BigDecimal): Statement = ???

      items zipPar fee map { case (is, f) => mapToStatement(is, f) }
    }
  }

}
