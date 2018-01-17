package com.inventory.purchase.api

import play.api.libs.json.{Format, Json}

case class ProductAction (quantity: Map[String, Int]){
  def isInvalid : Boolean = quantity.filter(kv => kv._2 < 0).nonEmpty
}

object ProductAction {
  // activate JSON serialization
  implicit val format: Format[ProductAction] = Json.format
}