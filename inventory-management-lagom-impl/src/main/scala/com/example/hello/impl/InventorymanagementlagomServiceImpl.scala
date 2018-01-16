package com.example.hello.impl

import com.example.hello.api
import com.example.hello.api.{InventorymanagementlagomService}
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.api.broker.Topic
import com.lightbend.lagom.scaladsl.broker.TopicProducer
import com.lightbend.lagom.scaladsl.persistence.{EventStreamElement, PersistentEntityRegistry}

/**
  * Implementation of the InventorymanagementlagomService.
  */
class InventorymanagementlagomServiceImpl(persistentEntityRegistry: PersistentEntityRegistry) extends InventorymanagementlagomService {

  override def hello(id: String) = ServiceCall { _ =>
    // Look up the inventory-management-lagom entity for the given ID.
    val ref = persistentEntityRegistry.refFor[InventorymanagementlagomEntity](id)

    // Ask the entity the Hello command.
    ref.ask(Hello(id))
  }

  override def useGreeting(id: String) = ServiceCall { request =>
    // Look up the inventory-management-lagom entity for the given ID.
    val ref = persistentEntityRegistry.refFor[InventorymanagementlagomEntity](id)

    // Tell the entity to use the greeting message specified.
    ref.ask(UseGreetingMessage(request.message))
  }


  override def greetingsTopic(): Topic[api.GreetingMessageChanged] =
    TopicProducer.singleStreamWithOffset {
      fromOffset =>
        persistentEntityRegistry.eventStream(InventorymanagementlagomEvent.Tag, fromOffset)
          .map(ev => (convertEvent(ev), ev.offset))
    }

  private def convertEvent(helloEvent: EventStreamElement[InventorymanagementlagomEvent]): api.GreetingMessageChanged = {
    helloEvent.event match {
      case GreetingMessageChanged(msg) => api.GreetingMessageChanged(helloEvent.entityId, msg)
    }
  }
}
