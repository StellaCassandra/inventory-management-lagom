#!/bin/bash
alias availableServices='curl -H "Content-Type: application/json" -X GET http://localhost:9008/services'

function registerNewCustomer()
{
curl -H "Content-Type: application/json" -X POST -d "$@" http://localhost:9000/api/customers
}

alias retrieveCustomer='curl -H "Content-Type: application/json" -X GET http://localhost:9000/api/customers/"$CustomerId"'

function relocateCustomer()
{
curl -H "Content-Type: application/json" -X POST -d "$@" http://localhost:9000/api/customers/"$CustomerId"/relocate
}

function nameChangeCustomer()0
{
curl -H "Content-Type: application/json" -X POST -d "$@" http://localhost:9000/api/customers/"$CustomerId"/name-change
}

alias freezeCustomerAccount='curl -H "Content-Type: application/json" -X GET http://localhost:9000/api/customers/"$CustomerId"/freeze-account'
alias retrieveAccountStatus='curl -H "Content-Type: application/json" -X GET http://localhost:9000/api/customers/"$CustomerId"/account-status'

CustomerCassandra=$(cat <<EOF
{"customerId": "Cassandra",
"customerName" : {"lastName" : "Tasai", "firstName" : "Cassandra"},
"customerAddress" : {"street" : "Am Lindenbaum", "streetNumber" : "1", "postalCode" : "86150", "city": "Augsburg"}
}
EOF
)

CustomerSeraphina=$(cat <<EOF
{"customerId": "Seraphina",
"customerName" : {"lastName" : "Tasai", "firstName" : "Seraphina"},
"customerAddress" : {"street" : "Am Neumond", "streetNumber" : "12", "postalCode" : "80331", "city": "Munich"}
}
EOF
)

CustomerRuby=$(cat <<EOF
{"customerId": "Ruby",
"customerName" : {"lastName" : "Tasai", "firstName" : "Ruby"},
"customerAddress" : {"street" : "Am Königsplatz", "streetNumber" : "5b", "postalCode" : "10115", "city": "Berlin"}
}
EOF
)

availableServices
# purchase-service, product-service, kafka_native, customer-service, cas_native

registerNewCustomer "$CustomerCassandra"
registerNewCustomer "$CustomerSeraphina"
registerNewCustomer "$CustomerRuby"

CustomerId="Cassandra"
newAddress=$(cat <<EOF
{"street" : "Am Tannenbaum", "streetNumber" : "5c", "postalCode" : "86150", "city": "Augsburg"}
EOF
)

newName=$(cat <<EOF
{"lastName" : "Tasai", "firstName" : "Cassandra Stella"}
EOF
)

retrieveCustomer
relocateCustomer "$newAddress"
nameChangeCustomer "$newName"
retrieveCustomer
# {"customerId":"Cassandra","customerName":{"lastName":"Tasai","firstName":"Cassandra Stella"},
# "customerAddress":{"street":"Am Tannenbaum","streetNumber":"5c","postalCode":"86150","city":"Augsburg"}}

CustomerId="Ruby"
retrieveAccountStatus
# {"isSolvent":true}
freezeCustomerAccount
retrieveAccountStatus
# {"isSolvent":false}
