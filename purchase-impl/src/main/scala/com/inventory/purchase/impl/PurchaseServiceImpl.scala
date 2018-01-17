package com.inventory.purchase.impl

import akka.stream.scaladsl.Source
import akka.{Done, NotUsed}
import com.inventory.customer.api.{CustomerAccountStatus, CustomerService}
import com.inventory.product.api.ProductService
import com.lightbend.lagom.scaladsl.persistence.{PersistentEntityRegistry, ReadSide}

import scala.concurrent._
import scala.concurrent.duration._
import com.inventory.purchase.api._
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.api.transport.{BadRequest, Forbidden, NotFound}
import com.lightbend.lagom.scaladsl.persistence.cassandra.{CassandraReadSide, CassandraSession}

class PurchaseServiceImpl(persistentEntityRegistry: PersistentEntityRegistry, customerService: CustomerService,
                          productService: ProductService, cassandraSession: CassandraSession)
                         (implicit ec: ExecutionContext) extends PurchaseService
{

  override def addProducts(customerId: String): ServiceCall[ProductAction, Done] =
    action =>
    {
      val ref = purchaseEntityRef(customerId)
      ref.ask(AddProducts(action))
    }

  override def removeProducts(customerId: String): ServiceCall[ProductAction, Done] =
    action =>
    {
      val ref = purchaseEntityRef(customerId)
      ref.ask(RemoveProducts(action))
    }

  override def issuePurchaseOrder(customerId: String): ServiceCall[NotUsed, Done] =
    _ =>
    {
      val account_status = customerService.retrieveAccountStatus(customerId).invoke()

      val isSolvent = Await.result(account_status, 10 seconds).isSolvent

      if (isSolvent)
      {
        val ref = purchaseEntityRef(customerId)
        ref.ask(IssuePurchaseOrder())
      }
      else
      {
        throw Forbidden(s"The account of the customer $customerId has been frozen. Contact customer support.")
      }
    }

  override def cancelPurchaseOrder(customerId: String): ServiceCall[NotUsed, Done] =
    _ =>
    {
      val ref = purchaseEntityRef(customerId)
      ref.ask(CancelPurchaseOrder())
    }

  override def retrievePurchaseOrders(customerId: String): ServiceCall[NotUsed, Seq[OrderSummary]] =
    _ =>
    {
      val response: Future[Seq[OrderSummary]] =
        cassandraSession.selectAll(s"SELECT order_id, itemtotal FROM purchase_summary WHERE customer_id = '$customerId'")
          .map
          { rows =>
            rows.map
            {
              row =>
                OrderSummary(row.getString("order_id"), row.getInt("itemtotal"))
            }
          }
      response
    }

  private def purchaseEntityRef(userId: String) =
    persistentEntityRegistry.refFor[PurchaseEntity](userId)
}
