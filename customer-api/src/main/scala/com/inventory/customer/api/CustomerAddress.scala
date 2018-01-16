package com.inventory.customer.api

import play.api.libs.json.{Format, Json}

case class CustomerAddress (street: String, streetNumber: String, postalCode: String, city: String)

object CustomerAddress {
  // activate JSON serialization
  implicit val format: Format[CustomerAddress] = Json.format[CustomerAddress]
}