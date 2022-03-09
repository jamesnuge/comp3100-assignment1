#!/bin/bash

git clone https://github.com/distsys-MQ/ds-sim
cd ds-sim/src
make
cp ds-server ../../server
cd -
rm -rf ./ds-sim
