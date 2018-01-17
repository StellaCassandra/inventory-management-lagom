package com.inventory.purchase.impl

import akka.Done
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity
import com.lightbend.lagom.scaladsl.playjson.{JsonSerializer, JsonSerializerRegistry}
import com.inventory.purchase.api._

import scalaz.Scalaz._
import scala.collection.immutable.Seq

class PurchaseEntity extends PersistentEntity
{
  override type Command = PurchaseCommand[_]
  override type Event = PurchaseEvent
  override type State = PurchaseState

  override def initialState: PurchaseState = PurchaseState(PurchaseOrder(orderId = "0"))

  // Command and query handler
  override def behavior: Behavior = Actions()
    .onCommand[AddProducts, Done]
    {
      case (AddProducts(action), ctx, state) =>
        if (action.isInvalid)
        {
          ctx.invalidCommand(s"AddProducts with negative quantity.")
          ctx.done
        }
        else ctx.thenPersist(ProductsAdded(action))
        {
          _ =>
            ctx.reply(Done)
            ctx.done
        }
    }
    .onCommand[RemoveProducts, Done]
    {
      case (RemoveProducts(action), ctx, state) =>
        if (action.isInvalid)
        {
          ctx.invalidCommand(s"RemoveProducts with negative quantity.")
          ctx.done

        }
        else ctx.thenPersist(ProductsRemoved(action))
        {
          _ =>
            ctx.reply(Done)
            ctx.done
        }
    }
    .onCommand[IssuePurchaseOrder, Done]
    {
      case (cmd, ctx, state) =>
        if (state.purchaseOrder.quantity.isEmpty)
        {
          ctx.invalidCommand(s"Purchase Order contains no items.")
          ctx.done
        }
        else ctx.thenPersist(PurchaseOrderAccepted(entityId, state.purchaseOrder))
        {
          _ =>
            ctx.reply(Done)
            ctx.done
        }
    }
    .onCommand[CancelPurchaseOrder, Done]
    {
      case (cmd, ctx, state) =>
        ctx.thenPersist(PurchaseOrderCancelled())
        {
          _ =>
            ctx.reply(Done)
            ctx.done
        }
    }
    .onEvent(eventHandler)

  def eventHandler: EventHandler =
  {
    case (ProductsAdded(action), state) =>
      val new_quantity = action.quantity |+| state.purchaseOrder.quantity
      val newOrder = state.purchaseOrder.copy(quantity = new_quantity)
      PurchaseState(newOrder)

    case (ProductsRemoved(action), state) =>
      val removalQuantity = action.quantity mapValues ((q: Int) => -q)
      //@formatter:off
      val new_quantity = (removalQuantity |+| state.purchaseOrder.quantity)
        .filter{ case (k, v) => v < 0 }
      //@formatter:on
      val newOrder = state.purchaseOrder.copy(quantity = new_quantity)
      PurchaseState(newOrder)

    case (PurchaseOrderAccepted(_,_), state) =>
      val orderId = state.purchaseOrder.orderId.toInt + 1
      PurchaseState(PurchaseOrder(orderId.toString))

    case (PurchaseOrderCancelled(), state) =>
      val newOrder = state.purchaseOrder.copy(quantity = Map.empty)
      PurchaseState(newOrder)
  }
}

case class PurchaseState(purchaseOrder: PurchaseOrder)

/**
  * Akka serialization, used by both persistence and remoting, needs to have
  * serializers registered for every type serialized or deserialized.
  */
object PurchaseSerializerRegistry extends JsonSerializerRegistry
{
  override val serializers: Seq[JsonSerializer[_]] = Seq(
    JsonSerializer[ProductAction],
    JsonSerializer[AddProducts],
    JsonSerializer[RemoveProducts],
    JsonSerializer[PurchaseOrder],
    JsonSerializer[RetrievePurchaseOrdersReply],
    JsonSerializer[PurchaseOrderAccepted]
  )
}