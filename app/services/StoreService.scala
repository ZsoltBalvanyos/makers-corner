package services

import com.google.inject.Inject
import models._
import repositories.GroupRepo

class StoreService @Inject() () {

  def getGroupsOfParent(parentId: Long): Seq[Group] = {
    ???
  }

  def getProductsOfParent(parentId: Long): Seq[Product] = {
    ???
  }



}
