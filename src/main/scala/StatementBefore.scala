object StatementBefore {

  import scala.concurrent.Future
  import scala.concurrent.ExecutionContext.Implicits.global
  import scala.concurrent.Await
  import scala.concurrent.duration._
  import scala.collection.mutable.MutableList

  trait DataSource {
    def fetchPrice(item: String): Future[BigDecimal]
    def fetchUsage(item: String): Future[Int]
    def fetchBalance(accountId: Long): Future[BigDecimal]
  }

  class Statement(var D: DataSource) {

    private var accountId: Long                                    = _
    private var lineItemCharges: MutableList[(String, BigDecimal)] = null
    private var balanceFee: BigDecimal                             = null
    private var totalCharge: BigDecimal                            = null

    private val BALANCE_FEE = "BalanceFee"

    def init(acctId: Long)(bankingServices: Set[String]): Unit = {

      accountId = acctId
      lineItemCharges = MutableList.empty
      balanceFee = 0
      totalCharge = 0

      for (item <- bankingServices) {
        var fPrice = D.fetchPrice(item)
        var fUsage = D.fetchUsage(item)

        var price = Await.result(fPrice, 1 second)
        var usage = Await.result(fUsage, 1 second)

        var charge = price * usage

        lineItemCharges += ((item, charge))
        totalCharge += charge
      }

      var fBal      = D.fetchBalance(acctId)
      var fBalPrice = D.fetchPrice(BALANCE_FEE)

      var bal      = Await.result(fBal, 1 second)
      var balPrice = Await.result(fBalPrice, 1 second)

      balanceFee = bal * balPrice
    }

    def setAccountId(id: Long): Unit = accountId = id
    def getAccountId: Long           = accountId

    def setLineItemCharges(lics: MutableList[(String, BigDecimal)]): Unit = lineItemCharges = lics
    def getLineItemCharges: MutableList[(String, BigDecimal)]             = lineItemCharges

    def setBalanceFee(fee: BigDecimal): Unit = balanceFee = fee
    def getBalanceFee: BigDecimal            = balanceFee

    def setTotalCharge(charge: BigDecimal): Unit = totalCharge = charge
    def getTotalCharge: BigDecimal               = totalCharge
  }
}
