package com.inventory.product.api

import play.api.libs.json.{Format, Json}

case class Item (productId : String, description: String)

object Item {
  // activate JSON serialization
  implicit val format: Format[Item] = Json.format[Item]
}