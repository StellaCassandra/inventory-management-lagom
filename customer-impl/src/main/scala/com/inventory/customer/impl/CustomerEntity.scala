package com.inventory.customer.impl

import java.time.LocalDateTime

import akka.Done
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity
import com.lightbend.lagom.scaladsl.playjson.{JsonSerializer, JsonSerializerRegistry}

import scala.collection.immutable.Seq
import com.inventory.customer.api._
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity.ReplyType

// Aggregate Root
class CustomerEntity extends PersistentEntity
{
  override type Command = CustomerCommand[_]
  override type Event = CustomerEvent
  override type State = CustomerState


  override def initialState: CustomerState = CustomerState(None)

  // Command handler
  override def behavior: Behavior =
  {
    case CustomerState(None, _) => Actions()
      .onCommand[CustomerCommand[Any], Any]
      {
        case (cmd, ctx, state) =>
          ctx.invalidCommand(s"Customer $entityId does not exist.")
          ctx.done
      }
      .onCommand[RegisterNewCustomer, Done]
      {
        case (RegisterNewCustomer(customer), ctx, state) =>
          ctx.thenPersist(NewCustomerRegistered(customer))
          {
            _ =>
              ctx.reply(Done)
              ctx.done
          }
      }
      .onReadOnlyCommand[RetrieveCustomer, RetrieveCustomerReply]
      {
        case (cmd, ctx, state) =>
          ctx.reply(RetrieveCustomerReply(None))
      }
      .onEvent(eventHandler)

    case CustomerState(Some(customer), _) => Actions()
      .onCommand[RegisterNewCustomer, Done]
      {
        case (cmd, ctx, state) =>
          ctx.invalidCommand(s"Customer $entityId does alrady exist.")
          ctx.done
      }
      .onCommand[RelocateCustomer, Done]
      {
        case (RelocateCustomer(customerAddress), ctx, state) =>
          ctx.thenPersist(CustomerRelocated(customerAddress))
          {
            _ =>
              ctx.reply(Done)
              ctx.done
          }
      }
      .onCommand[NameChangeCustomer, Done]
      {
        case (NameChangeCustomer(customerName), ctx, state) =>
          ctx.thenPersist(CustomerNameChanged(customerName))
          {
            _ =>
              ctx.reply(Done)
              ctx.done
          }
      }
      .onCommand[FreezeCustomerAccount, Done]
      {
        case (FreezeCustomerAccount(), ctx, state) =>
          ctx.thenPersist(CustomerAccountFrozen())
          {
            _ =>
              ctx.reply(Done)
              ctx.done
          }
      }
      .onReadOnlyCommand[RetrieveCustomer, RetrieveCustomerReply]
      {
        case (cmd, ctx, state) =>
          ctx.reply(RetrieveCustomerReply(Some(customer)))
      }
      .onEvent(eventHandler)
  }

  def eventHandler: EventHandler =
  {
    case (NewCustomerRegistered(customer), state) =>
      CustomerState(customer)

    case (CustomerRelocated(customerAddress), state) =>
      state.relocate(customerAddress)

    case (CustomerNameChanged(customerName), state) =>
      state.changeName(customerName)

    case (CustomerAccountFrozen(), state) =>
      state.copy(accountFrozen = true)
  }
}

case class CustomerState(customer: Option[Customer], accountFrozen: Boolean = false)
{
  def changeName(newName: CustomerName): CustomerState = customer match
  {
    case None => throw new IllegalStateException("Customer must exist for a name change to take effect.");
    case Some(customer) =>
      val newCustomer = customer.copy(customerName = newName)
      this.copy(customer = Some(newCustomer))
  }

  def relocate(newAddress: CustomerAddress): CustomerState = customer match
  {
    case None => throw new IllegalStateException("Customer must exist for a relocation to take effect.");
    case Some(customer) =>
      val newCustomer = customer.copy(customerAddress = newAddress)
      this.copy(customer = Some(newCustomer))
  }
}

object CustomerState
{
  def apply(customer: Customer): CustomerState = CustomerState(Some(customer))
}

/**
  * Akka serialization, used by both persistence and remoting, needs to have
  * serializers registered for every type serialized or deserialized.
  */
object CustomerSerializerRegistry extends JsonSerializerRegistry
{
  override val serializers: Seq[JsonSerializer[_]] = Seq(
    JsonSerializer[Customer],
    JsonSerializer[CustomerAddress],
    JsonSerializer[CustomerName],
    JsonSerializer[RegisterNewCustomer],
    JsonSerializer[RelocateCustomer],
    JsonSerializer[NameChangeCustomer],
    JsonSerializer[RetrieveCustomerReply]
  )
}