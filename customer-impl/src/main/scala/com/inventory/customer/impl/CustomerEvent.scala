package com.inventory.customer.impl

import java.time.Instant

import com.inventory.customer.api._
import com.lightbend.lagom.scaladsl.persistence.AggregateEvent
import com.lightbend.lagom.scaladsl.persistence.AggregateEventTag

object CustomerEvent
{
  val Tag = AggregateEventTag[CustomerEvent]
}

sealed trait CustomerEvent extends AggregateEvent[CustomerEvent]
{
  override def aggregateTag = CustomerEvent.Tag

  val timestamp: Instant = Instant.now()
}

case class NewCustomerRegistered(customer: Customer) extends CustomerEvent
case class CustomerRelocated(customerAddress: CustomerAddress) extends CustomerEvent
case class CustomerNameChanged(customerName: CustomerName) extends CustomerEvent
case class CustomerAccountFrozen() extends CustomerEvent