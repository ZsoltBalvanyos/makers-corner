package repositories

import com.google.inject.Inject
import models.OrderItem
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

class OrderItemRepo @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext)  {

  private val dbConfig = dbConfigProvider.get[JdbcProfile]
  import dbConfig._
  import profile.api._

  private class OrderItemTable(tag: Tag) extends Table[OrderItem](tag, "order_item") {
    def productId = column[Long]("product_id")
    def quantity = column[Int]("quantity")
    def orderId  = column[Long]("order_id")
    override def * = (productId, quantity, orderId) <> ((OrderItem.apply _).tupled, OrderItem.unapply)
    def pk = primaryKey("primary_key", (productId, orderId))
  }

  private val orderItems = TableQuery[OrderItemTable]
  db.run(DBIO.seq(orderItems.schema.create))

  def create(productId: Long, quantity: Int, orderId: Long) = db.run {
    orderItems.insertOrUpdate(OrderItem(productId, quantity, orderId))
  }

  def list(): Future[Seq[OrderItem]] = db.run(orderItems.result)

  def delete(productId: Long, orderId: Long) = db.run(
    orderItems
      .filter(_.productId === productId)
      .filter(_.orderId === orderId)
      .delete
  )

  def getOrder(orderId: Long): Future[Seq[OrderItem]] = db.run(
    orderItems
      .filter(_.orderId === orderId)
      .result
  )
}
