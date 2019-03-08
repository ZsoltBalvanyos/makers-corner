package controllers

import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._
import com.google.inject.Inject
import models.Group
import play.api.mvc.{AbstractController, ControllerComponents}
import repositories.GroupRepo
import services.StoreService
import cats.data.Kleisli
import cats.effect.Async
import cats.instances.vector._
import cats.syntax.either._
import cats.syntax.flatMap._
import cats.syntax.functor._
import cats.syntax.show._
import cats.syntax.traverse._
import cats.syntax.option._
import cats.implicits._

import scala.collection.immutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}

class StoreController @Inject() (cc: ControllerComponents,
                                 storeService: StoreService,
                                 groupRepo: GroupRepo) extends AbstractController(cc) {

  groupRepo.list().map { _.size match {
    case 0 =>
      for {
        x1 <- groupRepo.create("one", "1", 0)
        x2 <- groupRepo.create("two", "2", 0)
        x3 <- groupRepo.create("three", "3", 0)
        _   = Thread.sleep(4000)
        x4 <- groupRepo.create("four", "4", x1.right.get.groupId)
        x5 <- groupRepo.create("five", "5", x1.right.get.groupId)
        x6 <- groupRepo.create("six", "6", x5.right.get.groupId)
        x7 <- groupRepo.create("seven", "7", x5.right.get.groupId)
        x8 <- groupRepo.create("eight", "8", x7.right.get.groupId)
        x9 <- groupRepo.create("nine", "9", x3.right.get.groupId)
      } yield s"Result: ${x1}, ${x2}, ${x3}, ${x4}, ${x5}, ${x6}, ${x7}, ${x8}, ${x9}"

      Thread.sleep(3000)

      groupRepo.list().map(_.map(println(_)))
  }}



  def getStoreRoot = Action { implicit request =>
    Ok(views.html.store())
  }

  import scala.concurrent.duration._
  def getChildren(groupId: String) = Action.async { implicit request =>
    println(s"Getting children of $groupId")
    val x = groupRepo
      .listChildren(groupId.toLong)
    println(s"Size: ${Await.result(x, 20 seconds).size}")
    x.map(groups => Ok(groups.asJson.noSpaces).as(JSON))
  }


  def addGroup(name: String, avatar: String, parentId: Long) = Action { implicit request =>
    groupRepo.create(name, avatar, parentId)
    Ok
  }

}
