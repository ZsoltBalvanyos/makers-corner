package models

case class Group(groupId: Long, name: String, avatar: String, parentId: Long, path: String)
//
//object Group {
//  def apply(groupId: Long, name: String, avatar: String, parentId: Long, path: String): Group = new Group(groupId, name, avatar, parentId, path)
//  def apply(groupId: Long, name: String, avatar: String, parentId: Long, path: List[String]): Group = new Group(groupId, name, avatar, parentId, path.mkString(","))
//}