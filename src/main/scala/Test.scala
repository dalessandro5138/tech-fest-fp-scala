

import scala.collection.mutable.{Map => MutableMap}
import scala.collection.{Map => ImmutableMap}

object Test extends App {

  def address(a: Any) = Integer.toHexString(System.identityHashCode(a))

  val x = MutableMap(1 -> 2)
  println(s"address for x: ${address(x)}")
  println(s"address for x: ${address(x.addOne(5 -> 6))}\n")


  val y = ImmutableMap(1 -> 2)
  println(s"address for y: ${address(y)}")
  println(s"address for y: ${address(y ++ Map(3 -> 4))}")

}

/*

-get volumes
-aggregate volumes
-get balances for client
-average balances for client
-get prices for client
-get rates for client
-calculate fee revenue
-calculate balance revenue
-create bill

*/

//start with something that's difficult to test and make it more testable
//start with

case class ServiceVolume(serviceCode: Int, volume: Int)
case class Balance(balance: BigDecimal)
case class LineItemCharge(serviceCode: Int, charge: BigDecimal)
case class Bill(lineItems: Set[LineItemCharge], balanceCharges: BigDecimal)

trait VolumeService {
  def fetchVolumes: List[ServiceVolume]
}

trait BalanceService {
  def fetchBalances: List[Balance]
}

trait PriceService {
  def fetchPrices: Map[Int, BigDecimal]
}

trait RateService {
  def fetchRate: BigDecimal
}

trait BillService {
  def aggregateVolumes(vols: List[ServiceVolume]): Set[ServiceVolume]
  def averageBalances(bals: List[Balance]): BigDecimal
  def calculateBill(): Bill
}

class BillServiceImpl(volSvc: VolumeService, balSvc: BalanceService, prcSvc: PriceService, rateSvc: RateService) extends BillService {

  override def aggregateVolumes(vols: List[ServiceVolume]): Set[ServiceVolume] = {
    return vols
      .groupBy(_.serviceCode)
      .mapValues(_.map(_.serviceCode).sum)
      .map { case (svc, vol) => ServiceVolume(svc, vol) }
      .toSet
  }

  override def averageBalances(bals: List[Balance]): BigDecimal = {
    return bals.map(_.balance).sum / bals.size
  }

  override def calculateBill(): Bill = {
    val vols = volSvc.fetchVolumes
    val aggVols = aggregateVolumes(vols)
    val bals = balSvc.fetchBalances
    val avgBal = averageBalances(bals)
    val prices = prcSvc.fetchPrices
    val rate = rateSvc.fetchRate
    val lineItems = aggVols.map{ sv => LineItemCharge(sv.serviceCode, prices(sv.serviceCode) * sv.volume) }
    val balCharge = avgBal * rate

    return Bill(lineItems, rate)
  }
}
