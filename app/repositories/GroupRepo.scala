package repositories

import com.google.inject.Inject
import models.Group
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import cats.implicits._

import scala.collection.immutable
import scala.concurrent.{ExecutionContext, Future}

class GroupRepo @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext)  {

  private val dbConfig = dbConfigProvider.get[JdbcProfile]
  import dbConfig._
  import profile.api._

//  private implicit val songStatusCodec = dbConfig.profile.MappedColumnType.base[List[String], String](
//    _.mkString(","),
//    _.split(",").toList
//  )

  private class GroupTable(tag: Tag) extends Table[Group](tag, "group") {
    def groupId   = column[Long]("group_id", O.PrimaryKey, O.AutoInc)
    def name      = column[String]("name")
    def avatar    = column[String]("avatar")
    def parentId  = column[Long]("parent_id")
    def path  = column[String]("path")
    override def * = (groupId, name, avatar, parentId, path) <> ((Group.apply _).tupled, Group.unapply)
  }

  private val groups = TableQuery[GroupTable]
  db.run(DBIO.seq(groups.schema.create))

  def create(name: String, avatar: String, parentId: Long) = getPath(parentId).flatMap(getInsertStatement(name, avatar, parentId, _))

  def list(): Future[Seq[Group]] = db.run(groups.result)

  def listChildren(parentId: Long): Future[Seq[Group]] = db.run(groups.filter(_.parentId === parentId).result)

  def delete(groupId: Long) = db.run(groups.filter(_.groupId === groupId).delete)

  def getById(groupId: Long): Future[Option[Group]] = {
    println("IN GET BY ID")
    list().map(_.map(println(_)))
    println(s"Get: $groupId")
    val all = db.run(groups.result)
    val x: Future[Seq[Group]] = db.run(groups.filter(_.groupId === groupId).result)

    val result: Future[Option[Group]] = x.map(d => d.headOption)
    result
//    db.run(groups.filter(_.groupId === groupId).result).map(d => d.headOption)
  }

  def getByIdTest(groupId: Long): Future[Group] = {
    println("IN GET BY ID")
    list().map(_.map(println(_)))
    println(s"Get: $groupId")
    val all = db.run(groups.result)
    val x: Future[Seq[Group]] = db.run(groups.filter(_.groupId === groupId).result)

    val result: Future[Group] = x.map(_.toList).map(_(0))
    result
  }

  private def getPath(parentId: Long, result: List[String] = List[String]()): Future[Either[String, List[String]]] = {
    println(s"parent in getPath: ${parentId}")

    if (parentId == 0) return Future(Right(List()))

    println(s"parent in getPath: ${parentId}")

    getById(parentId).map((p: Option[Group]) => s"Result of getParent: $p")

    println(s"got parent successfully")

    getById(parentId)
      .flatMap {
        _.toRight(s"Parent ${parentId} does not exists") match {
          case Right(parent) =>
            if (parent.parentId == 0)
              return Future(Right(result))
            else {


              getPath(parent.parentId, parent.name :: result)
            }
          case Left(error) => Future(Left(error))
        }
      }
  }

  private def getInsertStatement(name: String, avatar: String, parentId: Long, path: Either[String, List[String]]) = {
    path.map{ p =>
      db.run {
        (groups.map(g => (g.name, g.avatar, g.parentId, g.path))
          returning groups.map(_.groupId)
          into ((data, id) => Group(id, data._1, data._2, data._3, data._4))
          ) += (name, avatar, parentId, p.mkString(","))
      }
    }.fold(e => Future(Left(e)), g => g.map(Right(_)))
  }
}
