#!/bin/bash

cd ~
if [ ! -d mcmp ]; then
  mkdir mcmp
  echo "Create mcmp dir"
fi

export BASE_DIR=~/mcmp
export BUILD_DIR=~/mcmp/build

cd -