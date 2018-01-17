package com.inventory.purchase.api

import play.api.libs.json.{Format, Json}

case class OrderSummary(orderId: String, itemTotal: Int)

object OrderSummary {
  // activate JSON serialization
  implicit val format: Format[OrderSummary] = Json.format
}