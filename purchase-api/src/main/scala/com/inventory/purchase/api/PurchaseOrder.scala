package com.inventory.purchase.api

import play.api.libs.json.{Format, Json}

case class PurchaseOrder(orderId: String, quantity: Map[String, Int] = Map.empty)

object PurchaseOrder {
  // activate JSON serialization
  implicit val format: Format[PurchaseOrder] = Json.format
}