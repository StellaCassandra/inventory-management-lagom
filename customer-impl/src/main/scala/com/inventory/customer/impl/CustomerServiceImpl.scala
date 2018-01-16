package com.inventory.customer.impl

import akka.{Done, NotUsed}
import com.inventory.customer.api._
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.api.transport.NotFound
import com.lightbend.lagom.scaladsl.persistence.PersistentEntityRegistry

import scala.concurrent.{ExecutionContext, Future}

class CustomerServiceImpl(persistentEntityRegistry: PersistentEntityRegistry)(implicit ec: ExecutionContext) extends CustomerService
{
  override def registerNewCustomer(): ServiceCall[Customer, Done] =
    newCustomer =>
    {
      val ref = customerEntityRef(newCustomer.customerId)
      ref.ask(RegisterNewCustomer(newCustomer))
    }

  override def relocateCustomer(userId: String): ServiceCall[CustomerAddress, Done] =
    newAddress =>
    {
      val ref = customerEntityRef(userId)
      ref.ask(RelocateCustomer(newAddress))
    }

  override def nameChangeCustomer(userId: String): ServiceCall[CustomerName, Done] =
    newName =>
    {
      val ref = customerEntityRef(userId)
      ref.ask(NameChangeCustomer(newName))
    }

  override def freezeCustomerAccount(userId: String): ServiceCall[NotUsed, Done] =
    _ =>
    {
      val ref = customerEntityRef(userId)
      ref.ask(FreezeCustomerAccount())
    }

  override def retrieveCustomer(userId: String): ServiceCall[NotUsed, Customer] =
    _ =>
    {
      val ref = customerEntityRef(userId)
      ref.ask(RetrieveCustomer()).map(_.customer.getOrElse(throw NotFound(s"User $userId not found.")))
    }

  override def retrieveAccountStatus(userId: String): ServiceCall[NotUsed, CustomerAccountStatus] =
    _ =>
    {
      val ref = customerEntityRef(userId)
      ref.ask(RetrieveAccountStatus()).map(_.accountStatus.getOrElse(throw NotFound(s"User $userId not found.")))
    }

  private def customerEntityRef(userId: String) =
    persistentEntityRegistry.refFor[CustomerEntity](userId)
}
