package com.inventory.purchase.api

import akka.{Done, NotUsed}
import com.lightbend.lagom.scaladsl.api.broker.Topic
import com.lightbend.lagom.scaladsl.api.broker.kafka.{KafkaProperties, PartitionKeyStrategy}
import com.lightbend.lagom.scaladsl.api._
import play.api.libs.json.{Format, Json}

object PurchaseService
{
  val TOPIC_NAME = "purchases"
}

trait PurchaseService extends Service
{
  // Commands
  def addProducts(customerId: String): ServiceCall[ProductAction, Done]
  def removeProducts(customerId: String): ServiceCall[ProductAction, Done]
  def issuePurchaseOrder(customerId: String): ServiceCall[NotUsed, Done]
  def cancelPurchaseOrder(customerId: String): ServiceCall[NotUsed, Done]

  // Queries
  def retrievePurchaseOrders(customerId: String): ServiceCall[NotUsed, Seq[OrderSummary]]

  override def descriptor: Descriptor =
  {
    import Service._
    named("purchase-service").withCalls(
      pathCall("/api/purchases/:customerId", retrievePurchaseOrders _),
      pathCall("/api/purchases/:customerId/add-products", addProducts _),
      pathCall("/api/purchases/:customerId/remove-products", removeProducts _),
      pathCall("/api/purchases/:customerId/issue-purchase-order", issuePurchaseOrder _),
      pathCall("/api/purchases/:customerId/cancel-purchase-order", cancelPurchaseOrder _),
    ).withAutoAcl(true)
  }
}

