#!/bin/bash

sed -i 's/Name = "cgw"/Name = "cgw'$1'"/g' $2
sed -i 's/Name = "test"/Name = "test'$1'"/g' $2

