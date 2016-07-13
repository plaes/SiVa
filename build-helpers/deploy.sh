#!/usr/bin/env bash

scp -i ci-deploy-key artifacts/distribution.zip ci-deploy@demoapps.eu:/home/ci-deploy
ssh -i ci-deploy-key ci-deploy@demoapps.eu "bash -s" < ./build-helpers/deploy-setup.sh