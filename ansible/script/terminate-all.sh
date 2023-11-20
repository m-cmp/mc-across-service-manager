#!/bin/bash

#terminate web
pm2 kill

#terminate db
sudo service mongod stop
