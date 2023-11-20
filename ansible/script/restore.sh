#!/bin/sh
mongorestore -h 127.0.0.1 --port=27017 -u 아이디 -p 비밀번호 --db 데이터베이스 --drop /root/mongo_backup/백업경로/

