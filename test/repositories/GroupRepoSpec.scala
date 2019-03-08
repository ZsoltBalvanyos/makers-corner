package repositories

import models.{Group, OrderItem}
import org.scalatest.FlatSpec
import play.api.inject.guice.GuiceApplicationBuilder
import util.CommonSpec

import scala.concurrent.ExecutionContext.Implicits.global

class GroupRepoSpec extends FlatSpec with CommonSpec {

  behavior of "GroupRepo"

  it should "insert, select and delete" in {
    val groupRepo = new GuiceApplicationBuilder().injector().instanceOf[GroupRepo]
    groupRepo.create("one", "pic1", 0).flatMap(a => groupRepo.create("two", "pic2", 1))
    await(groupRepo.getByIdTest(1)).name shouldBe "one"

//    await(groupRepo.list()).map(_.name) should contain("one")
//    await(groupRepo.list()).map(_.groupId) should contain(1)
//    await(groupRepo.getById(1)).map(_.groupId) should contain(1)

//    await(groupRepo.list()).map(_.name) should contain("two")

//    await(orderItemRepo.delete(oi.productId, oi.orderId)) shouldBe 1
//    await(orderItemRepo.list()).headOption shouldBe None
  }

}
