package com.inventory.customer.api

import play.api.libs.json.{Format, Json}

case class CustomerAccountStatus(isSolvent : Boolean)

object CustomerAccountStatus {
  // activate JSON serialization
  implicit val format: Format[CustomerAccountStatus] = Json.format[CustomerAccountStatus]
}