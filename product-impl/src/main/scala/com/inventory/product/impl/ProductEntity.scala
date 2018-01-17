package com.inventory.product.impl

import akka.Done
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity
import com.inventory.product.api._
import com.lightbend.lagom.scaladsl.playjson.{JsonSerializer, JsonSerializerRegistry}

import scala.collection.immutable.Seq

class ProductEntity extends PersistentEntity
{
  override type Command = ProductCommand[_]
  override type Event = ProductEvent
  override type State = ProductState

  override def initialState: ProductState = ProductState(None)

  // Command and query handler
  override def behavior: Behavior =
  {
    Actions()
      .onCommand[RegisterNewProduct, Done]
      {
        case (RegisterNewProduct(product), ctx, ProductState(None)) =>
          ctx.thenPersist(NewProductRegistered(product))
          {
            _ =>
              ctx.reply(Done)
              ctx.done
          }
        case (_, ctx, ProductState(Some(product))) =>
          ctx.invalidCommand(s"Product $entityId does alrady exist.")
          ctx.done
      }
      .onReadOnlyCommand[RetrieveProduct, RetrieveProductReply]
      {
        case (cmd, ctx, state) =>
          ctx.reply(RetrieveProductReply(state.product))
      }
      .onEvent(eventHandler)
  }

  def eventHandler: EventHandler =
  {
    case (NewProductRegistered(product), state) =>
      ProductState(product)
  }
}

case class ProductState(product: Option[Item])

object ProductState
{
  def apply(product: Item): ProductState = ProductState(Some(product))
}

/**
  * Akka serialization, used by both persistence and remoting, needs to have
  * serializers registered for every type serialized or deserialized.
  */
object ProductSerializerRegistry extends JsonSerializerRegistry
{
  override val serializers: Seq[JsonSerializer[_]] = Seq(
    JsonSerializer[Item],
    JsonSerializer[RegisterNewProduct],
    JsonSerializer[RetrieveProductReply]
  )
}