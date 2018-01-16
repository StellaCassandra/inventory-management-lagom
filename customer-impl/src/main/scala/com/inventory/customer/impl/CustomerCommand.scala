package com.inventory.customer.impl

import akka.Done
import com.inventory.customer.api._
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity.ReplyType
import play.api.libs.json.{Format, Json}

sealed trait CustomerCommand[R] extends ReplyType[R]

// Commands
case class RegisterNewCustomer(customer: Customer) extends CustomerCommand[Done]
case class RelocateCustomer(customerAddress: CustomerAddress) extends CustomerCommand[Done]
case class NameChangeCustomer(customerName: CustomerName) extends CustomerCommand[Done]
case class FreezeCustomerAccount() extends CustomerCommand[Done]

// Queries
case class RetrieveCustomer() extends CustomerCommand[RetrieveCustomerReply]
case class RetrieveCustomerReply(customer: Option[Customer])

object CustomerCommand {
  // activate JSON serialization
  implicit val registerNewCustomer: Format[RegisterNewCustomer] = Json.format[RegisterNewCustomer]
  implicit val relocateCustomer: Format[RelocateCustomer] = Json.format[RelocateCustomer]
  implicit val nameChangeCustomer: Format[NameChangeCustomer] = Json.format[NameChangeCustomer]

}

object RetrieveCustomerReply {
  // activate JSON serialization
  implicit val format: Format[RetrieveCustomerReply] = Json.format[RetrieveCustomerReply]
}