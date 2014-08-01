#!/bin/bash

#output file for the protobuf compiler
JAVA_PROTO_FILE=src/com/google/apphosting/api/IotsysServicePb.java

#add the protoc libraries to the path
LD_LIBRARY_PATH=$LD_LIBRARY_PATH:/usr/local/lib/ 

#compile the protobuf source into a java class
protoc --java_out=src/ iotsysservice.proto

#replace all occurrences of "com.google.protobuf" with "com.google.appengine.repackaged.com.google.protobuf"
#to match the repacked protobuf library used in the google testserver and so also in the appscale server
sed -i -e 's/com.google.protobuf/com.google.appengine.repackaged.com.google.protobuf/g' $JAVA_PROTO_FILE
