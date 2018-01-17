package com.inventory.product.impl

import java.time.Instant

import com.inventory.product.api._
import com.lightbend.lagom.scaladsl.persistence.AggregateEvent
import com.lightbend.lagom.scaladsl.persistence.AggregateEventTag

object ProductEvent
{
  val Tag = AggregateEventTag[ProductEvent]
}

sealed trait ProductEvent extends AggregateEvent[ProductEvent]
{
  override def aggregateTag = ProductEvent.Tag

  val timestamp: Instant = Instant.now()
}

case class NewProductRegistered(product: Item) extends ProductEvent