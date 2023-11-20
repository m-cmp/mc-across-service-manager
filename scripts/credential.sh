#!/bin/bash

#CSP_Credential(Terraform)

#####################################
############## Example ##############
#####################################
#AWS_ACCESS_KEY_ID=ABCDEFGBLABLA
#AWS_SECRET_ACCESS_KEY=ABCDEFGBLABLA
#ARM_SUBSCRIPTION_ID=123456789abcd
#ARM_TENANT_ID=123123123abcd
#ARM_CLIENT_ID=11111111abcd
#ARM_CLIENT_SECRET=_ABCDEFGBLABLA
#GOOGLE_CLOUD_KEYFILE_JSON=$BASE_DIR/terraform/source/gcp/credentials.json	##JSON Key File

export AWS_ACCESS_KEY_ID={USER INPUT}
export AWS_SECRET_ACCESS_KEY={USER INPUT}
export ARM_SUBSCRIPTION_ID={USER INPUT}
export ARM_TENANT_ID={USER INPUT}
export ARM_CLIENT_ID={USER INPUT}
export ARM_CLIENT_SECRET={USER INPUT}
export GOOGLE_CLOUD_KEYFILE_JSON={USER INPUT} 

echo "export AWS_ACCESS_KEY_ID=$AWS_ACCESS_KEY_ID" | sudo tee -a "$HOME"/.bashrc
echo "export AWS_SECRET_ACCESS_KEY=$AWS_SECRET_ACCESS_KEY" | sudo tee -a "$HOME"/.bashrc
echo "export ARM_SUBSCRIPTION_ID=$ARM_SUBSCRIPTION_ID" | sudo tee -a "$HOME"/.bashrc
echo "export ARM_TENANT_ID=$ARM_TENANT_ID" | sudo tee -a "$HOME"/.bashrc
echo "export ARM_CLIENT_ID=$ARM_CLIENT_ID" | sudo tee -a "$HOME"/.bashrc
echo "export ARM_CLIENT_SECRET=$ARM_CLIENT_SECRET" | sudo tee -a "$HOME"/.bashrc
echo "export GOOGLE_CLOUD_KEYFILE_JSON=$GOOGLE_CLOUD_KEYFILE_JSON" | sudo tee -a "$HOME"/.bashrc
