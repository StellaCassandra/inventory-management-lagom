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

CustomerId="Ruby"
addProducts
issuePurchaseOrder
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