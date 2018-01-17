package com.inventory.product.api

import akka.{Done, NotUsed}
import com.lightbend.lagom.scaladsl.api.broker.Topic
import com.lightbend.lagom.scaladsl.api.broker.kafka.{KafkaProperties, PartitionKeyStrategy}
import com.lightbend.lagom.scaladsl.api._
import play.api.libs.json.{Format, Json}

object ProductService
{
  val TOPIC_NAME = "products"
}

trait ProductService extends Service
{
  // Commands
  def registerNewProduct(): ServiceCall[Item, Done]

  // Queries
  def retrieveProduct(productId: String): ServiceCall[NotUsed, Item]

  override def descriptor: Descriptor =
  {
    import Service._
    named("product-service").withCalls(
      pathCall("/api/products", registerNewProduct _),
      pathCall("/api/products/:productId", retrieveProduct _)
    ).withAutoAcl(true)
  }
}

