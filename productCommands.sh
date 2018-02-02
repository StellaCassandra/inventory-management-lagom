#!/bin/bash
function registerNewProduct()
{
curl -H "Content-Type: application/json" -X POST -d "$@" http://localhost:9000/api/products
}

alias retrieveProduct='curl -H "Content-Type: application/json" -X GET http://localhost:9000/api/products/"$ProductId"'

ProductTrain=$(cat <<EOF
{"productId": "ToyTrain",
"description" : "A replica of an ICE3."
}
EOF
)

ProductRobot=$(cat <<EOF
{"productId": "ToyRobot",
"description" : "A replica of an industrial robot."
}
EOF
)

ProductCompSci=$(cat <<EOF
{"productId": "CompSci",
"description" : "A series of books on computer science."
}
EOF
)

registerNewProduct "$ProductTrain"
registerNewProduct "$ProductRobot"
registerNewProduct "$ProductCompSci"

ProductId="ToyRobot"
retrieveProduct
