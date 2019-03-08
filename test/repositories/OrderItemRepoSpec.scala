package repositories

import models.OrderItem
import org.scalatest.FlatSpec
import play.api.inject.guice.GuiceApplicationBuilder
import util.CommonSpec
import org.scalacheck.ScalacheckShapeless._

class OrderItemRepoSpec extends FlatSpec with CommonSpec {

  behavior of "OrderItemRepo"

  it should "insert, select and delete" in forAll { oi: OrderItem =>
    val orderItemRepo = new GuiceApplicationBuilder().injector().instanceOf[OrderItemRepo]
    orderItemRepo.create(oi.productId, oi.quantity, oi.orderId)
    await(orderItemRepo.list()).head shouldBe oi
    await(orderItemRepo.delete(oi.productId, oi.orderId)) shouldBe 1
    await(orderItemRepo.list()).headOption shouldBe None
  }

  it should "retrieve entries belong to the same order" in  {
    val orderItemRepo = new GuiceApplicationBuilder().injector().instanceOf[OrderItemRepo]

    orderItemRepo.create(1, 1, 1)
    orderItemRepo.create(2, 2, 2)
    orderItemRepo.create(3, 3, 1)
    orderItemRepo.create(4, 4, 2)
    orderItemRepo.create(5, 5, 1)

    val result = await(orderItemRepo.getOrder(1))
    result should contain(OrderItem(1, 1, 1))
    result should contain(OrderItem(3, 3, 1))
    result should contain(OrderItem(5, 5, 1))
    result shouldNot contain(OrderItem(2, 2, 2))
    result shouldNot contain(OrderItem(4, 4, 2))
  }

}
