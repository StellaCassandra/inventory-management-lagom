package com.inventory.purchase.impl

import akka.Done
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity.ReplyType
import play.api.libs.json.{Format, Json}
import com.inventory.purchase.api._

sealed trait PurchaseCommand[R] extends ReplyType[R]

// Commands
case class AddProducts(action: ProductAction) extends PurchaseCommand[Done]

case class RemoveProducts(action: ProductAction) extends PurchaseCommand[Done]

case class IssuePurchaseOrder() extends PurchaseCommand[Done]

case class CancelPurchaseOrder() extends PurchaseCommand[Done]

// Queries
case class RetrievePurchaseOrders() extends PurchaseCommand[RetrievePurchaseOrdersReply]

case class RetrievePurchaseOrdersReply(orders: Seq[PurchaseOrder])

object PurchaseCommand
{
  // activate JSON serialization
  implicit val addProducts: Format[AddProducts] = Json.format
  implicit val removeProducts: Format[RemoveProducts] = Json.format
}

object RetrievePurchaseOrdersReply
{
  implicit val format: Format[RetrievePurchaseOrdersReply] = Json.format
}