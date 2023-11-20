#!/bin/bash

# env variable
if [ -z "$(env | grep ANSIBLE_CONFIG)" ]; then
  echo "SET ENV"
  cat $(pwd)/.env | sudo tee -a $HOME/.bashrc
fi

source $HOME/.bashrc
