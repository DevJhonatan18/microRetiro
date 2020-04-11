#!/bin/bash

curl -X POST "$1:$2/v1/depositEvent" -H 'Content-Type:application/json' --data '{"amount": 3000 , "type":"retiro" , "creationDate": "123" , "accountId": 1}'

