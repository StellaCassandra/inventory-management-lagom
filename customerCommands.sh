RegisterNewCustomer=$(cat <<EOF
{"customerId": "Cassandra",
"customerName" : {"lastName" : "Tasai", "firstName" : "Cassandra"},
"customerAddress" : {"street" : "Am Lindenbaum", "streetNumber" : "1", "postalCode" : "1323", "city": "Augsburg"}
}
EOF
)

RetrieveCustomer="Cassandra"

alias registerNewCustomer='curl -H "Content-Type: application/json" -X POST -d "$RegisterNewCustomer" http://localhost:9000/api/customers'
alias retrieveCustomer='curl -H "Content-Type: application/json" -X GET http://localhost:9000/api/customers/$RetrieveCustomer'
alias relocateCustomer='curl -H "Content-Type: application/json" -X POST http://localhost:9000/api/customers/$RetrieveCustomer/relocate'
alias nameChangeCustomer='curl -H "Content-Type: application/json" -X POST http://localhost:9000/api/customers/$RetrieveCustomer/name-change'
alias freezeCustomerAccount='curl -H "Content-Type: application/json" -X GET http://localhost:9000/api/customers/$RetrieveCustomer/freeze-account'