package com.inventory.customer.api

import akka.{Done, NotUsed}
import com.lightbend.lagom.scaladsl.api.broker.Topic
import com.lightbend.lagom.scaladsl.api.broker.kafka.{KafkaProperties, PartitionKeyStrategy}
import com.lightbend.lagom.scaladsl.api._
import play.api.libs.json.{Format, Json}

object CustomerService
{
  val TOPIC_NAME = "customers"
}

trait CustomerService extends Service
{
  // Commands
  def registerNewCustomer(): ServiceCall[Customer, Done]

  def relocateCustomer(userId: String): ServiceCall[CustomerAddress, Done]

  def nameChangeCustomer(userId: String): ServiceCall[CustomerName, Done]

  def freezeCustomerAccount(userId: String): ServiceCall[NotUsed, Done]

  // Queries
  def retrieveCustomer(userId: String): ServiceCall[NotUsed, Customer]

  override def descriptor: Descriptor =
  {
    import Service._
    named("customer-service").withCalls(
      pathCall("/api/customers/:userId", retrieveCustomer _),
      pathCall("/api/customers", registerNewCustomer _),
      pathCall("/api/customers/:userId/relocate", relocateCustomer _),
      pathCall("/api/customers/:userId/name-change", nameChangeCustomer _),
      pathCall("/api/customers/:userId/freeze-account", freezeCustomerAccount _)
    ).withAutoAcl(true)
  }
}
