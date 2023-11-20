#!/bin/bash

sed -i 's/Name = "aws"/Name = "aws'$1'"/g' $2
sed -i 's/Name = "customer"/Name = "customer'$1'"/g' $2
sed -i 's/"namu.com"/"namu'$1'.com"/g' $2
