package models

case class Product(productId: Long,
                   name: String,
                   image: String,
                   price: Double,
                   features: List[String],
                   quantity: Int,
                   parent: String,
                   tags: List[String])

