package com.inventory.product.impl

import com.lightbend.lagom.scaladsl.persistence.{AggregateEventTag, ReadSideProcessor}
import com.lightbend.lagom.scaladsl.persistence.cassandra.{CassandraReadSide, CassandraSession}

import scala.concurrent.ExecutionContext

class ProductQueryModelBuilder(session: CassandraSession, readSide: CassandraReadSide)(implicit ec: ExecutionContext)
  extends ReadSideProcessor[ProductEvent]
{
  override def buildHandler(): ReadSideProcessor.ReadSideHandler[ProductEvent] = ???

  override def aggregateTags: Set[AggregateEventTag[ProductEvent]] = ???
}
