package com.inventory.product.impl

import akka.{Done, NotUsed}
import com.lightbend.lagom.scaladsl.persistence.PersistentEntityRegistry

import scala.concurrent.ExecutionContext
import com.inventory.product.api._
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.api.transport.NotFound

class ProductServiceImpl(persistentEntityRegistry: PersistentEntityRegistry)(implicit ec: ExecutionContext) extends ProductService
{
  override def registerNewProduct(): ServiceCall[Item, Done] =
    newProduct =>
      {
        val ref = productEntityRef(newProduct.productId)
        ref.ask(RegisterNewProduct(newProduct))
      }

  override def retrieveProduct(productId: String): ServiceCall[NotUsed, Item] =
    _ =>
    {
      val ref = productEntityRef(productId)
      ref.ask(RetrieveProduct()).map(_.product.getOrElse(throw NotFound(s"Product $productId not found.")))
    }

  private def productEntityRef(userId: String) =
    persistentEntityRegistry.refFor[ProductEntity](userId)
}
