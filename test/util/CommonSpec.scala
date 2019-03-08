package util

import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, Matchers}
import org.scalatest.concurrent.{Eventually, ScalaFutures}
import org.scalatest.prop.PropertyChecks
import play.api.test.{DefaultAwaitTimeout, FutureAwaits}

trait CommonSpec extends Matchers with PropertyChecks with ScalaFutures with Eventually with FutureAwaits with DefaultAwaitTimeout

