#!/bin/bash
alias addProducts='curl -H "Content-Type: application/json" -X POST -d "$ProductIds" http://localhost:9000/api/purchases/"$CustomerId"/add-products'
alias removeProducts='curl -H "Content-Type: application/json" -X POST -d "$ProductIds" http://localhost:9000/api/purchases/"$CustomerId"/remove-products'
alias issuePurchaseOrder='curl -H "Content-Type: application/json" -X GET http://localhost:9000/api/purchases/"$CustomerId"/issue-purchase-order'
alias cancelPurchaseOrder='curl -H "Content-Type: application/json" -X GET http://localhost:9000/api/purchases/"$CustomerId"/cancel-purchase-order'
alias retrievePurchaseOrders='curl -H "Content-Type: application/json" -X GET http://localhost:9000/api/purchases/"$CustomerId"'

CustomerId="Cassandra"
ProductIds='{"quantity" : {"ToyRobot" : 1, "CompSci" : 1, "ToyTrain" : 2}}'
addProducts

ProductIds='{"quantity" : {"ToyTrain" : 1}}'
removeProducts

issuePurchaseOrder
retrievePurchaseOrders
# [] due to eventual consistency
# [{"orderId":"0","itemTotal":3}] soon after

CustomerId="Ruby"
addProducts
issuePurchaseOrder
#{"name":"Forbidden","detail":"The account of the customer Ruby has been frozen. Contact customer support."}
cancelPurchaseOrder

CustomerId="Seraphina"
addProducts
issuePurchaseOrder
addProducts
issuePurchaseOrder
ProductIds='{"quantity" : {"ToyRobot" : 2, "CompSci" : 2, "ToyTrain" : 2}}'
addProducts
issuePurchaseOrder
retrievePurchaseOrders
#[{"orderId":"0","itemTotal":1},{"orderId":"1","itemTotal":1},{"orderId":"2","itemTotal":6}]