package com.inventory.customer.api

import play.api.libs.json.{Format, Json}

case class Customer (customerId: String, customerName: CustomerName, customerAddress: CustomerAddress)

object Customer {
  // activate JSON serialization
  implicit val format: Format[Customer] = Json.format[Customer]
}