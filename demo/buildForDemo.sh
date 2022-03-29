git clone https://github.com/distsys-MQ/ds-sim
mv ./ds-sim/src/pre-compiled/ds-server .
mv ./ds-sim/src/pre-compiled/ds-client .
rm -rf ds-sim
cd ../
gradle build
cd demo
cp ../build/libs/assignment-1.jar .