package com.inventory.customer.api

import play.api.libs.json.{Format, Json}

case class CustomerName (lastName: String, firstName: String)

object CustomerName {
  // activate JSON serialization
  implicit val format: Format[CustomerName] = Json.format[CustomerName]
}