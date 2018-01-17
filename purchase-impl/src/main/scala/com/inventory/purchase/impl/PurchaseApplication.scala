package com.inventory.purchase.impl

import com.inventory.customer.api.CustomerService
import com.inventory.product.api.ProductService
import com.inventory.purchase.api._
import com.lightbend.lagom.scaladsl.api.ServiceLocator
import com.lightbend.lagom.scaladsl.api.ServiceLocator.NoServiceLocator
import com.lightbend.lagom.scaladsl.broker.kafka.LagomKafkaComponents
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import com.lightbend.lagom.scaladsl.persistence.cassandra.CassandraPersistenceComponents
import com.lightbend.lagom.scaladsl.server.{LagomApplication, LagomApplicationContext, LagomApplicationLoader}
import com.softwaremill.macwire.wire
import play.api.libs.ws.ahc.AhcWSComponents

class PurchaseLoader extends LagomApplicationLoader {

  override def load(context: LagomApplicationContext): LagomApplication =
    new PurchaseApplication(context) {
      override def serviceLocator: ServiceLocator = NoServiceLocator
    }

  override def loadDevMode(context: LagomApplicationContext): LagomApplication =
    new PurchaseApplication(context) with LagomDevModeComponents

  override def describeService = Some(readDescriptor[PurchaseService])
}

abstract class PurchaseApplication(context: LagomApplicationContext)
  extends LagomApplication(context)
    with CassandraPersistenceComponents
    with LagomKafkaComponents
    with AhcWSComponents {

  // Bind the service that this server provides
  override lazy val lagomServer = serverFor[PurchaseService](wire[PurchaseServiceImpl])

  // Register the JSON serializer registry
  override lazy val jsonSerializerRegistry = PurchaseSerializerRegistry

  // Register the customer persistent entity
  persistentEntityRegistry.register(wire[PurchaseEntity])

  lazy val customerService = serviceClient.implement[CustomerService]
  lazy val productService = serviceClient.implement[ProductService]

  readSide.register[PurchaseEvent](new PurchaseQueryModelBuilder(cassandraSession, cassandraReadSide))
}