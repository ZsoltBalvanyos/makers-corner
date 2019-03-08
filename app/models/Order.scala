package models

import java.time.LocalDateTime

case class Order(orderId: Long, userId: Long, timeStamp: LocalDateTime)
