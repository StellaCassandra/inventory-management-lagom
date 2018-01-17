package com.inventory.purchase.impl

import scala.concurrent.Future
import akka.Done
import com.datastax.driver.core.BoundStatement
import com.datastax.driver.core.PreparedStatement
import com.lightbend.lagom.scaladsl.persistence.AggregateEventTag
import com.lightbend.lagom.scaladsl.persistence.EventStreamElement
import com.lightbend.lagom.scaladsl.persistence.ReadSideProcessor
import com.lightbend.lagom.scaladsl.persistence.cassandra.CassandraReadSide
import com.lightbend.lagom.scaladsl.persistence.cassandra.CassandraSession
import scala.concurrent.Promise
import scala.collection._

import scala.concurrent.ExecutionContext


class PurchaseQueryModelBuilder(session: CassandraSession, readSide: CassandraReadSide)(implicit ec: ExecutionContext)
  extends ReadSideProcessor[PurchaseEvent]
{
  // select  id, itemtotal from purchase_summary
  override def buildHandler(): ReadSideProcessor.ReadSideHandler[PurchaseEvent] =
  {
    val builder = readSide.builder[PurchaseEvent]("purchase_summary")
    builder.setGlobalPrepare(createTable)
    builder.setPrepare(tag => prepareWriteTitle())
    builder.setEventHandler[PurchaseOrderAccepted](processPurchaseOrderAccepted)
    builder.build()
  }

  private def createTable(): Future[Done] =
    session.executeCreateTable("CREATE TABLE IF NOT EXISTS purchase_summary ( " +
      "customer_id TEXT, order_id TEXT, itemtotal INT, PRIMARY KEY (customer_id,order_id))")

  private val writeTitlePromise = Promise[PreparedStatement]
  private def writeTitle: Future[PreparedStatement] = writeTitlePromise.future

  private def prepareWriteTitle(): Future[Done] =
  {
    val f = session.prepare("INSERT INTO purchase_summary (customer_id, order_id, itemtotal) VALUES (?, ?, ?)")
    writeTitlePromise.completeWith(f)
    f.map(_ => Done)
  }

  private def processPurchaseOrderAccepted(eventElement: EventStreamElement[PurchaseOrderAccepted]): Future[List[BoundStatement]] =
  {
    writeTitle.map
    { ps =>
      eventElement.event match
      {
        case PurchaseOrderAccepted(customerId, order) =>
          val bindWriteTitle = ps.bind()

          val total = order.quantity.foldLeft(0)(_ + _._2)
          bindWriteTitle.setString("customer_id", customerId)
          bindWriteTitle.setString("order_id", order.orderId)
          bindWriteTitle.setInt("itemtotal", total)
          List(bindWriteTitle)
        case _ =>
          List.empty
      }

    }
  }

  override def aggregateTags: Predef.Set[AggregateEventTag[PurchaseEvent]] = PurchaseEvent.Tag.allTags
}
