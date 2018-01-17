package com.inventory.product.impl

import akka.Done
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity.ReplyType
import com.inventory.product.api._
import play.api.libs.json.{Format, Json}

sealed trait ProductCommand[R] extends ReplyType[R]

// Commands
case class RegisterNewProduct(product: Item) extends ProductCommand[Done]

// Queries
case class RetrieveProduct() extends ProductCommand[RetrieveProductReply]

case class RetrieveProductReply(product: Option[Item])

object ProductCommand
{
  // activate JSON serialization
  implicit val registerNewProduct: Format[RegisterNewProduct] = Json.format
}

object RetrieveProductReply
{
  implicit val format: Format[RetrieveProductReply] = Json.format
}