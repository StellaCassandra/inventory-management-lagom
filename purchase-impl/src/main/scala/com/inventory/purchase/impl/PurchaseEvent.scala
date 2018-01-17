package com.inventory.purchase.impl

import java.time.Instant

import com.lightbend.lagom.scaladsl.persistence.{AggregateEvent, AggregateEventShards, AggregateEventTag}
import com.inventory.purchase.api._
import play.api.libs.json.{Format, Json}

object PurchaseEvent
{
  val NumShards = 3
  val Tag = AggregateEventTag.sharded[PurchaseEvent](NumShards)

  // activate JSON serialization
  implicit val format: Format[PurchaseOrderAccepted] = Json.format
}

sealed trait PurchaseEvent extends AggregateEvent[PurchaseEvent]
{
  override def aggregateTag: AggregateEventShards[PurchaseEvent] = PurchaseEvent.Tag

  val timestamp: Instant = Instant.now()
}

case class ProductsAdded(action: ProductAction) extends PurchaseEvent
case class ProductsRemoved(action: ProductAction) extends PurchaseEvent
case class PurchaseOrderAccepted(customerId : String, order: PurchaseOrder) extends PurchaseEvent
case class PurchaseOrderCancelled() extends PurchaseEvent